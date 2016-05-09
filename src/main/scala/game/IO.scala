package game
import java.io.{BufferedReader, InputStreamReader}

import models._
import cats._
import cats.implicits._
import cats.std.all._
import cats.data._
import util.RequestNonLinearExitException

import scala.io.StdIn.readLine
import scala.io.{Codec, Source}
import scala.util.control.NonFatal

object InputProviders {

  def turnBasedWaitForInput(): Option[Input] = decodeInputs(readLine)

  def realtimeInputProvider: Option[Input] = readLineWithTimeout(3000) flatMap decodeInputs

  def getFileInputProvider(fileName: String): Throwable Xor (() => Option[Input]) = {
    Xor.catchNonFatal {
      val lines = Source.fromFile(fileName)(Codec.UTF8).getLines()
      () => try {
        decodeInputs(lines.next())
      } catch {
       case NonFatal(ex) => throw new RequestNonLinearExitException
      }
    }
  }

  def getPreloadedProvider: () => Option[Input] = {
    val commands = (Seq.fill(20)(Clean) ++ // skip egg phase
      Seq.fill(20)(Seq(Clean, Feed, Play)).flatten // try to keep tamagotchi alive
      ++ Seq(Exit))
      .map(_.some)
      .iterator

    () => try {
      commands.next()
    } catch {
      case NonFatal(ex) => throw new RequestNonLinearExitException
    }
  }

  /**
    * Asynchronously reads a line of input from stdin and attempts to parse it into an Input object.
    * This is *not* a good solution, however, it appears that readline is a call that is borderline
    * impossible to interrupt. This solution will busywait until the stream is ready to provide a line.
    *
    * Solution cribbed almost wholesale from http://stackoverflow.com/questions/10059068/set-timeout-for-users-input
    *
    * @return Some(Input) if one was provided and correctly parsed otherwise None
    */
  protected def readLineWithTimeout(timeoutMillis: Long): Option[String] = {
    val in = new BufferedReader(new InputStreamReader(System.in))
    val startTime = System.currentTimeMillis()
    while ((System.currentTimeMillis() - startTime) < timeoutMillis
      && !in.ready()) {
      Thread.sleep(50)
    }

    if (in.ready) {
      in.readLine().some
    } else {
      none
    }
  }

  /**
    * Map inputs from text to internal action types
    *
    * @param input A string representing what may be a valid command
    * @return An instance of Some[Input] if the textual input was valid, otherwise None
    */
  protected def decodeInputs(input: String): Option[Input] = input.toLowerCase match {
    case "exit" | "quit" => Exit.some
    case "feed" => Feed.some
    case "clean" => Clean.some
    case "play" => Play.some
    case "sleep" => Sleep.some
    case _ => none
  }
}

object OutputProviders {

  /**
    * Debugging output provider. Simply prints the raw state at each tick
    *
    * @param t The current tamagotchi state
    */
  def naiveStatePrinter(t: Tamagotchi): Unit = println(t)

  /**
    * Pretty printer for the state. Attempts to interpret the game state and give
    * a more interesting interface to the player. Purposefully hides some detailed game information
    * to make it less a game of perfect information.
    *
    * @param t The current tamagotchi state
    */
  def neatStatePrinter(t: Tamagotchi): Unit = {
    val message = t match {
      case t: Egg => handleEgg(t)
      case t: Adult => handleAdult(10)(10)(10)(t)
      case t: Dead => handleDead(t)
    }

    println(message)
  }

  protected def handleEgg(e: Egg): String = {
    e match {
      case _ if e.age <= 10 => "Your egg sits quietly"
      case _ if e.age > 10 && e.age <= 15 => "Your egg gently rocks in place."
      case _ if e.age > 15 => "Your egg looks ready to hatch!"
    }
  }

  protected[game] def handleAdult(maxHunger: Int = 10)(maxPoop: Int = 10)(maxBoredom: Int = 10)(a: Adult): String = {
    def handleHungry(a: Adult): Option[String] = {
      if (a.hunger + Species.speciesHungerGrowth(a.species) >= maxHunger) {
        s"${a.name} is ravenous!".some
      } else if (a.hunger + 3 * Species.speciesHungerGrowth(a.species ) >= maxHunger) {
        s"${a.name} looks hungry.".some
      } else {
        none
      }
    }

    def handlePoopy(a: Adult): Option[String] = {
      if (a.poop + Species.poopProductionRate(a.species) >= maxPoop) {
        s"${a.name} is drowning in filth!".some
      } else if (a.poop + 3 * Species.poopProductionRate(a.species ) >= maxPoop) {
        s"${a.name} looks like it could use a clean.".some
      } else {
        none
      }
    }

    def handleTired(a: Adult): Option[String] = {
      if (a.energy <= 0 || !a.awake) {
        s"${a.name} is napping.".some
      } else if (a.energy <= 5) {
        s"${a.name} is letting out an occasional yawn.".some
      } else if (a.energy >= 15) {
        s"${a.name} is bright and perky.".some
      } else {
        none
      }
    }

    def handleBored(a: Adult): Option[String] = {
      if (a.boredom + Species.boredomProductionRate(a.species) >= maxBoredom) {
        s"${a.name} is climbing up the walls in frustration!".some
      } else if (a.boredom + 3 * Species.boredomProductionRate(a.species ) >= maxBoredom) {
        s"${a.name} is circling around looking for a toy.".some
      } else {
        none
      }
    }

    def handleOld(a: Adult): Option[String] = {
      if (a.age > a.maxAge - 10) {
        s"${a.name} is looking a little long in the tooth.".some
      } else if (a.age > a.maxAge - 5) {
        s"${a.name} certainly has more gray hair.".some
      } else if (a.age > a.maxAge - 2) {
        s"${a.name} has looked healthier.".some
      }else {
        none
      }
    }

    val base = s"${a.name} is a ${a.species}. It is ${a.age} ticks old.\n"
    base + Seq(handleHungry _, handlePoopy _, handleTired _, handleBored _, handleOld _)
      .map (f => f(a))
      .filter(_.nonEmpty)
      .map(_.get)
      .mkString("\n") + "\n"
  }

  protected def handleDead(d: Dead): String = {
    s"${d.name} the ${d.species} is dead."
  }
}

sealed trait Input
case object Exit extends Input
case object Feed extends Input
case object Clean extends Input
case object Play extends Input
case object Sleep extends Input