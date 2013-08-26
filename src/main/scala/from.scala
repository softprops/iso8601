package iso8601

import java.util.Calendar

/** Typeclass interface for going from a type to
 *  an ISO 8601 date time */
trait From[T] {
  def apply(in: T): DateTime
}

object From {
  implicit object FromLong extends From[Long] {
    def apply(l: Long) = {
      val c = Calendar.getInstance()
      c.setTimeInMillis(l)
      FromJavaCalendar(c)
    }
  }
  implicit object FromJavaCalendar extends From[Calendar] {
    def apply(c: Calendar) = {
      val d = Date(c.get(Calendar.YEAR),
                   c.get(Calendar.MONTH) + 1,
                   c.get(Calendar.DAY_OF_MONTH))
      val t = Time(c.get(Calendar.HOUR_OF_DAY),
                   c.get(Calendar.MINUTE),
                   c.get(Calendar.SECOND))
      val offset =
        c.getTimeZone.getOffset(c.getTimeInMillis) match {
          case 0 => Zulu
          case os =>
            val hour = math.abs((os / (60 * 1000)) / 60)
            val mins = math.abs((os / (60 * 1000)) % 60)
            if (os > 0) Plus(hour, mins) else Minus(hour, mins)
        }
      DateTime(d, t.copy(offset = offset))
    }
  }
}
