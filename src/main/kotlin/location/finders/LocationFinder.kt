package location.finders


import location.RecordStore

/**
 * Abstract base class of location finders
 * These classes find you a GPS location based on a given timestamp,
 * using a RecordStore to get access to location records
 */
abstract class LocationFinder internal constructor(protected val recordStore: RecordStore) {

    /**
     * Returns the location (lat, long) for the specified time
     */
    abstract fun getLocation(timeMS: Long): Pair<Double, Double>?

    /**
     * Prints statistics about the matches found on the standard output
     */
    abstract fun printStats()

}
