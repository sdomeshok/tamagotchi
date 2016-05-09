package models

import cats.implicits._
import util.Endo._

sealed trait LifeStage


sealed trait Tamagotchi

case class Egg(
  name: String,
  age: Int
) extends Tamagotchi

case class Adult(
  name: String,
  age: Int = 0,
  species: Species,
  poop: Int = 0,
  maxAge: Int = 50,
  hunger: Int = 0,
  boredom: Int = 0,
  energy: Int = 20,
  awake: Boolean = true,
  napTimeLeft: Int = 0
) extends Tamagotchi {
  override def toString: String = {
    s"Adult($name, age = $age, specie = $species, poop = $poop, hunger = $hunger, boredom = $boredom, energy = $energy, awake = $awake"
  }
}

case class Dead(
  name: String,
  species: Species,
  age: Int) extends Tamagotchi

object Tamagotchi {
  def tick(t: Tamagotchi)
          (maxAge: Int = 50,
           maxHunger: Int = 10,
           maxPoop: Int = 10,
           maxBoredom: Int = 10,
           maxEnergy: Int = 20): Tamagotchi = {
    (checkLifecycle(maxAge) _ |+|
      increaseHunger _ |+|
      increasePoop _ |+|
      increaseSleepiness _ |+|
      increaseBoredom _ |+|
      checkDeath(maxHunger)(maxPoop)(maxBoredom) |+|
      haveNap(maxEnergy) |+|
      checkEvolution _ )(t)
  }

  protected def checkDeath(maxHunger: Int)(maxPoop: Int)(maxBoredom: Int)(t: Tamagotchi): Tamagotchi = t match {
    case t: Adult  =>
      val tooOld = t.age > t.maxAge
      val tooHungry = t.hunger > maxHunger
      val tooPoopy = t.poop > maxPoop
      val tooBored = t.boredom > maxBoredom

      if (tooOld || tooHungry || tooPoopy || tooBored) {
        Dead(t.name, t.species, t.age)
      } else {
        t
      }

    case _ => t
  }

  protected def increaseSleepiness(t: Tamagotchi): Tamagotchi = t match {
    case t: Adult => t.copy(energy = t.energy - 1 )
    case _ => t
  }

  protected def increaseHunger(t: Tamagotchi): Tamagotchi = t match {
    case t: Adult => t.copy(hunger = t.hunger + Species.speciesHungerGrowth(t.species))
    case _ => t
  }

  protected def increasePoop(t: Tamagotchi): Tamagotchi = t match {
    case t: Adult => t.copy(poop = t.poop + Species.poopProductionRate(t.species))
    case _ => t
  }

  protected def increaseBoredom(t: Tamagotchi): Tamagotchi = t match {
    case t: Adult => t.copy(boredom = t.boredom + Species.boredomProductionRate(t.species))
    case _ => t
  }

  /**
    * Tamagotchi can evolve under specific circumstances. This mutates their species field.
    * Only adult unevolved tamagotchi can evolve
    */
  protected def checkEvolution(t: Tamagotchi): Tamagotchi =
    t match {
      case t: Adult if t.species == Basic =>
        t match {
          case _ if t.energy <= 0 && t.hunger >= 10 => t.copy(energy = 20, hunger = 0, species = Ghoul, awake = true)
          case _ if t.boredom <= -10 => t.copy(species = Fairy)
          case _ if t.hunger <= -20 && t.energy >= 15 => t.copy(species = Dragon)
          case _ if t.age >= 20 && t.boredom >= 10 && t.hunger <= 0 => t.copy(species = Dave)
          case _ => t
        }

      case _ => t
    }


  protected def checkLifecycle(maxAge: Int)(t: Tamagotchi): Tamagotchi = t match {
    case t: Egg if t.age >= 20 => Adult(t.name, 0, Basic, 0, maxAge, 0, 0, 20, awake = true)
    case t: Egg => t.copy(age = t.age + 1)
    case t: Adult => t.copy(age = t.age + 1)
    case _ => t
  }

  def haveNap(maxEnergy: Int = 20)(t: Tamagotchi): Tamagotchi = t match {
    case t: Adult if t.awake && t.energy <= 0 => t.copy(awake = false, napTimeLeft = 5)
    case t: Adult if !t.awake && t.napTimeLeft > 0 => t.copy(napTimeLeft = t.napTimeLeft - 1)
    case t: Adult if !t.awake && t.napTimeLeft <= 0 => t.copy(energy = maxEnergy, awake = true)
    case _ => t
  }

  def feed(t: Tamagotchi): Tamagotchi = t match {
    case t: Adult => t.copy(hunger = t.hunger - 5)
    case _ => t
  }

  def clean(t: Tamagotchi): Tamagotchi = t match {
    case t: Adult => t.copy(poop = t.poop - 5)
    case _ => t
  }

  def play(t: Tamagotchi): Tamagotchi = t match {
    case t: Adult => t.copy(boredom = t.boredom - 5)
    case _ => t
  }

  def sleep(maxEnergy: Int = 20)(t: Tamagotchi): Tamagotchi = t match {
    case t: Adult => t.copy(energy = maxEnergy, awake = false, napTimeLeft = 5)
    case _ => t
  }
}