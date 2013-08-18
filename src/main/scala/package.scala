package object iso8601 {
  /**
   * @param datetime invalid datetime
   * @param msg error message
   */
  case class InvalidDateTime(datetime: DateTime, msg: String)
       extends IllegalArgumentException
}
