package location.filters


import location.LocationRecord

/**
 * Filter that accepts records that are of a given or higher accuracy
 */
class AccuracyFilter(private val maxRadius: Int) : RecordFilter() {

    override fun test(record: LocationRecord) = record.accuracy <= maxRadius

}
