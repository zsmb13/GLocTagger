import location.filters.AccuracyFilter
import location.filters.LocationFilter
import location.filters.RecordFilter
import location.filters.TimeFilter
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

    lateinit var locationData: File
    lateinit var photoInDirectory: File
    lateinit var photoOutDirectory: File

    lateinit var timeZone: TimeZone

    var filter: RecordFilter? = null

    /**
     * Parses the command line arguments given to the program
     *
     * @param args the array of arguments
     *
     * @return true if they were valid, false if the program can not possibly continue
     */
    fun parse(args: Array<String>): Boolean {
        if (args.size < 4) {
            throw RuntimeException("Invalid arguments: at least one of the required arguments is missing.")
        }

        locationData = File(args[0])
        photoInDirectory = File(args[1])
        photoOutDirectory = File(args[2])

        // Location data file checks
        if (!locationData.exists()) {
            throw RuntimeException("Location data file does not exist!")
        }

        // Photo directory checks
        if (!photoInDirectory.exists() || !photoInDirectory.isDirectory) {
            throw RuntimeException("Photo input directory does not exist!")
        }

        // Output directory checks
        if (!photoOutDirectory.exists()) {
            // Create directory if necessary
            val success = photoOutDirectory.mkdirs()
            if (!success) {
                throw RuntimeException("Couldn't create output directory!")
            }
        }
        if (!photoOutDirectory.isDirectory) {
            throw RuntimeException("Photo output directory is invalid!")
        }

        val hourOffset: Int
        try {
            hourOffset = args[3].toInt()
        } catch (e: NumberFormatException) {
            throw RuntimeException("Invalid time offset, parse your parameters.")
        }

        // Parse optional arguments
        val optArgs = args.sliceArray(4 until args.size)
        parseOptional(optArgs)

        // Set up timezone
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
                        throw RuntimeException("Unknown optional switch read, ignoring it.")
                    }
                    else {
                        throw RuntimeException("Unknown optional param read, ignoring it.")
                    }
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
            throw RuntimeException("Time filter can't be added, parse your parameters!")
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
        try {
            val lat = arg1.toDouble()
            val lon = arg2.toDouble()
            val rad = arg3.toDouble()

            addFilter(LocationFilter(lat, lon, rad, acceptInside))
        } catch (e: NumberFormatException) {
            throw RuntimeException("Location filter can't be added, parse your parameters!")
        }
    }

    /**
     * Creates an accuracy filter and appends it to the current filters
     *
     * @param arg accuracy as an integer
     */
    private fun parseAccuracyFilter(arg: String) {
        try {
            addFilter(AccuracyFilter(arg.toInt()))
        } catch (e: NumberFormatException) {
            throw RuntimeException("Accuracy filter can't be added, parse your parameters!")
        }
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

}
