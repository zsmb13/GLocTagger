import location.RecordStore
import location.finders.SimpleFinder
import photos.PhotoStore
import photos.PhotoWorker
import java.io.File

/**
 * Displays the contents of the help file on the error output
 */
private fun displayHelp() {
    File("src/main/resources/help.txt").forEachLine {
        System.err.println(it)
    }
}

fun main(args: Array<String>) {
    val params = try {
        ParamProcessor().apply { parse(args) }
    } catch (e: RuntimeException) {
        displayHelp()
        throw e
    }

    val recordStore = RecordStore(
            locationFile = params.locationData,
            filter = params.filter)
    val photoStore = PhotoStore(
            inputDirectory = params.photoInDirectory,
            timeZone = params.timeZone)
    val locationFinder = SimpleFinder(recordStore)

    // Multithreaded use is implemented and working, but is not advised. Since
    // the program is very heavily IO bound, it does much better on a single
    // thread, where the storage device can perform sequential writes.

    // int threadCount = Runtime.getRuntime().availableProcessors();
    val threadCount = 1
    val threads = Array(threadCount) { Thread(PhotoWorker(photoStore, locationFinder, params.photoOutDirectory)) }
    threads.forEach(Thread::start)
    threads.forEach(Thread::join)
}
