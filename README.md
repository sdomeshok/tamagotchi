# Tamagotchi

## Technical
Implementation of a basic Tamagotchi simulator. The entire code is pure with the exception of functions that do direct
IO and a small escape hatch to throw an exception to make handling the exit command more straight forward.
With these exceptions, the code has complete referential transparency.

The application is organised into the game driver located in the `game` packed and the basic components in the `model` package. Being a game, this separation is not as clear as we would like and there are some ambiguous components.
`util` contains an instance of `Monoid` for endomorphisms which `cats` in its infinite wisdom has an insane version of. The version
that I have implemented, as the comments note, composes backward to your typical monoid and the result is more reminiscent of the
F# `|>` operator. This is deliberate.

## Playing
The general flow of the engine is a pipeline that analyses and manipulates instances of the `Tamagotchi` trait. Tamagotchi
have several mechanics. They begin as an egg and after a certain number of turns, they hatch into an adult. An adult will stay
in that phase until it either reaches old age and dies, or dies due to lack of care.

Tamagotchi have a number of stats that need
to be looked after. Your tamagotchi will get hungry and this is relieved with the `feed` command. `clean` will remove their waste.
Tamagotchi need to be kept busy or they will be annoyed, you can entertain them with `play`. A tamagotchi only has so much energy
reserves and will need a nap every now and again. If a tamagotchi reaches zero energy, it will fall asleep on its own.  Note that tamagotchi will
still lose hunger, boredom and cleanliness in their sleep and can die in their sleep so a well timed `sleep` command when they
are in good shape will be useful for keeping them alive.

This implementation is set up fairly aggresively and plays more like a simple tactical management game than a traditional tamagotchi
but the paramaters for various death conditions can be trivially changed to make it easier.

## Evolutions
Your tamagotchi begins its life as a basic model. When you perform certain actions and leave your tamagotchi in certain states,
it can evolve into a different species. There are 4 species to unlock: Fairys which are playful and self-entertaining; Ghouls, who's violent
transition leaves them craving flesh; Dragons, who fly around with the enregy of fire and finally Dave, the bored college student.
Find out how to unlock each species on your own! Or cheat and read the code. Cheater. Evolutions are not just cosmetic! Each evolution changes the consumption rate of the various resources. Some evolutions are very hard to keep alive or very hard to reach on the default settings, but all are possible!

## Commands
The game can take inputs and outputs in various ways. Supported inputs are realtime, turnbased and batch read from a file. Supported output
formats are naive, which prints raw data structures mostly for debugging and and the neat printer which gives a much more interesting
description of the game.

The simplest way to run the game with default settings is to just hit `sbt run`. This will run the game in turn based pretty printed mode.
To specify input and output modes, you can add options to the command line. `sbt run auto naive` will run the game in real time with
the naive printer. `sbt run manual pretty` will run the turn based pretty printer. Options can be mixed and matched. To invoke the
file reader, call `sbt file <name>` where name is a valid file path where each line is a command of `{sleep, feed, clean, play, exit}`.

## Tests
Please excuse the stubbed tests, it would have taken about as much time as I spent on this once over to really bring the tests up to the level that
I would like, I hope the enclosed tests and the test stubbs give credence to the fact that I sincerely believe in testing
methodology.
