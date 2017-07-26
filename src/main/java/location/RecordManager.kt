package location

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import location.databinding.PlainRoot
import location.filters.RecordFilter
import java.io.File
import java.util.*

/**
 * Reads location records from file, stores them, provides lookup access to get records
 */
class RecordManager @JvmOverloads constructor(locationFile: File, private val filter: RecordFilter? = null) {

    private val records = ArrayList<LocationRecord>()

    init {
        loadRecords(locationFile)
    }

    /**
     * Loads the records from the given JSON file, applying the stored filter to them
     *
     * @param locationFile the file to read records from
     */
    private fun loadRecords(locationFile: File) {
        if (!locationFile.exists() || !locationFile.isFile) {
            throw RuntimeException("Records can't be located, check your records file.")
        }

        val mapper = ObjectMapper().apply {
            // Ignore properties that are not in PlainRecordObject
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }

        // Read records from JSON file
        val root = mapper.readValue(locationFile, PlainRoot::class.java)
        val locations = root.locations ?: throw RuntimeException("No locations found in JSON file")

        println("Successfully read " + locations.size + " records from the JSON location data file.")

        // Create filterable records
        val locationRecords = locations.map(LocationRecord.Companion::from)

        if (filter == null) {
            records.addAll(locationRecords)
        }
        else {
            locationRecords.filterTo(records, filter::accept)
        }

        println("Number of records after filtering: " + records.size)

        // Necessary for the binary search that's used later
        records.sort()
    }

    /**
     * Gets the (up to two) closest records to a given timestamp
     */
    fun getClosestRecords(timeMS: Long): List<LocationRecord> {
        val closest = mutableListOf<LocationRecord>()

        val result = records.binarySearch { it.timeStampMS.compareTo(timeMS) }

        // There was a single exact match
        if (result >= 0) {
            closest.add(records[result])
        }
        else {
            val insertionPoint = -(result + 1)

            // The insertion point returned was not the first index
            // This adds the record with the timestamp before the given one
            if (insertionPoint > 0) {
                closest.add(records[insertionPoint - 1])
            }

            // The insertion point was an index that's still in the list
            // This adds the record with the timestamp after the given one
            if (insertionPoint < records.size) {
                closest.add(records[insertionPoint])
            }
        }

        return closest
    }

}
