package location.filters;

import location.LocationRecord;

/**
 * Created by zsmb on 2016-07-25.
 */
public class TimeFilter extends RecordFilter {

    private long timeMS;
    private boolean acceptBefore;

    public TimeFilter(long timeMS, boolean acceptBefore) {
        this.timeMS = timeMS;
        this.acceptBefore = acceptBefore;
    }

    @Override
    protected boolean test(LocationRecord record) {
        return acceptBefore ? (record.getTimeStampMS() <= timeMS) : (record.getTimeStampMS() >= timeMS);
    }
}
