package location.finders


import location.RecordStore
import java.util.*

/**
 * The obvious implementation of the superclass, finds the location
 * record that's closest in time to the given timestamps
 */
class SimpleFinder(recordStore: RecordStore) : LocationFinder(recordStore) {

    /**
     * A list of the millisecond differences that have been used
     * as best results for a query
     */
    private val diffs = ArrayList<Long>()

    override fun getLocation(timeMS: Long): Pair<Double, Double>? {
        val records = recordStore.getClosestRecords(timeMS)

        val bestMatch = records.minBy { Math.abs(it.timeStampMS - timeMS) } ?: return null

        diffs.add(Math.abs(bestMatch.timeStampMS - timeMS))

        return Pair(bestMatch.latitude, bestMatch.longitude)
    }

    /**
     * Prints the average difference between the given time and the timestamp of the best matching record
     */
    override fun printStats() = when {
        diffs.isEmpty() -> println("No accuracy stats to display")
        else -> println("Average time difference of matches: ${diffs.average()} ms")
    }

}
