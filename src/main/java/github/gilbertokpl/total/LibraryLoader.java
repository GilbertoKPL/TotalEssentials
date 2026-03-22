package github.gilbertokpl.total;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class LibraryLoader {

    private static final String MAVEN_CENTRAL = "https://repo1.maven.org/maven2";
    private static final String JITPACK = "https://jitpack.io";
    private static final String DV8TION = "https://m2.dv8tion.net/releases";

    private final Logger logger;
    private final Path libFolder;
    private final ClassLoader classLoader;
    private Method addURLMethod;

    public LibraryLoader(Logger logger, Path dataFolder, ClassLoader classLoader) {
        this.logger = logger;
        this.libFolder = dataFolder.resolve("lib");
        this.classLoader = classLoader;
    }

    public boolean loadAll() {
        try {
            Files.createDirectories(libFolder);
        } catch (IOException e) {
            logger.severe("Falha ao criar pasta lib: " + e.getMessage());
            return false;
        }

        if (!(classLoader instanceof URLClassLoader)) {
            logger.severe("ClassLoader incompativel! Nao e URLClassLoader.");
            return false;
        }

        if (!setupReflection()) {
            logger.severe("Falha ao configurar reflection para injecao de libs.");
            return false;
        }

        boolean allLoaded = true;
        List<Dependency> deps = getDependencies();

        for (Dependency dep : deps) {
            try {
                loadDependency(dep);
            } catch (Exception e) {
                logger.severe("Falha ao carregar dependencia " + dep.artifactId + "-" + dep.version + ": " + e.getMessage());
                allLoaded = false;
            }
        }

        if (allLoaded) {
            logger.info("Todas as " + deps.size() + " dependencias foram carregadas com sucesso!");
        }

        return allLoaded;
    }

    private void loadDependency(Dependency dep) throws Exception {
        String fileName = dep.artifactId + "-" + dep.version + ".jar";
        Path jarPath = libFolder.resolve(fileName);

        if (!Files.exists(jarPath)) {
            String url = buildMavenUrl(dep);
            logger.info("Baixando: " + dep.artifactId + "-" + dep.version);

            boolean downloaded = download(url, jarPath);
            if (!downloaded) {
                throw new IOException("Falha no download de " + url);
            }
        }

        addToClasspath(jarPath.toUri().toURL());
    }

    private String buildMavenUrl(Dependency dep) {
        return dep.repoUrl + "/"
                + dep.groupId.replace('.', '/') + "/"
                + dep.artifactId + "/"
                + dep.version + "/"
                + dep.artifactId + "-" + dep.version + ".jar";
    }

    private boolean download(String urlStr, Path target) {
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                URL url = new URL(urlStr);
                URLConnection conn = url.openConnection();
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(30000);
                conn.setRequestProperty("User-Agent", "TotalEssentials");

                try (InputStream in = conn.getInputStream()) {
                    Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
                }
                return true;
            } catch (IOException e) {
                if (attempt < 3) {
                    logger.warning("Tentativa " + attempt + " falhou para " + urlStr + ", tentando novamente...");
                    try {
                        Thread.sleep(attempt * 2000L);
                    } catch (InterruptedException ignored) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    logger.severe("Download falhou apos 3 tentativas: " + urlStr);
                    // Remove partial file
                    try {
                        Files.deleteIfExists(target);
                    } catch (IOException ignored) {
                    }
                }
            }
        }
        return false;
    }

    private boolean setupReflection() {
        try {
            addURLMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);

            // Try simple setAccessible (Java 8-15)
            try {
                addURLMethod.setAccessible(true);
                return true;
            } catch (Exception ignored) {
            }

            // Java 16+: Use Unsafe to bypass module restrictions
            try {
                Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
                Field theUnsafe = unsafeClass.getDeclaredField("theUnsafe");
                theUnsafe.setAccessible(true);
                Object unsafe = theUnsafe.get(null);

                Method objectFieldOffset = unsafeClass.getMethod("objectFieldOffset", Field.class);
                Method putBoolean = unsafeClass.getMethod("putBoolean", Object.class, long.class, boolean.class);

                Field overrideField = AccessibleObject.class.getDeclaredField("override");
                long offset = (long) objectFieldOffset.invoke(unsafe, overrideField);
                putBoolean.invoke(unsafe, addURLMethod, offset, true);
                return true;
            } catch (Exception e) {
                logger.severe("Unsafe bypass falhou: " + e.getMessage());
            }

            return false;
        } catch (NoSuchMethodException e) {
            logger.severe("URLClassLoader.addURL nao encontrado!");
            return false;
        }
    }

    private void addToClasspath(URL url) throws Exception {
        addURLMethod.invoke(classLoader, url);
    }

    public void cleanOldLibs() {
        File libDir = libFolder.toFile();
        if (!libDir.exists()) return;

        File[] files = libDir.listFiles((dir, name) -> name.endsWith(".jar"));
        if (files == null) return;

        List<String> expectedNames = new ArrayList<>();
        for (Dependency dep : getDependencies()) {
            expectedNames.add(dep.artifactId + "-" + dep.version + ".jar");
        }

        // Also keep the old TotalEssentials-lib JARs for backward compat during transition
        for (File file : files) {
            if (!expectedNames.contains(file.getName()) && !file.getName().startsWith("TotalEssentials-lib-")) {
                logger.info("Removendo lib antiga: " + file.getName());
                file.delete();
            }
        }
    }

    private static List<Dependency> getDependencies() {
        List<Dependency> deps = new ArrayList<>();

        // ===== Kotlin =====
        deps.add(dep("org.jetbrains.kotlin", "kotlin-stdlib", "2.3.20", MAVEN_CENTRAL));
        deps.add(dep("org.jetbrains.kotlin", "kotlin-stdlib-jdk7", "2.3.20", MAVEN_CENTRAL));
        deps.add(dep("org.jetbrains.kotlin", "kotlin-stdlib-jdk8", "2.3.20", MAVEN_CENTRAL));
        deps.add(dep("org.jetbrains", "annotations", "26.0.2", MAVEN_CENTRAL));

        // ===== Kotlinx Coroutines (Exposed dependency) =====
        deps.add(dep("org.jetbrains.kotlinx", "kotlinx-coroutines-core-jvm", "1.10.2", MAVEN_CENTRAL));

        // ===== Exposed ORM =====
        deps.add(dep("org.jetbrains.exposed", "exposed-core", "1.1.1", MAVEN_CENTRAL));
        deps.add(dep("org.jetbrains.exposed", "exposed-dao", "1.1.1", MAVEN_CENTRAL));
        deps.add(dep("org.jetbrains.exposed", "exposed-jdbc", "1.1.1", MAVEN_CENTRAL));

        // ===== H2 Database =====
        deps.add(dep("com.h2database", "h2", "2.2.224", MAVEN_CENTRAL));

        // ===== MariaDB Driver =====
        deps.add(dep("org.mariadb.jdbc", "mariadb-java-client", "3.5.7", MAVEN_CENTRAL));

        // ===== HikariCP =====
        deps.add(dep("com.zaxxer", "HikariCP", "4.0.3", MAVEN_CENTRAL));

        // ===== SLF4J =====
        deps.add(dep("org.slf4j", "slf4j-api", "2.0.17", MAVEN_CENTRAL));
        deps.add(dep("org.slf4j", "slf4j-nop", "2.0.17", MAVEN_CENTRAL));

        // ===== Simple YAML =====
        deps.add(dep("me.carleslc.Simple-YAML", "Simple-Yaml", "1.7.3", JITPACK));

        // ===== OSHI (Host Info) =====
        deps.add(dep("com.github.oshi", "oshi-core", "6.9.3", MAVEN_CENTRAL));
        deps.add(dep("net.java.dev.jna", "jna", "5.15.0", MAVEN_CENTRAL));
        deps.add(dep("net.java.dev.jna", "jna-platform", "5.15.0", MAVEN_CENTRAL));

        // ===== JSON =====
        deps.add(dep("org.json", "json", "20250517", MAVEN_CENTRAL));

        // ===== JDA (Discord) =====
        deps.add(dep("net.dv8tion", "JDA", "6.3.2", DV8TION));

        // JDA transitives
        deps.add(dep("com.squareup.okhttp3", "okhttp", "4.12.0", MAVEN_CENTRAL));
        deps.add(dep("com.squareup.okio", "okio-jvm", "3.9.1", MAVEN_CENTRAL));
        deps.add(dep("com.neovisionaries", "nv-websocket-client", "2.14", MAVEN_CENTRAL));
        deps.add(dep("net.sf.trove4j", "trove4j", "3.0.3", MAVEN_CENTRAL));
        deps.add(dep("org.apache.commons", "commons-collections4", "4.4", MAVEN_CENTRAL));
        deps.add(dep("com.fasterxml.jackson.core", "jackson-core", "2.18.3", MAVEN_CENTRAL));
        deps.add(dep("com.fasterxml.jackson.core", "jackson-databind", "2.18.3", MAVEN_CENTRAL));
        deps.add(dep("com.fasterxml.jackson.core", "jackson-annotations", "2.18.3", MAVEN_CENTRAL));

        // ===== Discord Webhooks =====
        deps.add(dep("club.minnced", "discord-webhooks", "0.8.4", MAVEN_CENTRAL));

        return deps;
    }

    private static Dependency dep(String groupId, String artifactId, String version, String repoUrl) {
        return new Dependency(groupId, artifactId, version, repoUrl);
    }

    private static class Dependency {
        final String groupId;
        final String artifactId;
        final String version;
        final String repoUrl;

        Dependency(String groupId, String artifactId, String version, String repoUrl) {
            this.groupId = groupId;
            this.artifactId = artifactId;
            this.version = version;
            this.repoUrl = repoUrl;
        }
    }
}
