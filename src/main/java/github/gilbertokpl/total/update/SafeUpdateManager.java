package github.gilbertokpl.total.update;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Checks GitHub releases without unloading a running plugin.
 * Bukkit uses its update directory; proxies replace the on-disk jar while the
 * already loaded classes remain active until the next normal restart.
 */
public final class SafeUpdateManager implements AutoCloseable {

    public enum Platform {
        BUKKIT,
        BUNGEE,
        VELOCITY
    }

    public interface UpdateLogger {
        void info(String message);

        void warning(String message, Throwable error);
    }

    private static final String RELEASE_API =
            "https://api.github.com/repos/GilbertoKPL/TotalEssentials/releases/latest";
    private static final Pattern TAG_PATTERN = Pattern.compile(
            "\\\"tag_name\\\"\\s*:\\s*\\\"([^\\\"]+)\\\""
    );
    private static final Pattern DOWNLOAD_PATTERN = Pattern.compile(
            "\\\"browser_download_url\\\"\\s*:\\s*\\\"([^\\\"]+\\.jar)\\\"",
            Pattern.CASE_INSENSITIVE
    );
    private static final Pattern PLUGIN_VERSION_PATTERN = Pattern.compile(
            "(?m)^version\\s*:\\s*[\\\"']?([^\\s\\\"']+)"
    );
    private static final long MAX_DOWNLOAD_BYTES = 128L * 1024L * 1024L;
    private static final long MIN_DOWNLOAD_BYTES = 1024L;

    private final Platform platform;
    private final String currentVersion;
    private final Path dataDirectory;
    private final Path sourceJar;
    private final int intervalMinutes;
    private final UpdateLogger logger;
    private final AtomicBoolean checking = new AtomicBoolean();
    private final AtomicBoolean helperStarted = new AtomicBoolean();
    private ScheduledExecutorService executor;
    private volatile String preparedVersion;

    public SafeUpdateManager(
            Platform platform,
            String currentVersion,
            Class<?> pluginClass,
            Path dataDirectory,
            int intervalMinutes,
            UpdateLogger logger
    ) {
        this.platform = platform;
        this.currentVersion = normalizeVersion(currentVersion);
        this.dataDirectory = dataDirectory.toAbsolutePath().normalize();
        this.sourceJar = findSourceJar(pluginClass);
        this.intervalMinutes = Math.max(1, intervalMinutes);
        this.logger = logger;
    }

    public synchronized void start() {
        if (executor != null) return;

        ThreadFactory threadFactory = runnable -> {
            Thread thread = new Thread(runnable, "TotalEssentials-SafeUpdater");
            thread.setDaemon(true);
            return thread;
        };
        executor = Executors.newSingleThreadScheduledExecutor(threadFactory);
        executor.scheduleWithFixedDelay(this::checkSafely, 0L, intervalMinutes, TimeUnit.MINUTES);
        logger.info("Atualizador seguro iniciado; verificacoes a cada "
                + intervalMinutes + " minutos.");
    }

    private void checkSafely() {
        if (!checking.compareAndSet(false, true)) return;
        try {
            Release release = fetchLatestRelease();
            if (!isNewerVersion(release.version, currentVersion)) return;
            if (release.version.equals(preparedVersion)) return;

            Path stagedJar = downloadAndValidate(release);
            prepareInstallation(stagedJar, release.version);
            preparedVersion = release.version;
        } catch (Exception exception) {
            logger.warning("Nao foi possivel verificar ou preparar a atualizacao do TotalEssentials.", exception);
        } finally {
            checking.set(false);
        }
    }

