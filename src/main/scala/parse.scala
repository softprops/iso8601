package iso8601

import scala.util.parsing.combinator.RegexParsers

object Parse extends RegexParsers {

  def datetime: Parser[DateTime] =
    (date ~ "[T|t]".r ~ time) ^^ {
      case (d ~ _ ~ t) =>  DateTime(d, t)
    }

  def date: Parser[Date] =
    (year ~ "-" ~ month ~ "-" ~ day) ^^ {
      case (year ~ _ ~ mon ~ _ ~ day) => Date(year, mon, day)
    }

  def year: Parser[Int] =
    """(\d{4})""".r ^^ (_.toInt)

  def month: Parser[Int] =
    """(0[1-9]|1[0-2])""".r ^^ (_.toInt)

  // todo: http://tools.ietf.org/html/rfc3339#section-5.7

  def day: Parser[Int] =
    ("""(0[1-9]|1[0-9]|2[0-8])""".r
     | """(0[1-9]|[1-2][0-9])""".r
     | """(0[1-9]|[1-2][0-9]|30)""".r
     | """(0[1-9]|[1-2][2-9]|3[0-1])""".r) ^^ (_.toInt)

  def time: Parser[Time] =
    partialtime ~ offset ^^ {
      case (pt ~ offs) => pt.copy(offset = Some(offs))
    }

  def partialtime: Parser[Time] =
    (hour ~ ":" ~ minute ~ ":" ~ second) ~ opt(secFrac) ^^ {
      case (h ~ _ ~ m ~ _ ~ s) ~ f => Time(h, m, s, secondFraction = f)
    }

  def hour: Parser[Int] =
    """([0-1][0-9]|2[0-3])""".r ^^ (_.toInt)

  def minute: Parser[Int] =
    """([0-5][0-9])""".r ^^ (_.toInt)

  def second: Parser[Int] =
    ("""([0-4][0-9]|5[0-8])""".r
     | """([0-5][0-9])""".r
     | """([0-5][0-9]|60)""".r) ^^ (_.toInt)

  def secFrac: Parser[Int] =
    "." ~> """(\d+)""".r ^^ (_.toInt)

  def offset: Parser[Offset] =
    utc | timeOffset

  def utc: Parser[Offset] =
    "(Z|z)".r ^^ {
      case _ => Zulu
    }

  def timeOffset: Parser[Offset] =
    "([+-])".r  ~ hour ~ ":"~ minute ^^ {
      case "+" ~ hour ~ _ ~ min => Plus(hour, min)
      case "-" ~ hour ~ _ ~ min => Minus(hour, min)
    }

  def apply(in: String) = parseAll(datetime, in)
}
