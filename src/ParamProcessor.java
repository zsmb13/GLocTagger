import location.RecordManager;
import location.filters.AccuracyFilter;
import location.filters.LocationFilter;
import location.filters.RecordFilter;
import location.filters.TimeFilter;
import photos.PhotoManager;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Created by zsmb on 2016-07-18.
 */
public class ParamProcessor {

    private static File locationData;
    private static File photoInDirectory;
    private static File photoOutDirectory;
    private static int hourOffset;

    private static boolean initialized = false;

    private static RecordFilter filter = null;

    private static TimeZone timeZone;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static boolean check(String[] args) {
        // TODO add switches for all these options
        // TODO add UTC offset option

        if (args.length < 4) {
            System.err.println("Invalid arguments.");
            return false;
        }

        locationData = new File(args[0]);
        photoInDirectory = new File(args[1]);
        photoOutDirectory = new File(args[2]);

        // Location data file checks
        if (!locationData.exists()) {
            System.err.println("Location data file does not exist!");
            return false;
        }

        // Photo directory checks
        if (!photoInDirectory.exists() || !photoInDirectory.isDirectory()) {
            System.err.println("Photo input directory does not exist!");
            return false;
        }

        // Output directory checks
        if (!photoOutDirectory.exists()) {
            // Create directory if necessary
            return photoOutDirectory.mkdirs();
        }
        if (!photoOutDirectory.isDirectory()) {
            System.err.println("Output directory is invalid!");
            return false;
        }

        try {
            hourOffset = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            //TODO error handling
            System.err.println("Invalid time offset");
            e.printStackTrace();
        }

        System.out.println("Param check OK");

        initialized = true;

        String[] optArgs = new String[args.length - 4];
        for(int i = 0; i < args.length - 4; i++) {
            optArgs[i] = args[i + 4];
        }
        parse(optArgs);


        // SET UP TIMEZONE
        String timeZoneString = "GMT" + (hourOffset < 0 ? "" : "+") + hourOffset;
        System.out.println(timeZoneString);
        timeZone = TimeZone.getTimeZone(timeZoneString);
        dateFormat.setTimeZone(timeZone);

        return true;
    }

    private static void parse(String[] optArgs) {
        int i = 0;
        while(i < optArgs.length) {
            switch(optArgs[i]) {
                case "-from":
                    parseTimeFilter(optArgs[i+1], false);
                    i += 1;
                    break;
                case "-until":
                    parseTimeFilter(optArgs[i+1], true);
                    i += 1;
                    break;
                case "-accuracy":
                    parseAccuracyFilter(optArgs[i+1]);
                    i += 1;
                    break;
                case "-restrict":
                    parseLocationFilter(optArgs[i+1], optArgs[i+2], optArgs[i+3], true);
                    i += 3;
                    break;
                case "-exclude":
                    parseLocationFilter(optArgs[i+1], optArgs[i+2], optArgs[i+3], false);
                    i += 3;
                    break;
                default:
                    //TODO error handling
                    System.err.println("Unknown switch read, throwing it away...");
            }

            i++;
        }
    }

    private static final long dayMS = 86400000;

    private static void parseTimeFilter(String arg, boolean acceptUntil) {
        long timeMS;
        try {
            timeMS = dateFormat.parse(arg).getTime();
        } catch (ParseException e) {
            // TODO error handling
            e.printStackTrace();
            return;
        }

        // Make "until" type filter inclusive, accepting until the end of
        // the given day instead of until the beginning
        if(acceptUntil) {
            timeMS += dayMS;
        }

        addFilter(new TimeFilter(timeMS, acceptUntil));
    }

    private static void parseLocationFilter(String arg1, String arg2, String arg3, boolean acceptInside) {
        double lat, lon, rad;
        try {
            lat = Double.parseDouble(arg1);
            lon = Double.parseDouble(arg2);
            rad = Double.parseDouble(arg3);
        } catch(NumberFormatException e) {
            //TODO error handling
            e.printStackTrace();
            return;
        }

        addFilter(new LocationFilter(lat, lon, rad, acceptInside));
    }

    private static void parseAccuracyFilter(String arg) {
        int accuracy;
        try {
            accuracy = Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            //TODO error handling
            e.printStackTrace();
            return;
        }

        addFilter(new AccuracyFilter(accuracy));
    }

    private static void addFilter(RecordFilter rf) {
        if(filter == null) {
            filter = rf;
        }
        else {
            filter = filter.append(rf);
        }
    }

    public static RecordManager getRecordManager() {
        if (!initialized) {
            System.err.println("CANT GET RECORD MANAGER YET");
            //TODO error handling
        }

        if(filter == null) {
            return new RecordManager(locationData);
        }

        return new RecordManager(locationData, filter);
    }

    public static PhotoManager getPhotomanager() {
        if (!initialized) {
            System.err.println("CANT GET PHOTO MANAGER YET");
            //TODO error handling
        }
        return new PhotoManager(photoInDirectory, photoOutDirectory, timeZone);
    }
}
