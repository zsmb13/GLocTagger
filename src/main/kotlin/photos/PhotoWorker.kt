package photos


import location.finders.LocationFinder
import org.apache.sanselan.Sanselan
import org.apache.sanselan.formats.jpeg.JpegImageMetadata
import org.apache.sanselan.formats.jpeg.exifRewrite.ExifRewriter
import java.io.File

/**
 * Gets photos from the given PhotoStore, processes them, and writes
 * the results to the output directory
 */
class PhotoWorker(private val photoStore: PhotoStore,
                  private val locationFinder: LocationFinder,
                  private val outputDir: File)
    : Runnable {

    /**
     * Gets and processes pictures until there are none left
     */
    override fun run() {
        var p: Photo? = photoStore.next()
        while (p != null) {
            val (lat, lon) = locationFinder.getLocation(p.getTimeStampMS())
                    ?: throw RuntimeException("No timestamp found for a photo")

            writeExifLocation(p.file, lat, lon)
            p = photoStore.next()
        }
    }

    /**
     * Writes the photo to the output directory, with the given location data added
     */
    private fun writeExifLocation(photoFile: File, latitude: Double, longitude: Double) {
        val jpegMetadata = Sanselan.getMetadata(photoFile) as? JpegImageMetadata
        val outputSet = jpegMetadata?.exif?.outputSet
                ?: throw RuntimeException("EXIF data reading error, can't process photo")

        outputSet.setGPSInDegrees(longitude, latitude)

        val outputFile = File(outputDir, photoFile.name)

        outputFile.outputStream().buffered().use { os ->
            ExifRewriter().updateExifMetadataLossless(photoFile, os, outputSet)
            os.flush()
        }
    }

}
