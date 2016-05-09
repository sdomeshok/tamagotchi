package game

import org.scalatest._
import org.scalatest.Matchers._
import cats.implicits._
import models.{Adult, Basic, Dead, Egg}
import util.RequestNonLinearExitException

class GameDriverSpec extends FreeSpec {
  val t = Adult("a", species = Basic, awake = false, age = 0, boredom = 1, hunger = 1, energy = 20, poop = 0)

  // all these changes are what we normally expect to happen after 1 correct tick
  val t2 = Adult("a", species = Basic, awake = true, age = 1, boredom = 2, hunger = 2, energy = 19, poop = 1)

  val e = Egg("e", 0)
  val d = Dead("d", Basic, 0)
  "Game commands do not affect sleeping individuals" - {

    "Sleep does not reset the nap timer" in {
      GameDriver.doGameTurn(Sleep.some, t) should equal (t2)
    }

    "Feed does not affect sleeping individual" in {
      GameDriver.doGameTurn(Feed.some, t) should equal (t2)
    }

    "Play does not affect sleeping individual" in {
      GameDriver.doGameTurn(Play.some, t) should equal (t2)
    }
  }
  "Exit still works on sleeping individuals" in {
    an [RequestNonLinearExitException] should be thrownBy {GameDriver.doGameTurn(Exit.some, t)}
  }

  "Eggs and Dead individuals are unaffected by commands" in {
    GameDriver.doGameTurn(Feed.some, e) should equal (e.copy(age = e.age + 1))
    GameDriver.doGameTurn(Feed.some, d) should equal (d)
  }
}
