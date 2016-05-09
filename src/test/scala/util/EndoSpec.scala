package util

import org.scalatest._
import org.scalatest.Matchers._
import cats.implicits._

import util.Endo._

class EndoSpec extends FreeSpec {
  "Endomorphisms compose as expected" - {
    "Endomorphisms over int functions work" in {
      val add3: (Int => Int) = _ + 3
      val divide6: (Int => Int) = _ / 6
      val subtractFrom5: (Int => Int) = 5 - _

      (add3 |+| divide6 |+| subtractFrom5)(3) should equal (3)
      (divide6 |+| subtractFrom5 |+| subtractFrom5 |+| add3)(-12) should equal (-1)
    }

    "Endomorphisms over string functions work" in {
      val toLower: (String => String) = (_:String).toLowerCase
      val thirdLetter: (String => String) = (_:String)(2).toString

      (toLower |+| thirdLetter)("ABC") should equal ("c")
    }
  }
}
