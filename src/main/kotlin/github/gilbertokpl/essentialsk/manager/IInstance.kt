package github.gilbertokpl.essentialsk.manager

interface IInstance<T> {
    fun createInstance(): T

    fun getInstance(): T

}