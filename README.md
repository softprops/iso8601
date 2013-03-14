# iso8601

[![Build Status](https://travis-ci.org/softprops/iso8601.png?branch=master)](https://travis-ci.org/softprops/iso8601)

threadsafe and immutable internet [time](http://tools.ietf.org/html/rfc3339)

## usage

    import iso8601._
    import java.util.Calendar
    
    Parse(stamp).right.map {
      case dt @ DateTime(Date(year, month, day), Time(hour, minute, second, offset)) =>
        require(dt.valid)
        require(dt.iso8601 == stamp)
    }

    Format(Calendar.getInstance) // 2013-03-13T22:16:54-04:00

Doug Tangren (softprops) 2013
