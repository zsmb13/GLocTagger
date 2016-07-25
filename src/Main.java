import location.LocationFinder;
import location.RecordManager;
import location.SimpleFinder;
import location.filters.AccuracyFilter;
import location.filters.LocationFilter;

/**
 * Created by zsmb on 2016-07-17.
 */
public class Main {

    public static void main(String[] args) {
        long time = System.currentTimeMillis();

        boolean success = ParamProcessor.check(args);
        if (!success) {
            return;
        }

        RecordManager rm = ParamProcessor.getRecordManager();
        PhotoManager pm = ParamProcessor.getPhotomanager();
        LocationFinder lf = new SimpleFinder(rm);

        while (pm.next()) {
            //System.out.println("---");
            long timeMS = pm.getCurrentTimestampMS();
            double[] latlong = lf.getLocation(timeMS);
            //System.out.println("Writing " + latlong[0] + "," + latlong[1]);
            pm.writeCurrentExifData(latlong[0], latlong[1]);
        }

        lf.printStats();

        System.out.println();
        System.out.println("TIME TAKEN");
        System.out.println(System.currentTimeMillis() - time);
    }

}
