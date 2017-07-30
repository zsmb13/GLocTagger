import location.RecordManager
import location.filters.AccuracyFilter
import location.filters.LocationFilter
import location.filters.RecordFilter
import location.filters.TimeFilter
import photos.PhotoManager
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Processes the command line arguments given to the program,
 * input/output files, filters, etc.
 */
class ParamProcessor {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd")

    private val dayMS: Long = 86400000

    private lateinit var locationData: File
    private lateinit var photoInDirectory: File
    private lateinit var photoOutDirectory: File

    private var filter: RecordFilter? = null

    private lateinit var timeZone: TimeZone

    /**
     * Parses the command line arguments given to the program
     *
     * @param args the array of arguments
     *
     * @return true if they were valid, false if the program can not possibly continue
     */
    fun parse(args: Array<String>): Boolean {
        if (args.size < 4) {
            System.err.println("Invalid arguments: at least one of the required arguments is missing.")
            return false
        }

        locationData = File(args[0])
        photoInDirectory = File(args[1])
        photoOutDirectory = File(args[2])

        // Location data file checks
        if (!locationData.exists()) {
            System.err.println("Location data file does not exist!")
            return false
        }

        // Photo directory checks
        if (!photoInDirectory.exists() || !photoInDirectory.isDirectory) {
            System.err.println("Photo input directory does not exist!")
            return false
        }

        // Output directory checks
        if (!photoOutDirectory.exists()) {
            // Create directory if necessary
            val success = photoOutDirectory.mkdirs()
            if (!success) {
                System.err.println("Couldn't create output directory!")
                return false
            }
        }
        if (!photoOutDirectory.isDirectory) {
            System.err.println("Photo output directory is invalid!")
            return false
        }

        val hourOffset: Int
        try {
            hourOffset = Integer.parseInt(args[3])
        } catch (e: NumberFormatException) {
            System.err.println("Invalid time offset, parse your parameters.")
            return false
        }

        println("Required parameters checked, all OK.")

        // Parse optional arguments
        val optArgs = args.sliceArray(4 until args.size)
        parseOptional(optArgs)

        println("Optional parameters checked.")

        // SET UP TIMEZONE
        val timeZoneString = "GMT" + (if (hourOffset < 0) "" else "+") + hourOffset
        timeZone = TimeZone.getTimeZone(timeZoneString)
        dateFormat.timeZone = timeZone

        return true
    }

    /**
     * Parses the given array for optional arguments
     *
     * @param optArgs an array containing the optional arguments from the command line
     */
    private fun parseOptional(optArgs: Array<String>) {
        var i = 0
        while (i < optArgs.size) {
            when (optArgs[i]) {
                "-f", "--from" -> {
                    parseTimeFilter(optArgs[i + 1], false)
                    i += 1
                }
                "-u", "--until" -> {
                    parseTimeFilter(optArgs[i + 1], true)
                    i += 1
                }
                "-a", "--accuracy" -> {
                    parseAccuracyFilter(optArgs[i + 1])
                    i += 1
                }
                "-r", "--restrict" -> {
                    parseLocationFilter(optArgs[i + 1], optArgs[i + 2], optArgs[i + 3], true)
                    i += 3
                }
                "-e", "--exclude" -> {
                    parseLocationFilter(optArgs[i + 1], optArgs[i + 2], optArgs[i + 3], false)
                    i += 3
                }
                else -> {
                    if (optArgs[i][0] == '-') {
                        System.err.println("Unknown optional switch read, ignoring it.")
                    }
                    else {
                        System.err.println("Unknown optional param read, ignoring it.")
                    }
                    System.err.println("Read string was " + optArgs[i])
                }
            }

            i++
        }
    }

    /**
     * Creates a time filter and appends it to the current filters
     *
     * @param arg         the time parameter of the filter
     * @param acceptUntil the before/after parameter of the filter
     */
    private fun parseTimeFilter(arg: String, acceptUntil: Boolean) {
        var timeMS: Long
        try {
            timeMS = dateFormat.parse(arg).time
        } catch (e: ParseException) {
            System.err.println("Time filter can't be added, parse your parameters!")
            return
        }

        // Make "until" type filter inclusive, accepting until the end of
        // the given day instead of until the beginning
        if (acceptUntil) {
            timeMS += dayMS
        }

        addFilter(TimeFilter(timeMS, acceptUntil))
    }

    /**
     * Creates a location filter and appends it to the current filters
     *
     * @param arg1         latitude as a double
     * @param arg2         longitude as a double
     * @param arg3         radius as a double
     * @param acceptInside the inside/outside parameter of the filter
     */
    private fun parseLocationFilter(arg1: String, arg2: String, arg3: String, acceptInside: Boolean) {
        val lat: Double
        val lon: Double
        val rad: Double
        try {
            lat = java.lang.Double.parseDouble(arg1)
            lon = java.lang.Double.parseDouble(arg2)
            rad = java.lang.Double.parseDouble(arg3)
        } catch (e: NumberFormatException) {
            System.err.println("Location filter can't be added, parse your parameters!")
            return
        }

        addFilter(LocationFilter(lat, lon, rad, acceptInside))
    }

    /**
     * Creates an accuracy filter and appends it to the current filters
     *
     * @param arg accuracy as an integer
     */
    private fun parseAccuracyFilter(arg: String) {
        val accuracy: Int
        try {
            accuracy = Integer.parseInt(arg)
        } catch (e: NumberFormatException) {
            System.err.println("Accuracy filter can't be added, parse your parameters!")
            return
        }

        addFilter(AccuracyFilter(accuracy))
    }

    /**
     * Appends the given filter to the current filters, or makes it the
     * current filter, if there aren't any yet
     *
     * @param rf the filter to add
     */
    private fun addFilter(rf: RecordFilter) {
        filter = rf.append(filter)
    }

    /**
     * Creates a RecordManager instance based on the parsed parameters
     *
     * @return the RecordManager instance
     */
    fun getRecordManager() = RecordManager(locationData, filter)

    /**
     * Creates a PhotoManager instance based on the parsed parameters
     *
     * @return the PhotoManager instance
     */
    fun getPhotoManager() = PhotoManager(photoInDirectory, photoOutDirectory, timeZone)
}
