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
case class Time(
  hour: Int,
  minute: Int,
  second: Int,
  secondFraction: Option[Int] = None,
  offset: Offset = Zulu)

object DateTime {
  def from[T : From](in: T) = implicitly[From[T]].apply(in)
}

/** ISO 8601 representation of internet time */
case class DateTime(date: Date, time: Time) {

  /** Context bound interface for representing ISO 8601
   *  date times as other types */
  def as[T : As] = implicitly[As[T]].apply(this)

  /** A property formatted date time does not guaurantee
   *  a property logical date time. This returns true
   *  if the logical date time is sound */
  lazy val valid = as[Calendar].isRight

  /** A ISO 8601 compliant time stamp for this date time */
  lazy val stamp = {
    def pad(i: Int, to: Int = 2) =
      ("%0" + to + "d").format(i)
    "%s-%s-%sT%s:%s:%s%s%s".format(
      pad(date.year, 4),
      pad(date.month),
      pad(date.day),
      pad(time.hour),
      pad(time.minute),
      pad(time.second),
      time.secondFraction.map("." + _).getOrElse(""),
      time.offset match {
        case Zulu => "Z"
        case Plus(hours, minutes) =>
          "+%s:%s".format(pad(hours, 2), pad(minutes, 2))
        case Minus(hours, minutes) =>
          "-%s:%s".format(pad(hours, 2), pad(minutes, 2))
      })
  }

  override def toString = stamp
}
