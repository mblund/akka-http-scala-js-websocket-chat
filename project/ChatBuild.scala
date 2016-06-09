import sbt._
import Keys._

import org.scalajs.sbtplugin.ScalaJSPlugin
import ScalaJSPlugin._
import autoImport._
import sbtassembly.AssemblyKeys

import spray.revolver.RevolverPlugin._

object ChatBuild extends Build {
  lazy val scalaV = "2.11.8"
  lazy val akkaV = "2.4.2"

  lazy val root =
    Project("root", file("."))
      .aggregate(frontend, backend, cli)

  // Scala-Js frontend
  lazy val frontend =
    Project("frontend", file("frontend"))
      .enablePlugins(ScalaJSPlugin)
      .settings(commonSettings: _*)
      .settings(
        persistLauncher in Compile := true,
        persistLauncher in Test := false,
        testFrameworks += new TestFramework("utest.runner.Framework"),
        libraryDependencies ++= Seq(
          "org.scala-js" %%% "scalajs-dom" % "0.9.0",
          "com.github.japgolly.scalajs-react" %%% "core" % "0.11.1",
          "com.lihaoyi" %%% "upickle" % "0.2.8",
          "com.lihaoyi" %%% "utest" % "0.3.0" % "test"
        ),
        // React JS itself (Note the filenames, adjust as needed, eg. to remove addons.)
        jsDependencies ++= Seq(
          "org.webjars.bower" % "react" % "15.0.2"
            /        "react-with-addons.js"
            minified "react-with-addons.min.js"
            commonJSName "React",
          "org.webjars.bower" % "react" % "15.0.2"
            /         "react-dom.js"
            minified  "react-dom.min.js"
            dependsOn "react-with-addons.js"
            commonJSName "ReactDOM",
          "org.webjars.bower" % "react" % "15.0.2"
            /         "react-dom-server.js"
            minified  "react-dom-server.min.js"
            dependsOn "react-dom.js"
            commonJSName "ReactDOMServer")
      )
      .dependsOn(sharedJs)

  // Akka Http based backend
  lazy val backend =
    Project("backend", file("backend"))
      .settings(Revolver.settings: _*)
      .settings(commonSettings: _*)
      .settings(
        libraryDependencies ++= Seq(
          "com.typesafe.akka" %% "akka-http-experimental" % akkaV,
          "org.specs2" %% "specs2" % "2.3.12" % "test",
          "com.lihaoyi" %% "upickle" % "0.2.8"
        ),
        (resourceGenerators in Compile) <+=
          (fastOptJS in Compile in frontend, packageScalaJSLauncher in Compile in frontend, packageMinifiedJSDependencies in Compile in frontend)
            .map((f1, f2, f3) => Seq(f1.data, f2.data, f3)),
        watchSources <++= (watchSources in frontend)
      )
      .dependsOn(sharedJvm)

  lazy val cli =
    Project("cli", file("cli"))
      .settings(Revolver.settings: _*)
      .settings(commonSettings: _*)
      .settings(
        libraryDependencies ++= Seq(
          "com.typesafe.akka" %% "akka-http-core" % akkaV,
          "org.specs2" %% "specs2" % "2.3.12" % "test",
          "com.lihaoyi" %% "upickle" % "0.2.8"
        ),
        fork in run := true,
        connectInput in run := true
      )
      .dependsOn(sharedJvm)

  lazy val shared = (crossProject.crossType(CrossType.Pure) in file ("shared")).
    settings(
      scalaVersion:=scalaV
    )

  lazy val sharedJvm= shared.jvm
  lazy val sharedJs= shared.js

  def commonSettings = Seq(
    scalaVersion := scalaV
  ) ++ ScalariformSupport.formatSettings
}
