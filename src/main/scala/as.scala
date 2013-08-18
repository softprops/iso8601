package iso8601

import java.util.{ Calendar, GregorianCalendar, TimeZone }

trait As[T] {
  def apply(dt: DateTime): Either[InvalidDateTime, T]
}

object As {
  implicit object JavaCalendar extends As[Calendar] {
    def apply(dt: DateTime) = {
      def pad(i: Int, to: Int = 2) =
        ("%0" + to + "d").format(i)
      val (d, t) = (dt.date, dt.time)
      val tz = TimeZone.getTimeZone("GMT%s".format(t.offset match {
        case Plus(h, m) => "+%s:%s".format(pad(h, 2), pad(m, 2))
        case Minus(h, m) => "-%s:%s".format(pad(h, 2), pad(m, 2))
        case Zulu => ""
      }))
      val c = Calendar.getInstance(tz)
      c.setLenient(false)
      c.set(Calendar.YEAR, d.year)
      c.set(Calendar.ERA, GregorianCalendar.AD)
      c.set(Calendar.MONTH, d.month - 1)
      c.set(Calendar.DAY_OF_MONTH, d.day)
      c.set(Calendar.HOUR_OF_DAY, t.hour)
      c.set(Calendar.MINUTE, t.minute)
      c.set(Calendar.SECOND, t.second)
      try {
        c.getTime // this will trigger an IllegalArgumentException if invalid
        Right(c)
      } catch {
        case e: IllegalArgumentException =>
          Left(InvalidDateTime(dt, e.getMessage))
      }
    }
  }
}
