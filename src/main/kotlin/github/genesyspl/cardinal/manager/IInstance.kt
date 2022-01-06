package github.genesyspl.cardinal.manager

interface IInstance<T> {
    fun createInstance(): T

    fun getInstance(): T

}