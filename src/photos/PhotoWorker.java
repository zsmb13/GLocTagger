package photos;

import location.finders.LocationFinder;

/**
 * Created by zsmb on 2016-07-25.
 */
public class PhotoWorker implements Runnable {

    PhotoManager pm;
    LocationFinder lf;

    public PhotoWorker(PhotoManager pm, LocationFinder lf) {
        this.pm = pm;
        this.lf = lf;
    }

    @Override
    public void run() {
        Photo p;
        while((p = pm.getNext()) != null) {
            long timeMS = p.getTimestampMS();
            double[] latlong = lf.getLocation(timeMS);
            //System.out.println("Writing " + latlong[0] + "," + latlong[1]);
            p.writeExifLocation(latlong[0], latlong[1]);
        }
    }

}
