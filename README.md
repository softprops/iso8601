# iso8601

[![Build Status](https://travis-ci.org/softprops/iso8601.png?branch=master)](https://travis-ci.org/softprops/iso8601)

threadsafe and immutable internet [time](http://tools.ietf.org/html/rfc3339)

## usage

### parsing

    import iso8601._
    import java.util.Calendar
    
    Parse(stamp).right.map {
      case dt @ DateTime(Date(year, month, day), Time(hour, minute, second, offset)) =>
        require(dt.valid)
        require(dt.iso8601 == stamp)
    }


Once parsed, this library stores a representation of the the time in an
intermediate type, `iso8601.DateTime` which can then be used to resolve an application specific
representation of time. Out of the box this library provides a conversion to 
the `java.util.Calendar`.

    datetime.asCalendar.fold(handleInvalid, { calendar =>
       // do something with java.util.Calendar
    })

### formatting

This library also supports formatting representations of time to iso8601 compliant timestamps

This library needs to be able to convert a given representation of time
to the `iso8601.DateTime` type to do so. The `Format` object, which formats dates, requires there to be a typeclass `Format.View[T]` for the given type to transform the type into an instance of `iso8601.DateTime`.

This library comes out of the box with a `java.util.Calendar` View.

    Format(Calendar.getInstance) // 2013-03-13T22:16:54-04:00

To create your own view you can do the following.

    implicit object YourTypeView extends Format.View[YourType] {
      def apply(yourType: YourType): iso8601.DateTime = {
         convertYourType(yourType)
      }
    }


Doug Tangren (softprops) 2013
