package photos;

import location.finders.LocationFinder;

/**
 * Gets photos from the given PhotoManager, processes them, and writes
 * the results to the output directory
 */
public class PhotoWorker implements Runnable {

    private PhotoManager pm;
    private LocationFinder lf;

    public PhotoWorker(PhotoManager pm, LocationFinder lf) {
        this.pm = pm;
        this.lf = lf;
    }

    /**
     * Gets and processes pictures until there are none left
     */
    @Override
    public void run() {
        Photo p;
        while ((p = pm.getNext()) != null) {
            long timeMS = p.getTimestampMS();
            double[] latlong = lf.getLocation(timeMS);
            p.writeExifLocation(latlong[0], latlong[1]);
        }
    }

}
