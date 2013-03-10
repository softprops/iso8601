package iso8601

import java.util.Calendar

object Iso8601 {
  def apply(in: String) = Parse(in) match {
    case Parse.Success(dt, _) => Right(dt)
    case _ => Left("malformed date: %s" format in)
  }

  def format(cal: Calendar) =
    DateTime.from(cal).iso8601
}
