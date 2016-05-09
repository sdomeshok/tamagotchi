import models.Egg
import game.GameDriver._
import game.InputProviders._
import game.OutputProviders._
import game.{Clean, Feed, Play, Exit}

import cats._
import cats.implicits._
import cats.data.Xor._


object TamagotchiMain {
  def main(args: Array[String]) = try {
    val (inFunc, outFunc) = args match {

      case Array("file", name) => {
        getFileInputProvider(name) match {
          case Left(ex) =>
            println(ex.getMessage)
            System.exit(1)
            ??? // This branch is non-returning, throw an exception so that we can still typecheck
          case Right(fun) =>
            (fun, turnBasedWaitForInput _)
        }
      }


      case Array(input, output) =>
        val inFunc = if (input.toLowerCase == "auto") realtimeInputProvider _ else turnBasedWaitForInput _
        val outFunc = if (output.toLowerCase == "naive") naiveStatePrinter _ else neatStatePrinter _
        (inFunc, outFunc)


      case Array("preloaded") =>
        val commands = (Seq.fill(20)(Clean) ++ // skip egg phase
          Seq.fill(20)(Seq(Clean, Feed, Play)).flatten // try to keep tamagotchi alive
          ++ Seq(Exit))
          .map(_.some)
          .iterator

        (commands.next _, neatStatePrinter _)


      case _ =>
        (turnBasedWaitForInput _, neatStatePrinter _)
    }

    interpretateWithState[models.Tamagotchi, Option[game.Input]](Egg("Baby", 0), inFunc, println, doGameTurn)
  } catch {
    case ex: NotImplementedError => {
      println("Exiting.")
      System.exit(0)
    }
  }
}