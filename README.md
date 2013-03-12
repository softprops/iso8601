# iso8601

threadsafe and immutable internet [time](http://tools.ietf.org/html/rfc3339)

## usage

    import iso8601._
    Iso8601(stamp).right.map {
      case dt @ DateTime(Date(year, month, day), Time(hour, minute, second, offset)) =>
        require(dt.valid)
        require(dt.iso8601 == stamp)
    }

Doug Tangren (softprops) 2013
