package location.filters;

import location.LocationRecord;

/**
 * Abstract base class for filters, responsible for appending filters
 */
public abstract class RecordFilter {

    protected RecordFilter nextFilter = null;

    public final boolean accept(LocationRecord record) {
        if (nextFilter == null) {
            return test(record);
        }
        else {
            return test(record) && nextFilter.accept(record);
        }
    }

    protected abstract boolean test(LocationRecord record);

    public final RecordFilter append(RecordFilter filter) {
        filter.nextFilter = this;
        return filter;
    }

}
