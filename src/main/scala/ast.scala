package iso8601

import java.util.Calendar

case class Date(year: Int, month: Int, day: Int)
trait Offset {
  def hours: Int
  def minutes: Int
}

case object Zulu extends Offset {
  val hours   = 0
  val minutes = 0
}
case class Plus(val hours: Int, val minutes: Int) extends Offset
case class Minus(val hours: Int, val minutes: Int) extends Offset
case class Time(hour: Int, minute: Int, second: Int,
                secondFraction: Option[Int] = None, offset: Offset = Zulu)

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

case class DateTime(d: Date, t: Time) {

  lazy val asCalendar = {
    import java.util.GregorianCalendar
    val c = new GregorianCalendar(d.year, d.month, d.day, t.hour, t.minute, t.second)
    t.offset match {
      case Plus(hour, min) =>
        c.add(Calendar.HOUR, hour)
        c.add(Calendar.MINUTE, min)
        c
      case Minus(hour, min) =>
        c.add(Calendar.HOUR, -hour)
        c.add(Calendar.MINUTE, -min)
        c
      case Zulu =>
        c // +00:00 (no offset)
    }
  }

  lazy val iso8601 = {
    def pad(i: Int, to: Int) =
      ("%0" + to + "d").format(i)
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
