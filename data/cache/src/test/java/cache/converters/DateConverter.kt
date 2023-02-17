package cache.converters

import com.justmusic.cache.converters.DateConverter
import org.junit.Assert
import org.junit.Test
import java.util.Calendar

class DateConverterTest {
    private val cal = Calendar.getInstance().apply {
        set(Calendar.YEAR, 1998)
        set(Calendar.MONTH, Calendar.SEPTEMBER)
        set(Calendar.DAY_OF_MONTH, 4)
    }

    @Test
    fun `Convert from date to timestamp`() {
        Assert.assertEquals(cal.timeInMillis, DateConverter.fromDate(cal.time))
    }

    @Test
    fun `Convert from timestamp to date`() {
        Assert.assertEquals(DateConverter.toDate(cal.timeInMillis), cal.time)
    }
}