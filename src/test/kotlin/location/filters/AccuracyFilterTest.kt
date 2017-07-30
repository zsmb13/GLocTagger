package location.filters

import location.LocationRecord
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameter
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class AccuracyFilterTest {

    @JvmField @Parameter(0) var accuracy = 0
    @JvmField @Parameter(1) var isValid = false

    @Test
    fun test() {
        val filter = AccuracyFilter(10)
        val record = LocationRecord(0L, accuracy = accuracy)

        val result = filter.accept(record)

        assertEquals(isValid, result)
    }

    companion object {
        @JvmStatic @Parameters
        fun data() = arrayOf(
                arrayOf(0, true),
                arrayOf(5, true),
                arrayOf(9, true),
                arrayOf(10, true),
                arrayOf(11, false),
                arrayOf(20, false)
        )
    }

}
