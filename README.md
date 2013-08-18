# iso8601

[![Build Status](https://travis-ci.org/softprops/iso8601.png?branch=master)](https://travis-ci.org/softprops/iso8601)

threadsafe and immutable internet [time](http://tools.ietf.org/html/rfc3339)

## usage

### parsing

```scala
import iso8601._
import java.util.Calendar
    
Parse(stamp).right.map {
  case dt @ DateTime(Date(year, month, day), Time(hour, minute, second, offset)) =>
    require(dt.valid)
}
```

### representing as ...

Once parsed, this library stores a representation of the time in an
intermediate type, `iso8601.DateTime` which can then be used to resolve an application specific
representation of time. DateTime objects provide an `as` method which takes a context bound type class instance of
`As[T]` where T may be any given type which is we can convert a DateTime to type T. Not all property formatted date times
are logical times. As, such the `As[T]` interface returns an `Either` type.

```scala
datetime.as[Calendar]
  .fold(handleInvalid, { calendar =>
    // do something with java.util.Calendar
  })
```

You may provide your own `As[T]` conversions by bringing an implicit type class instance of your representation into scope

```scala
implicit val object AsMine extends is8601.As[MyType] {
  def apply(dt: DateTime) = Right(...)
}
datetime.as[MyType]
  .fold(handleInvalid, { myType =>
    // do something with my type
  })
```

### formatting

This library also supports formatting representations of time to ISO 8601 compliant timestamps.

This library needs to be able to convert a given representation of time
to the `iso8601.DateTime` type to do so. The `Format` object, which formats dates, requires there to be a typeclass instance for `From[T]` for the given type to transform the type into an instance of `iso8601.DateTime`.

This library comes out of the box with a `From[java.util.Calendar]` implementation.

```scala
Format(Calendar.getInstance) // 2013-03-13T22:16:54-04:00
```

To create your own `From[T]` instance, you can do the following.

```scala
implicit object YourTypeView extends From[YourType] {
  def apply(yourType: YourType): iso8601.DateTime = {
    convertYourType(yourType)
  }
}
Format(newYourType) // iso 8601 formatted string
```


Doug Tangren (softprops) 2013
