import com.fasterxml.jackson.databind.ObjectMapper
import location.databinding.PlainRecordObject
import location.databinding.PlainRoot
import org.apache.sanselan.Sanselan
import org.apache.sanselan.common.RationalNumber
import org.apache.sanselan.formats.jpeg.JpegImageMetadata
import org.apache.sanselan.formats.jpeg.exifRewrite.ExifRewriter
import org.apache.sanselan.formats.tiff.TiffField
import org.apache.sanselan.formats.tiff.constants.ExifTagConstants.EXIF_TAG_CREATE_DATE
import org.apache.sanselan.formats.tiff.constants.GPSTagConstants
import org.apache.sanselan.formats.tiff.constants.TiffFieldTypeConstants.FIELD_TYPE_ASCII
import org.apache.sanselan.formats.tiff.write.TiffOutputField
import org.junit.Assert
import java.io.File
import java.math.BigInteger
import java.text.SimpleDateFormat
import java.util.*

internal fun createTestImage(timeMS: Long, offsetFromGMT: Int = 0): String {
    val date = getDateString(timeMS, offsetFromGMT)

    val original = File("src/test/resources/test.jpg")
    val jpegMetadata = Sanselan.getMetadata(original) as JpegImageMetadata
    val outputSet = jpegMetadata.exif.outputSet
    val exifDir = outputSet.getOrCreateExifDirectory()

    val dateField = TiffOutputField(EXIF_TAG_CREATE_DATE, FIELD_TYPE_ASCII, date.length, date.toByteArray())
    exifDir.add(dateField)

    val fileName = BigInteger(130, Random()).toString(32) + ".jpeg"

    val inImg = File(FROM_DIR, fileName)
    inImg.parentFile.mkdirs()
    inImg.outputStream().buffered().use { os ->
        ExifRewriter().updateExifMetadataLossless(original, os, outputSet)
        os.flush()
    }

    return fileName
}

internal fun getDateString(timeMs: Long, offset: Int): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeMs

    val dateFormat = SimpleDateFormat("yyyy:MM:dd HH:mm:ss")

    if (offset >= 0) {
        dateFormat.timeZone = TimeZone.getTimeZone("GMT+$offset")
    }
    else {
        dateFormat.timeZone = TimeZone.getTimeZone("GMT$offset")
    }

    return dateFormat.format(Date(calendar.timeInMillis))
}

internal fun assertLatLon(outImg: File, expectedLat: Double, expectedLon: Double) {
    fun TiffField.asLatOrLon(): Double {
        @Suppress("UNCHECKED_CAST")
        val array = this.value as Array<RationalNumber>
        return array[0].toDouble() + array[1].toDouble() / 60.0 + array[2].toDouble() / 60.0 / 60.0
    }

    val jpegImageMetadata = Sanselan.getMetadata(outImg) as JpegImageMetadata

    val lat = jpegImageMetadata.findEXIFValue(GPSTagConstants.GPS_TAG_GPS_LATITUDE).asLatOrLon()
    val lon = jpegImageMetadata.findEXIFValue(GPSTagConstants.GPS_TAG_GPS_LONGITUDE).asLatOrLon()

    Assert.assertEquals(expectedLat, lat, 0.00001)
    Assert.assertEquals(expectedLon, lon, 0.00001)
}

internal fun locationData(block: JsonRoot.() -> Unit) {
    val root = JsonRoot().apply(block).getRoot()
    ObjectMapper().writeValue(File(JSON_FILE), root)
}

internal class JsonRoot {

    val records = mutableListOf<PlainRecordObject>()

    fun getRoot() = PlainRoot(records)

    fun record(block: PlainRecordObject.() -> Unit) {
        val record = PlainRecordObject().apply(block)
        records += record
    }

}
