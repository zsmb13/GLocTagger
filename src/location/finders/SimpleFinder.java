package location.finders;

import location.LocationRecord;
import location.RecordManager;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * The obvious implementation of the superclass, finds the location
 * record that's closest in time to the given timestamps
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
            System.err.println("There were no closest records found for a given timestamp.");
            System.err.println("Check your filters and your JSON data file.");
            return null;
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

    /**
     * Prints the average difference in timestamps between the given time and
     * the timestamp of the found location record for the processed queries
     */
    @Override
    public void printStats() {
        if (diffs.isEmpty()) {
            System.out.println("No accuracy stats to display");
            return;
        }

        System.out.println("Average time difference of matches: " + getAverage(diffs) + " ms");
    }

    /**
     * Returns the average of a list of long values, in an overflow safe way
     *
     * @param list the list to use
     * @return the average of the stored values
     */
    private String getAverage(List<Long> list) {
        BigInteger sum = BigInteger.ZERO;

        for (Long l : list) {
            sum = sum.add(BigInteger.valueOf(l));
        }

        BigInteger count = BigInteger.valueOf(list.size());

        return sum.divide(count).toString();
    }
}
