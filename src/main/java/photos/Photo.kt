package photos

import org.apache.sanselan.Sanselan
import org.apache.sanselan.formats.jpeg.JpegImageMetadata
import org.apache.sanselan.formats.jpeg.exifRewrite.ExifRewriter
import org.apache.sanselan.formats.tiff.constants.ExifTagConstants
import java.io.File

/**
 * Represents a single picture that has to be processed
 */
internal class Photo(private val photoFile: File, private val outputDir: File) {

    /**
     * Gets the timestamp for when the photo was taken
     *
     * @return timestamp in milliseconds, UTC time
     */
    @Suppress("UsePropertyAccessSyntax")
    fun getTimeStampMS(): Long {
        val jpegMetadata = Sanselan.getMetadata(photoFile) as? JpegImageMetadata ?: return 0
        val dateField = jpegMetadata.findEXIFValue(ExifTagConstants.EXIF_TAG_CREATE_DATE)
        val dateString = dateField.stringValue
        val date = PhotoManager.dateFormat.parse(dateString)
        return date.getTime()
    }

    /**
     * Writes the photo to the output directory, with the given location data added
     */
    fun writeExifLocation(latitude: Double, longitude: Double) {
        val jpegMetadata = Sanselan.getMetadata(photoFile) as? JpegImageMetadata
        val outputSet = jpegMetadata?.exif?.outputSet

        if (outputSet == null) {
            System.err.println("EXIF data reading error, can't process photo, skipping it.")
            return
        }

        outputSet.setGPSInDegrees(longitude, latitude)

        val outputFile = File(outputDir, photoFile.name)

        outputFile.outputStream().buffered().use { os ->
            val exifRewriter = ExifRewriter()
            exifRewriter.updateExifMetadataLossless(photoFile, os, outputSet)
            os.flush()
        }
    }

}
