package iso8601

import org.scalatest.FunSpec

class ParseSpec extends FunSpec {
  describe("parse") {
    it ("should parse 1996-12-19T16:39:57-08:00") {
      Parse("1996-12-19T16:39:57-08:00").fold(fail(_), { dt =>
        assert(dt === DateTime(Date(1996, 12, 19), Time(16, 39, 57, offset = Minus(8,0))))
      })
    }
  }
}
