package location.filters

import location.LocationRecord
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameter
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class TimeFilterTestBefore {

    @JvmField @Parameter(0) var timeStampMS = 0L
    @JvmField @Parameter(1) var isValid = false

    @Test
    fun test() {
        val filter = TimeFilter(10L, true)
        val record = LocationRecord(timeStampMS = timeStampMS)

        val result = filter.accept(record)

        assertEquals(isValid, result)
    }

    companion object {
        @JvmStatic @Parameters
        fun data() = arrayOf(
                arrayOf(0L, true),
                arrayOf(5L, true),
                arrayOf(9L, true),
                arrayOf(10L, true),
                arrayOf(11L, false),
                arrayOf(20L, false)
        )
    }

}

class TimeFilterTestAfter {

    @JvmField @Parameter(0) var timeStampMS = 0L
    @JvmField @Parameter(1) var isValid = false

    @Test
    fun test() {
        val filter = TimeFilter(10L, false)
        val record = LocationRecord(timeStampMS = timeStampMS)

        val result = filter.accept(record)

        assertEquals(isValid, result)
    }

    companion object {
        @JvmStatic @Parameters
        fun data() = arrayOf(
                arrayOf(0L, false),
                arrayOf(5L, false),
                arrayOf(9L, false),
                arrayOf(10L, true),
                arrayOf(11L, true),
                arrayOf(20L, true)
        )
    }

}
