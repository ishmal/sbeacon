name := "sbeacon"

version := "1.0"

scalaVersion := "2.10.0"

//javacOptions ++= Seq("-target", "1.6")

libraryDependencies ++= Seq(
    "net.sourceforge.jtransforms" % "jtransforms"     % "2.4.0"
)

scalacOptions += "-deprecation"

scalacOptions += "-feature"





