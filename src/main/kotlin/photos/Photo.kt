package photos

import org.apache.sanselan.Sanselan
import org.apache.sanselan.formats.jpeg.JpegImageMetadata
import org.apache.sanselan.formats.tiff.constants.ExifTagConstants
import java.io.File

/**
 * Represents a single picture that has to be processed
 */
internal class Photo(val file: File) {

    /**
     * Gets the timestamp for when the photo was taken
     *
     * @return timestamp in milliseconds, UTC time
     */
    @Suppress("UsePropertyAccessSyntax")
    fun getTimeStampMS(): Long {
        val jpegMetadata = Sanselan.getMetadata(file) as? JpegImageMetadata ?: return 0
        val dateField = jpegMetadata.findEXIFValue(ExifTagConstants.EXIF_TAG_CREATE_DATE)
        val dateString = dateField.stringValue
        val date = PhotoStore.dateFormat.parse(dateString)
        return date.getTime()
    }

}
