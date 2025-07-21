package co.infinum.princeofversions

/**
 * This class loads update resource.
 */
interface Loader {
    /**
     * Loads update resource into [String].
     *
     * @return Loaded text.
     * @throws Throwable if error happens during load.
     */
    @Throws(Throwable::class)
    fun load(): String
}
