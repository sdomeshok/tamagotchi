package game
import java.io.{BufferedReader, InputStreamReader}

import models.{Adult, Dead, Egg, Tamagotchi}
import cats._
import cats.implicits._
import cats.std.all._
import cats.data._

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
       case NonFatal(ex) => ???
      }
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
      case t: Adult => t.toString
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