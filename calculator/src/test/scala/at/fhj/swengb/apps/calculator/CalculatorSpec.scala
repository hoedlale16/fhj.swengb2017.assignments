package at.fhj.swengb.apps.calculator

import org.scalatest.{Matchers, WordSpecLike}


class CalculatorSpec extends WordSpecLike with Matchers {


  // pending specs ...
  "validPositiveInt" in {
    assert(Number("123").getNumber() == 123)
  }

  "validPositiveInt1" in {
    assert(Number("1").getNumber() == 1)
  }

  "validNegativInt" in {
    assert(Number("-123").getNumber() == -123)
  }

  "validNegativInt1" in {
    assert(Number("-1").getNumber() == -1)
  }

  "invalidNumber0" in {
    intercept[IllegalArgumentException] {
      Number("a123")
    }
  }

  "invalidNumber1" in {
    intercept[IllegalArgumentException] {
      Number("12a3")
    }
  }

  "invalidNumber2" in {
    intercept[IllegalArgumentException] {
      Number(".123")
    }
  }

  "validPositiveDouble" in {
    assert(Number("123.456").getNumber() == 123.456)
  }

  "validNegativDouble" in {
    assert( Number("-123.456").getNumber() == -123.456)
  }

}
