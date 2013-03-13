package iso8601

import org.scalatest.FunSpec
import java.util.{Calendar, TimeZone}

class FormatSpec extends FunSpec {
  describe("format") {
    it ("should format calendars") {
      val zone = TimeZone.getTimeZone("America/Los_Angeles")
      val cal = Calendar.getInstance(zone)
      cal.set(Calendar.YEAR, 2013)
      cal.set(Calendar.MONTH, 2)
      cal.set(Calendar.DAY_OF_MONTH, 14)
      cal.set(Calendar.HOUR, 12)
      cal.set(Calendar.MINUTE, 10)
      cal.set(Calendar.SECOND, 0)
      assert(Format(cal) === "2013-03-15T00:10:00-07:00")
    }
  }
}
