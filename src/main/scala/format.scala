package iso8601

import java.util.Calendar

object Format {
  trait View[Of] {
    def apply(of: Of): DateTime
  }

  implicit object CalendarView extends View[Calendar]{
    def apply(cal: Calendar) =
      DateTime.from(cal)
  }

  def apply[T: View](of: T) = implicitly[View[T]].apply(of).iso8601
}
