package location.filters


import location.LocationRecord

/**
 * Abstract base class for filters, responsible for appending filters
 */
abstract class RecordFilter {

    private var nextFilter: RecordFilter? = null

    protected abstract fun test(record: LocationRecord): Boolean

    fun accept(record: LocationRecord): Boolean = test(record) && nextFilter?.accept(record) ?: true

    fun append(filter: RecordFilter?) = apply { nextFilter = filter }

}
