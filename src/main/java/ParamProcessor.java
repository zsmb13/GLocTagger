
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
 * Processes the command line arguments given to the program,
 * input/output files, filters, etc.
 */
class ParamProcessor {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private static final long dayMS = 86400000;

    private static File locationData;
    private static File photoInDirectory;
    private static File photoOutDirectory;

    private static RecordFilter filter = null;

    private static TimeZone timeZone;

    /**
     * Parses the command line arguments given to the program
     *
     * @param args the array of arguments
     * @return true if they were valid, false if the program can not possibly continue
     */
    static boolean parse(String[] args) {
        if (args.length < 4) {
            System.err.println("Invalid arguments: at least one of the required arguments is missing.");
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
            System.err.println("Photo output directory is invalid!");
            return false;
        }

        int hourOffset;
        try {
            hourOffset = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid time offset, parse your parameters.");
            return false;
        }

        System.out.println("Required parameters checked, all OK.");

        // Parse optional arguments
        String[] optArgs = new String[args.length - 4];
        System.arraycopy(args, 4, optArgs, 0, args.length - 4);
        parseOptional(optArgs);

        System.out.println("Optional parameters checked.");

        // SET UP TIMEZONE
        String timeZoneString = "GMT" + (hourOffset < 0 ? "" : "+") + hourOffset;
        timeZone = TimeZone.getTimeZone(timeZoneString);
        dateFormat.setTimeZone(timeZone);

        return true;
    }

    /**
     * Parses the given array for optional arguments
     *
     * @param optArgs an array containing the optional arguments from the command line
     */
    private static void parseOptional(String[] optArgs) {
        int i = 0;
        while (i < optArgs.length) {
            switch (optArgs[i]) {
                case "-f":
                case "--from":
                    parseTimeFilter(optArgs[i + 1], false);
                    i += 1;
                    break;
                case "-u":
                case "--until":
                    parseTimeFilter(optArgs[i + 1], true);
                    i += 1;
                    break;
                case "-a":
                case "--accuracy":
                    parseAccuracyFilter(optArgs[i + 1]);
                    i += 1;
                    break;
                case "-r":
                case "--restrict":
                    parseLocationFilter(optArgs[i + 1], optArgs[i + 2], optArgs[i + 3], true);
                    i += 3;
                    break;
                case "-e":
                case "--exclude":
                    parseLocationFilter(optArgs[i + 1], optArgs[i + 2], optArgs[i + 3], false);
                    i += 3;
                    break;
                default:
                    if (optArgs[i].charAt(0) == '-') {
                        System.err.println("Unknown optional switch read, ignoring it.");
                    }
                    else {
                        System.err.println("Unknown optional param read, ignoring it.");
                    }
                    System.err.println("Read string was " + optArgs[i]);
            }

            i++;
        }
    }

    /**
     * Creates a time filter and appends it to the current filters
     *
     * @param arg         the time parameter of the filter
     * @param acceptUntil the before/after parameter of the filter
     */
    private static void parseTimeFilter(String arg, boolean acceptUntil) {
        long timeMS;
        try {
            timeMS = dateFormat.parse(arg).getTime();
        } catch (ParseException e) {
            System.err.println("Time filter can't be added, parse your parameters!");
            return;
        }

        // Make "until" type filter inclusive, accepting until the end of
        // the given day instead of until the beginning
        if (acceptUntil) {
            timeMS += dayMS;
        }

        addFilter(new TimeFilter(timeMS, acceptUntil));
    }

    /**
     * Creates a location filter and appends it to the current filters
     *
     * @param arg1         latitude as a double
     * @param arg2         longitude as a double
     * @param arg3         radius as a double
     * @param acceptInside the inside/outside parameter of the filter
     */
    private static void parseLocationFilter(String arg1, String arg2, String arg3, boolean acceptInside) {
        double lat, lon, rad;
        try {
            lat = Double.parseDouble(arg1);
            lon = Double.parseDouble(arg2);
            rad = Double.parseDouble(arg3);
        } catch (NumberFormatException e) {
            System.err.println("Location filter can't be added, parse your parameters!");
            return;
        }

        addFilter(new LocationFilter(lat, lon, rad, acceptInside));
    }

    /**
     * Creates an accuracy filter and appends it to the current filters
     *
     * @param arg accuracy as an integer
     */
    private static void parseAccuracyFilter(String arg) {
        int accuracy;
        try {
            accuracy = Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            System.err.println("Accuracy filter can't be added, parse your parameters!");
            return;
        }

        addFilter(new AccuracyFilter(accuracy));
    }

    /**
     * Appends the given filter to the current filters, or makes it the
     * current filter, if there aren't any yet
     *
     * @param rf the filter to add
     */
    private static void addFilter(RecordFilter rf) {
        filter = rf.append(filter);
    }

    /**
     * Creates a RecordManager instance based on the parsed parameters
     *
     * @return the RecordManager instance
     */
    static RecordManager getRecordManager() {
        if (filter == null) {
            return new RecordManager(locationData);
        }

        return new RecordManager(locationData, filter);
    }

    /**
     * Creates a PhotoManager instance based on the parsed parameters
     *
     * @return the PhotoManager instance
     */
    static PhotoManager getPhotoManager() {
        return new PhotoManager(photoInDirectory, photoOutDirectory, timeZone);
    }
}
