package iso8601

import java.util.Calendar

/**
 * @param year 4 digit year
 * @param month 1-12 month
 * @param day 1-28, 1-29, 1-30, 1-31 based on month/year
 */
case class Date(year: Int, month: Int, day: Int)

trait Offset {
  def hours: Int
  def minutes: Int
}

/** denotes a UTC offset of 00:00 */
case object Zulu extends Offset {
  val hours   = 0
  val minutes = 0
}

/** a positive UTC offset */
case class Plus(val hours: Int, val minutes: Int)
   extends Offset

/** a negative UTC offset */
case class Minus(val hours: Int, val minutes: Int)
   extends Offset

/**
 * @param hour 00-23
 * @param minute 00-59
 * @param second 00-58, 00-59, 00-60 based on leap second based on leap second rules
 * @param secondFraction optional fraction of a second
 * @param offset local UTC ffset
 */
case class Time(hour: Int, minute: Int, second: Int,
                secondFraction: Option[Int] = None, offset: Offset = Zulu)

/**
 * @param datetime invalid datetime
 * @param msg error message
 */
case class InvalidDateTime(datetime: DateTime, msg: String)
   extends IllegalArgumentException

object DateTime {
  def from(c: Calendar) = {
    val d = Date(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH))
    val t = Time(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND))
    val offset = c.getTimeZone.getOffset(c.getTimeInMillis) match {
      case 0 => Zulu
      case os =>
        val hour = math.abs((os / (60 * 1000)) / 60)
        val mins = math.abs((os / (60 * 1000)) % 60)
        if (os > 0) Plus(hour, mins) else Minus(hour, mins)
    }
    DateTime(d, t.copy(offset = offset))
  }
}

/** iso8601 representation of internet time */
case class DateTime(d: Date, t: Time) {
  private def pad(i: Int, to: Int) =
    ("%0" + to + "d").format(i)

  lazy val valid = asCalendar.isLeft

  lazy val asCalendar: Either[InvalidDateTime, Calendar] = {
    import java.util.{ GregorianCalendar, TimeZone }
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
        Left(InvalidDateTime(this, e.getMessage))
    }
  }

  lazy val iso8601 = {
    "%s-%s-%sT%s:%s:%s%s%s".format(
      pad(d.year, 4), pad(d.month, 2), pad(d.day, 2),
      pad(t.hour, 2), pad(t.minute, 2), pad(t.second, 2),
      t.secondFraction.map("." + _).getOrElse(""),
      t.offset match {
        case Zulu => "Z"
        case Plus(hours, minutes) => "+%s:%s".format(pad(hours, 2), pad(minutes, 2))
        case Minus(hours, minutes) => "-%s:%s".format(pad(hours, 2), pad(minutes, 2))
      })
  }
}
