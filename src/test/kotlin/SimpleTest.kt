import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameter
import org.junit.runners.Parameterized.Parameters
import java.io.File

const val FROM_DIR = "src/test/resources/fromDir"
const val TO_DIR = "src/test/resources/toDir"
const val JSON_FILE = "src/test/resources/location.json"

@RunWith(Parameterized::class)
class SimpleTest {

    @Before
    fun setUp() {
        locationData {
            record {
                timestampMs = 10_000L
                latitudeE7 = 10_000_0000
                longitudeE7 = 20_000_0000
            }
            record {
                timestampMs = 15_000L
                latitudeE7 = 30_000_0000
                longitudeE7 = 40_000_0000
            }
            record {
                timestampMs = 20_000L
                latitudeE7 = 50_000_0000
                longitudeE7 = 60_000_0000
            }
        }
    }

    @After
    fun cleanUp() {
        File(FROM_DIR).deleteRecursively()
        File(TO_DIR).deleteRecursively()
        File(JSON_FILE).delete()
    }

    @JvmField @Parameter(0) var imgTimeMS: Long = 0
    @JvmField @Parameter(1) var expectedLat: Double = 0.0
    @JvmField @Parameter(2) var expectedLon: Double = 0.0

    @Test
    fun test() {
        val a = createTestImage(imgTimeMS)

        main("$JSON_FILE $FROM_DIR $TO_DIR 0"
                .split(" ").toTypedArray())

        assertLatLon(File(TO_DIR, a), expectedLat, expectedLon)
    }

    companion object {
        @JvmStatic @Parameters
        fun data() = arrayOf(
                arrayOf(5_000L, 10.0, 20.0),
                arrayOf(10_000L, 10.0, 20.0),
                arrayOf(11_000L, 10.0, 20.0),
                arrayOf(12_000L, 10.0, 20.0),
                arrayOf(13_000L, 30.0, 40.0),
                arrayOf(14_000L, 30.0, 40.0),
                arrayOf(15_000L, 30.0, 40.0),
                arrayOf(16_000L, 30.0, 40.0),
                arrayOf(19_000L, 50.0, 60.0),
                arrayOf(20_000L, 50.0, 60.0),
                arrayOf(53_000L, 50.0, 60.0)
        )
    }

}
