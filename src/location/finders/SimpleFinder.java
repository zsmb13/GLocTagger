package location.finders;

import location.LocationRecord;
import location.RecordManager;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zsmb on 2016-07-23.
 */
public class SimpleFinder extends LocationFinder {

    /**
     * A list of the millisecond differences that have been used
     * as best results for a query
     */
    private List<Long> diffs = new ArrayList<>();

    public SimpleFinder(RecordManager rm) {
        super(rm);
    }

    /**
     * Returns the location for the specified time
     *
     * @param timeMS the time to check
     * @return the location in a two-length array, latitude(0) and longitude(1)
     */
    @Override
    public double[] getLocation(long timeMS) {
        List<LocationRecord> records = rm.getClosestRecords(timeMS);
        if (records.isEmpty()) {
            //TODO error handling
            System.err.println("ERROR NO RECORDS RETURNED FROM GETCLOSESTRECORDS");
        }

        LocationRecord bestMatch = records.get(0);
        long bestDiff = Math.abs(bestMatch.getTimeStampMS() - timeMS);

        for (int i = 1; i < records.size(); i++) {
            LocationRecord lr = records.get(i);
            long diff = Math.abs(lr.getTimeStampMS() - timeMS);

            if (diff < bestDiff) {
                bestMatch = lr;
                bestDiff = diff;
            }
        }

        diffs.add(bestDiff);

        //System.out.println("Best diff was " + bestDiff / 1000.0 / 3600.0 + " hours (" + bestDiff + ")");
        //System.out.println("Record used was " + bestMatch.getTimeStampMS() + " (accuracy: " + bestMatch.getAccuracy() + ")");

        return new double[]{bestMatch.getLatitude(), bestMatch.getLongitude()};
    }

    @Override
    public void printStats() {
        System.out.println("SimpleFinder stats");

        if (diffs.isEmpty()) {
            System.out.println("No stats to display");
            return;
        }

        System.out.println("Average diff: " + getAverage(diffs));
    }

    private String getAverage(List<Long> list) {
        BigInteger sum = BigInteger.ZERO;

        for (Long l : list) {
            sum = sum.add(BigInteger.valueOf(l));
        }

        BigInteger count = BigInteger.valueOf(list.size());

        return sum.divide(count).toString();
    }
}
