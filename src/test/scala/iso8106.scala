package iso8601

import org.scalatest.FunSpec

class Iso8601Spec extends FunSpec {
  describe("iso8601") {
    it ("should parse 1996-12-19T16:39:57-08:00") {
      Iso8601("1996-12-19T16:39:57-08:00").fold(fail(_), { dt =>
        assert(dt === DateTime(Date(1996, 12, 19), Time(16, 39, 57, offset = Minus(8,0))))
      })
    }
  }
}
