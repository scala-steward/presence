ThisBuild / scalaVersion := "2.13.3"

ThisBuild / libraryDependencies ++=
  Nil

ThisBuild / libraryDependencies ++=
  "com.softwaremill.diffx" %% "diffx-scalatest" % "0.4.1" % Test ::
    "com.github.alexarchambault" %% "scalacheck-shapeless_1.14" % "1.2.5" % Test ::
    "org.scalacheck" %% "scalacheck" % "1.15.2" % Test ::
    "org.scalamock" %% "scalamock" % "5.1.0" % Test ::
    "org.scalatest" %% "scalatest" % "3.2.3" % Test ::
    Nil

ThisBuild / libraryDependencies ++= {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, 12)) =>
      compilerPlugin(("org.scalamacros" % "paradise" % "2.1.1").cross(CrossVersion.full)) ::
        Nil
    case _ =>
      Nil
  }
}
