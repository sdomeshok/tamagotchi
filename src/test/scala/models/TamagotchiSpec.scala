package models
import org.scalatest._
import org.scalatest.Matchers._

class TamagotchiSpec extends FreeSpec {
"The game model should" - {
  "Correctly identify a dead Adult" - {
    val t = Adult("test", 5, Basic, 0, maxAge = 10, hunger = 0, boredom = 0, energy = 20, awake = false, napTimeLeft = 0)

    "That was bored to death" in {
      Tamagotchi.checkDeath(maxHunger = 10)(maxPoop = 10)(maxBoredom = 10)(t.copy(boredom = 11)) shouldBe a [Dead]
    }

    "That pooped to death" in {
      Tamagotchi.checkDeath(maxHunger = 10)(maxPoop = 10)(maxBoredom = 10)(t.copy(poop = 11)) shouldBe a [Dead]

    }

    "That starved to death" in {
      Tamagotchi.checkDeath(maxHunger = 10)(maxPoop = 10)(maxBoredom = 10)(t.copy(hunger = 11)) shouldBe a [Dead]

    }

    "That died of old age" in {
      Tamagotchi.checkDeath(maxHunger = 10)(maxPoop = 10)(maxBoredom = 10)(t.copy(age = 11)) shouldBe a [Dead]
    }

    "That died for multiple reasons" in {
      Tamagotchi.checkDeath(maxHunger = 10)(maxPoop = 10)(maxBoredom = 10)(t.copy(age = 11, hunger = 10, boredom = 11)) shouldBe a [Dead]
    }

    "Do nothing if an individual is ok" in {
      Tamagotchi.checkDeath(10)(10)(10)(t) should equal (t)
    }
  }

  "Correctly handle ages and life phases" - {
    val e = Egg("Egg", 0)
    val d = Dead("d", Basic, 10)

    "Increase and egg's age if has not matured" in {
      Tamagotchi.checkLifecycle(10)(e) should equal (Egg("Egg", 1))
    }

    "Hatch an egg that is ready to" in {
      val t = Tamagotchi.checkLifecycle(10)(e.copy(age = 20))
      t should equal (Adult("Egg", 0, Basic, 0, 10, 0, 0, 20, true, 0))
    }

    "Increase an adult's age" in {
      val a = Adult("adult", species = Basic, age = 3)
      Tamagotchi.checkLifecycle(10)(a).asInstanceOf[Adult].age should equal (4)
    }

    "Dead invividuals stop aging" in {
      Tamagotchi.checkLifecycle(5)(d).asInstanceOf[Dead].age should equal (10)

    }

  }

  "Evaluate effects in the correct order" - {
    "Use an individuals pre-evolution hunger to compute change in hunger"
    "Use an individuals pre-evolution boredom to compute change in boredom"
    "Use an individuals pre-evolution poop to compute change in poop"
    "Individual can still die while asleep"
  }

  "Handle evolutions correctly" - {
    "Only a Basic type can evolve"
    "Can correctly evolve into a Fairy"
    "Can correctly evolve into a Dragon"
    "Can correctly evolve into a Ghoul"
    "Can correctly evolve into a Dave"
    "Evolving into a Ghoul will prevent a death from hunger that turn"
    "Resources are consumed from the correct species"
  }

  "Sleep is handled correctly" - {
    "The sleep action will put an individual to sleep"
    "An individual will keep the sleep state until their nap time runs out"
    "An individual's energy is not reset until they awaken"
    "Each turn, an individual is one turn closer to waking up"
    "A sleeping individual can evolve (needed for Ghoul transformation)"

  }

  "Basic actions work as expected" - {
    "Play reduces boredom"
    "Clean reduces poop"
    "Feed reduces hunger"
    "Sleep starts a nap"
  }

}
}
