package location.filters;

import location.LocationRecord;

/**
 * Created by zsmb on 2016-07-23.
 */
public abstract class RecordFilter {

    protected RecordFilter nextFilter = null;

    public boolean accept(LocationRecord record) {
        if (nextFilter == null) {
            return test(record);
        }
        else {
            return nextFilter.test(record) && test(record);
        }
    }

    protected abstract boolean test(LocationRecord record);

    public final RecordFilter append(RecordFilter filter) {
        filter.nextFilter = this;
        return filter;
    }

}
