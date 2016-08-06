package location.filters;

import location.LocationRecord;

/**
 * Filter that accepts records that are of a given or higher accuracy
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
