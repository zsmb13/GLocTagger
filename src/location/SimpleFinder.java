package location;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zsmb on 2016-07-23.
 */
public class SimpleFinder implements LocationFinder {

    private List<Long> diffs = new ArrayList<>();
    private RecordManager rm;

    public SimpleFinder(RecordManager rm) {
        this.rm = rm;
    }

    /**
     * Returns the difference between two long integers
     *
     * @param a first number
     * @param b second number
     * @return the difference (its absolute value)
     */
    private long getDiff(long a, long b) {
        if (a > b) {
            return getDiff(b, a);
        }

        return b - a;
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
        long bestDiff = getDiff(bestMatch.getTimeStampMS(), timeMS);

        for (int i = 1; i < records.size(); i++) {
            LocationRecord lr = records.get(i);
            long diff = getDiff(lr.getTimeStampMS(), timeMS);

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

        /*System.out.println("---");
        System.out.println("Diffs");
        for (Long l : diffs) {
            System.out.println(l);
        }*/
        System.out.println("---");
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
