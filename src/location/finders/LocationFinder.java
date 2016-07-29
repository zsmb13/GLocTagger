package location.finders;

import location.RecordManager;

/**
 * Abstract base class of location finders
 * These classes find you a GPS location based on a given timestamp,
 * using a RecordManager to get access to location records
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
