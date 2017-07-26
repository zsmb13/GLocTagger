package photos;


import kotlin.Pair;
import location.finders.LocationFinder;
import org.jetbrains.annotations.Nullable;

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
            @Nullable Pair<Double, Double> latlong = lf.getLocation(timeMS);
            if (latlong == null) {
                // TODO handle gracefully (skip?)
                throw new RuntimeException("No timestamp found for a photo");
            }
            p.writeExifLocation(latlong.getFirst(), latlong.getSecond());
        }
    }

}
