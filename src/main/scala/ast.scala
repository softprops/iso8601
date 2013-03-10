package iso8601

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
case class Time(hour: Int, minute: Int, second: Int, secondFraction: Option[Int] = None, offset: Option[Offset] = None)

case class DateTime(d: Date, t: Time) {
 import java.util.Calendar

 lazy val asCalendar = {
   import java.util.GregorianCalendar
   val c = new GregorianCalendar(d.year, d.month, d.day, t.hour, t.minute, t.second)
   t.offset.map {
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
   }.getOrElse(c)
 }
}
