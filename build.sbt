lazy val root = (project in file(".")).
  settings(
    name := "tamagotchi",
    scalaVersion := "2.11.7",
    libraryDependencies ++= Seq(scalatest, scalacheck, cats)
  )

lazy val scalatest = "org.scalatest" %% "scalatest" % "2.2.6" % Test
lazy val scalacheck = "org.scalacheck" %% "scalacheck" % "1.12.5" % Test
lazy val cats = "org.typelevel" %% "cats" % "0.4.1"