    private Release fetchLatestRelease() throws IOException {
        HttpURLConnection connection = openConnection(new URL(RELEASE_API));
        connection.setRequestProperty("Accept", "application/vnd.github+json");
        connection.setRequestProperty("User-Agent", "TotalEssentials-SafeUpdater/" + currentVersion);

        int status = connection.getResponseCode();
        if (status != HttpURLConnection.HTTP_OK) {
            connection.disconnect();
            throw new IOException("GitHub respondeu HTTP " + status + " ao consultar a ultima release");
        }

        String json;
        try (InputStream input = limitedInput(connection.getInputStream(), 2L * 1024L * 1024L)) {
            json = readUtf8(input);
        } finally {
            connection.disconnect();
        }

        Matcher tagMatcher = TAG_PATTERN.matcher(json);
        if (!tagMatcher.find()) throw new IOException("A release do GitHub nao possui tag_name");

        String rawTag = unescapeJson(tagMatcher.group(1));
        String version = normalizeVersion(rawTag);
        String expectedName = "TotalEssentials-" + version + ".jar";
        String fallback = null;
        Matcher downloadMatcher = DOWNLOAD_PATTERN.matcher(json);
        while (downloadMatcher.find()) {
            String candidate = unescapeJson(downloadMatcher.group(1));
            String fileName = fileName(candidate);
            if (expectedName.equalsIgnoreCase(fileName)
                    || ("TotalEssentials-" + rawTag + ".jar").equalsIgnoreCase(fileName)) {
                return new Release(version, candidate);
            }
            if (fallback == null
                    && fileName.toLowerCase(Locale.ROOT).startsWith("totalessentials")
                    && fileName.toLowerCase(Locale.ROOT).endsWith(".jar")) {
                fallback = candidate;
            }
        }
        if (fallback != null) return new Release(version, fallback);
        throw new IOException("A release " + rawTag + " nao possui um JAR do TotalEssentials");
    }

