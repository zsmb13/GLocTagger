package location.finders;

import location.RecordManager;

/**
 * Created by zsmb on 2016-07-23.
 */
public abstract class LocationFinder {

    final RecordManager rm;

    LocationFinder(RecordManager rm) {
        this.rm = rm;
    }

    /**
     * Returns the location for the specified time
     *
     * @param timeMS the time to check
     * @return the location in a two-length array, latitude(0) and longitude(1)
     */
    public abstract double[] getLocation(long timeMS);

    /**
     * Prints statistics about the matches found on the standard output
     */
    public abstract void printStats();
}
