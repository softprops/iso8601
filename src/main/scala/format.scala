package iso8601

import java.util.Calendar

/** There is only one format, ISO 8601, but the source of what's
 *  being formatted may vary. As such a we must know that a 
 *  iso 8601 datetime can be extracted from a given representation
 *  of time.
 */
object Format {
  /** @return a context bounded views of a DateTime's
   *  iso 8601 representation */
  def apply[T : From](rep: T) = DateTime.from(rep).stamp
}
