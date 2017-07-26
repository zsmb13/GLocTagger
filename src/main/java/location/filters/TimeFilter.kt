package location.filters


import location.LocationRecord

/**
 * Filters records before or after a given timestamp
 */
class TimeFilter(private val timeMS: Long, private val acceptBefore: Boolean) : RecordFilter() {

    override fun test(record: LocationRecord) =
            if (acceptBefore)
                record.timeStampMS <= timeMS
            else
                record.timeStampMS >= timeMS

}
