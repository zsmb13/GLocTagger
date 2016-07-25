package location.filters;

import location.LocationRecord;

/**
 * Created by zsmb on 2016-07-23.
 */
public class AccuracyFilter extends RecordFilter {

    private int maxRadius;

    public AccuracyFilter(int maxRadius) {
        this.maxRadius = maxRadius;
    }

    @Override
    protected boolean test(LocationRecord record) {
        return record.getAccuracy() <= maxRadius;
    }
}
