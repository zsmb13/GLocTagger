package location.finders;

/**
 * Created by zsmb on 2016-07-23.
 */
public interface LocationFinder {

    /**
     * Returns the location for the specified time
     *
     * @param timeMS the time to check
     * @return the location in a two-length array, latitude(0) and longitude(1)
     */
    public double[] getLocation(long timeMS);

    void printStats();
}