    private Path downloadAndValidate(Release release) throws IOException {
        Path updateDirectory = dataDirectory.resolve("update");
        Files.createDirectories(updateDirectory);
        Path partial = updateDirectory.resolve("TotalEssentials.download.part");

        HttpURLConnection connection = openConnection(new URL(release.downloadUrl));
        connection.setRequestProperty("Accept", "application/octet-stream");
        connection.setRequestProperty("User-Agent", "TotalEssentials-SafeUpdater/" + currentVersion);
        int status = connection.getResponseCode();
        if (status != HttpURLConnection.HTTP_OK) {
            connection.disconnect();
            throw new IOException("GitHub respondeu HTTP " + status + " ao baixar " + release.downloadUrl);
        }

        long declaredLength = connection.getContentLengthLong();
        if (declaredLength > MAX_DOWNLOAD_BYTES) {
            connection.disconnect();
            throw new IOException("O JAR anunciado excede o limite de 128 MB");
        }

        try (InputStream input = limitedInput(connection.getInputStream(), MAX_DOWNLOAD_BYTES)) {
            Files.copy(input, partial, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            connection.disconnect();
        }

        long actualLength = Files.size(partial);
        if (actualLength < MIN_DOWNLOAD_BYTES) {
            Files.deleteIfExists(partial);
            throw new IOException("O arquivo baixado e pequeno demais para ser um plugin valido");
        }
        validateJar(partial, release.version);
        return partial;
    }

    private void validateJar(Path jarPath, String releaseVersion) throws IOException {
        try (JarFile jar = new JarFile(jarPath.toFile(), true)) {
            requireEntry(jar, "plugin.yml");
            requireEntry(jar, "bungee.yml");
            requireEntry(jar, "github/gilbertokpl/total/TotalEssentials.class");
            requireEntry(jar, "github/gilbertokpl/total/bungee/TotalEssentialsBungee.class");
            requireEntry(jar, "github/gilbertokpl/total/velocity/TotalEssentialsVelocity.class");

            String pluginYaml = readEntry(jar, "plugin.yml", 64L * 1024L);
            Matcher versionMatcher = PLUGIN_VERSION_PATTERN.matcher(pluginYaml);
            if (!versionMatcher.find()) throw new IOException("plugin.yml sem versao");
            String jarVersion = normalizeVersion(versionMatcher.group(1));
            if (!releaseVersion.equals(jarVersion)) {
                throw new IOException("A tag " + releaseVersion + " nao corresponde ao JAR " + jarVersion);
            }
        } catch (IOException exception) {
            Files.deleteIfExists(jarPath);
            throw exception;
        }
    }

    private void prepareInstallation(Path downloadedJar, String releaseVersion) throws IOException {
        if (platform == Platform.BUKKIT) {
            prepareBukkitUpdate(downloadedJar, releaseVersion);
        } else {
            prepareProxyUpdate(downloadedJar, releaseVersion);
        }
    }

    private void prepareBukkitUpdate(Path downloadedJar, String releaseVersion) throws IOException {
        Path pluginsDirectory = dataDirectory.getParent();
        if (pluginsDirectory == null) throw new IOException("Pasta plugins nao encontrada");

        Path updateDirectory = pluginsDirectory.resolve("update");
        Files.createDirectories(updateDirectory);
        String targetName = sourceJar != null && sourceJar.getFileName().toString().endsWith(".jar")
                ? sourceJar.getFileName().toString()
                : "TotalEssentials.jar";
        atomicMove(downloadedJar, updateDirectory.resolve(targetName));
        logger.info("TotalEssentials " + releaseVersion
                + " validado e colocado na pasta de atualizacao do Bukkit. "
                + "Ele sera ativado no proximo reinicio normal, sem desligamento automatico.");
    }

    private void prepareProxyUpdate(Path downloadedJar, String releaseVersion) throws IOException {
        Path pending = dataDirectory.resolve("update").resolve("TotalEssentials.pending.jar");
        atomicMove(downloadedJar, pending);
        if (sourceJar == null || !sourceJar.getFileName().toString().endsWith(".jar")) {
            throw new IOException("O plugin nao foi carregado de um JAR; atualizacao mantida em " + pending);
        }

        if (isWindows()) {
            startWindowsReplacementHelper(pending, sourceJar);
            logger.info("TotalEssentials " + releaseVersion
                    + " validado. O JAR sera substituido pelo aplicador seguro assim que este "
                    + platformName() + " encerrar normalmente; a versao atual continua ativa ate la.");
            return;
        }

        try {
            atomicMove(pending, sourceJar);
            logger.info("TotalEssentials " + releaseVersion
                    + " instalado no disco. A versao atual continua ativa e a nova sera carregada "
                    + "no proximo reinicio normal do " + platformName() + ".");
        } catch (IOException exception) {
            logger.warning("O sistema bloqueou a troca do JAR ativo. A atualizacao ficou em " + pending
                    + " e deve ser movida para " + sourceJar + " depois que o proxy encerrar.", exception);
        }
    }

    private void startWindowsReplacementHelper(Path pending, Path destination) throws IOException {
        if (!helperStarted.compareAndSet(false, true)) return;
        long pid = currentProcessId();
        if (pid <= 0L) {
            helperStarted.set(false);
            throw new IOException("Nao foi possivel descobrir o PID para iniciar o aplicador do Windows");
        }

        String script = "$p=Get-Process -Id " + pid + " -ErrorAction SilentlyContinue;"
                + "if($p){$p.WaitForExit()};"
                + "for($i=0;$i -lt 120;$i++){try{"
                + "Move-Item -LiteralPath '" + powershellLiteral(pending) + "' -Destination '"
                + powershellLiteral(destination) + "' -Force -ErrorAction Stop;exit 0"
                + "}catch{Start-Sleep -Milliseconds 500}};exit 1";
        String encoded = Base64.getEncoder().encodeToString(script.getBytes(StandardCharsets.UTF_16LE));
        Path helperLog = dataDirectory.resolve("update").resolve("update-helper.log");
        new ProcessBuilder(
                "powershell.exe", "-NoProfile", "-NonInteractive", "-WindowStyle", "Hidden",
                "-EncodedCommand", encoded
        ).redirectErrorStream(true).redirectOutput(helperLog.toFile()).start();
    }

    private static HttpURLConnection openConnection(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(10_000);
        connection.setReadTimeout(30_000);
        connection.setInstanceFollowRedirects(true);
        connection.setUseCaches(false);
        return connection;
    }

    private static InputStream limitedInput(InputStream delegate, long maximumBytes) {
        return new InputStream() {
            private long read;

            @Override
            public int read() throws IOException {
                int value = delegate.read();
                if (value >= 0 && ++read > maximumBytes) throw new IOException("Resposta excedeu o limite");
                return value;
            }

            @Override
            public int read(byte[] bytes, int offset, int length) throws IOException {
                int count = delegate.read(bytes, offset, length);
                if (count > 0 && (read += count) > maximumBytes) throw new IOException("Resposta excedeu o limite");
                return count;
            }

            @Override
            public void close() throws IOException {
                delegate.close();
            }
        };
    }

    private static String readUtf8(InputStream input) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            StringBuilder result = new StringBuilder();
            char[] buffer = new char[4096];
            int count;
            while ((count = reader.read(buffer)) >= 0) result.append(buffer, 0, count);
            return result.toString();
        }
    }

    private static String readEntry(JarFile jar, String name, long maximumBytes) throws IOException {
        JarEntry entry = requireEntry(jar, name);
        try (InputStream input = limitedInput(jar.getInputStream(entry), maximumBytes)) {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int count;
            while ((count = input.read(buffer)) >= 0) output.write(buffer, 0, count);
            return new String(output.toByteArray(), StandardCharsets.UTF_8);
        }
    }

    private static JarEntry requireEntry(JarFile jar, String name) throws IOException {
        JarEntry entry = jar.getJarEntry(name);
        if (entry == null) throw new IOException("JAR sem a entrada obrigatoria " + name);
        return entry;
    }

    private static void atomicMove(Path source, Path destination) throws IOException {
        Path parent = destination.toAbsolutePath().normalize().getParent();
        if (parent != null) Files.createDirectories(parent);
        try {
            Files.move(source, destination, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException ignored) {
            Files.move(source, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static Path findSourceJar(Class<?> pluginClass) {
        try {
            URI uri = pluginClass.getProtectionDomain().getCodeSource().getLocation().toURI();
            Path path = java.nio.file.Paths.get(uri).toAbsolutePath().normalize();
            if (path.getParent() != null
                    && ".paper-remapped".equals(path.getParent().getFileName().toString())
                    && path.getParent().getParent() != null) {
                Path original = path.getParent().getParent().resolve(path.getFileName());
                if (Files.isRegularFile(original)) return original;
            }
            return Files.isRegularFile(path) ? path : null;
        } catch (Exception ignored) {
            return null;
        }
    }

    static boolean isNewerVersion(String candidate, String current) {
        String normalizedCandidate = normalizeVersion(candidate);
        String normalizedCurrent = normalizeVersion(current);
        if (normalizedCandidate.equalsIgnoreCase(normalizedCurrent)) return false;

        VersionParts candidateParts = VersionParts.parse(normalizedCandidate);
        VersionParts currentParts = VersionParts.parse(normalizedCurrent);
        if (candidateParts == null || currentParts == null) return false;
        return candidateParts.compareTo(currentParts) > 0;
    }

    private static String normalizeVersion(String version) {
        String normalized = version == null ? "" : version.trim();
        if (normalized.startsWith("v") || normalized.startsWith("V")) normalized = normalized.substring(1);
        return normalized;
    }

    private static String unescapeJson(String value) {
        return value.replace("\\/", "/").replace("\\u0026", "&");
    }

    private static String fileName(String url) {
        try {
            String path = new URL(url).getPath();
            return URLDecoder.decode(path.substring(path.lastIndexOf('/') + 1), "UTF-8");
        } catch (Exception ignored) {
            return "";
        }
    }

    private static boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win");
    }

    private static long currentProcessId() {
        String runtimeName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
        int separator = runtimeName.indexOf('@');
        try {
            return Long.parseLong(separator < 0 ? runtimeName : runtimeName.substring(0, separator));
        } catch (NumberFormatException ignored) {
            return -1L;
        }
    }

    private static String powershellLiteral(Path path) {
        return path.toAbsolutePath().normalize().toString().replace("'", "''");
    }

    private String platformName() {
        return platform == Platform.BUNGEE ? "BungeeCord" : "Velocity";
    }

    @Override
    public synchronized void close() {
        if (executor == null) return;
        executor.shutdownNow();
        executor = null;
    }

    private static final class Release {
        private final String version;
        private final String downloadUrl;

        private Release(String version, String downloadUrl) {
            this.version = version;
            this.downloadUrl = downloadUrl;
        }
    }

    private static final class VersionParts implements Comparable<VersionParts> {
        private final int[] numbers;
        private final boolean prerelease;

        private VersionParts(int[] numbers, boolean prerelease) {
            this.numbers = numbers;
            this.prerelease = prerelease;
        }

        private static VersionParts parse(String value) {
            String withoutBuild = value.split("\\+", 2)[0];
            String[] releaseAndPre = withoutBuild.split("-", 2);
            String[] tokens = releaseAndPre[0].split("\\.");
            if (tokens.length == 0) return null;
            int[] numbers = new int[tokens.length];
            try {
                for (int i = 0; i < tokens.length; i++) numbers[i] = Integer.parseInt(tokens[i]);
            } catch (NumberFormatException ignored) {
                return null;
            }
            return new VersionParts(numbers, releaseAndPre.length > 1);
        }

        @Override
        public int compareTo(VersionParts other) {
            int length = Math.max(numbers.length, other.numbers.length);
            for (int i = 0; i < length; i++) {
                int left = i < numbers.length ? numbers[i] : 0;
                int right = i < other.numbers.length ? other.numbers[i] : 0;
                if (left != right) return left < right ? -1 : 1;
            }
            if (prerelease == other.prerelease) return 0;
            return prerelease ? -1 : 1;
        }
    }
}
