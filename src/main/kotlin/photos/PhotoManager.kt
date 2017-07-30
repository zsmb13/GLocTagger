package photos

import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Reads photos from a directory, stores them, provides threadsafe access to them for the workers
 *
 * @param inputDirectory  the directory containing the photos to be read
 * @param outputDirectory the directory to write the processed photos to
 * @param timeZone        the timezone that the photos were taken in
 */
class PhotoManager(inputDirectory: File, outputDirectory: File, timeZone: TimeZone) {

    private val photos: List<Photo>
    private var currentIndex = -1

    companion object {
        internal val dateFormat = SimpleDateFormat("yyyy:MM:dd HH:mm:ss")
        private val imageFormats = arrayOf("jpg", "JPG", "jpeg", "JPEG")
    }

    init {
        photos = inputDirectory
                .listFiles()
                .filter { it.isFile }
                .filter { it.extension in imageFormats }
                .map { Photo(it, outputDirectory) }

        dateFormat.timeZone = timeZone
    }

    /**
     * Fetches the next photo to process
     *
     * @return the next photo, or null, if there are none left.
     */
    internal fun next(): Photo? = synchronized(photos) {
        return photos.getOrNull(++currentIndex)
    }

}
