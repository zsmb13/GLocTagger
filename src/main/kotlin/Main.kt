import location.finders.SimpleFinder
import photos.PhotoWorker
import java.io.File
import java.io.FileNotFoundException
import java.util.*

/**
 * Displays the contents of the help file on the error output
 */
private fun displayHelp() {
    val help = File("res/help.txt")

    try {
        val scanner = Scanner(help)
        while (scanner.hasNextLine()) {
            System.err.println(scanner.nextLine())
        }
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    }
}

/**
 * Creates components, runs worker threads
 *
 * @param args arguments from the command line
 */
fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()

    val paramProcessor = ParamProcessor()

    val success = paramProcessor.parse(args)
    if (!success) {
        displayHelp()
        return
    }

    val rm = paramProcessor.getRecordManager()
    val pm = paramProcessor.getPhotoManager()
    val lf = SimpleFinder(rm)

    /*
      Multithreaded use is implemented and working, but is not advised. Since
       the program is very heavily IO bound, it does much better on a single
       thread, where the storage device can perform sequential writes.
     */
    //int threadCount = Runtime.getRuntime().availableProcessors();
    val threadCount = 1

    val threads = ArrayList<Thread>()
    for (i in 0..threadCount - 1) {
        threads.add(Thread(PhotoWorker(pm, lf)))
    }

    threads.forEach { it.start() }

    try {
        for (t in threads) {
            t.join()
        }
    } catch (e: InterruptedException) {
        e.printStackTrace()
    }

    //lf.printStats()

    //println("---")
    //println("Job complete, time taken: " + (System.currentTimeMillis() - startTime) + " ms")
}
