package game

import models.Tamagotchi._
import models.{Adult, Tamagotchi}

import scala.annotation.tailrec

object GameDriver {
  // Execute a single step of the game logic
  def doGameTurn(input: Option[Input], state: Tamagotchi): Tamagotchi = {
    // Inputs except `Exit` only affect adult, alive and awake tamagotchi
    val updatedState = state match {
      case t: Adult if t.awake =>
        input.fold (state) {
          case Exit => ???
          case Feed => Tamagotchi.feed(t)
          case Clean => Tamagotchi.clean(t)
          case Play => Tamagotchi.play(t)
          case Sleep => Tamagotchi.sleep()(t)
        }

      case _ if input contains Exit => ???
      case _ => state
    }

    tick(updatedState)()
  }



  @tailrec
  def interpretateWithState[State, Input](state: State,
                                          input: () => Input,
                                          output: State => _,
                                          process: (Input, State) => State): Unit ={
    val newState = process(input(), state)
    output(newState)
    interpretateWithState(newState, input, output, process)
  }
}
