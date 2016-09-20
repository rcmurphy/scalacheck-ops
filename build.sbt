
val commonRootSettings = Seq(
  name := "scalacheck-ops",
  organization := "me.jeffmay",
  organizationName := "Jeff May",
  version := "2.0.0",

  // scala version for root project
  scalaVersion := "2.11.8",
  crossScalaVersions := Seq("2.11.8", "2.10.6"),

  licenses += ("Apache-2.0", url("http://opensource.org/licenses/apache-2.0"))
)

lazy val root = (project in file("."))
  .settings(commonRootSettings)
  .settings(
    name := "scalacheck-ops-root",
    // don't publish the surrounding multi-project root
    publish := {}
  )
  .aggregate(core, `core-1_12`, joda, `joda-1_12`)

// the version of scalacheck, all cross-compiled settings are derived from this setting
lazy val scalacheckVersion = settingKey[String]("version of scalacheck (all cross-compiled settings are derived from this setting)")

// using scalatest version 3.0.0 should be no problem
lazy val scalatestVersion = settingKey[String]("version of scalatest")

/**
  * Choose one value or another based on the version of scalacheck.
  */
def basedOnVersion[T](version: String, old: T, latest: T): T = {
  if (version startsWith "1.12.") old else latest
}

val commonSettings = Seq(

  // scalatest 2.2.6 pulls in scalacheck and it only works with 1.12.5
  // if you want to pull in the latest version, you should be fine to pull in scalacheck 1.13.x
  scalatestVersion := basedOnVersion(scalacheckVersion.value, "2.2.6", "3.0.0"),

  scalacOptions := {
    // the deprecation:false flag is only supported by scala >= 2.11.3, but needed for scala >= 2.11.0 to avoid warnings
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, scalaMinor)) if scalaMinor >= 11 =>
        // For scala versions >= 2.11.3
        Seq("-Xfatal-warnings", "-deprecation:false")
      case Some((2, scalaMinor)) if scalaMinor < 11 =>
        // For scala versions 2.10.x
        Seq("-Xfatal-warnings")
    }
  } ++ Seq(
    "-feature",
    "-Xlint",
    "-Ywarn-dead-code",
    "-encoding", "UTF-8"
  ),

  libraryDependencies ++= Seq(
    // pull in the specified version of scalacheck
    "org.scalacheck" %% "scalacheck" % scalacheckVersion.value,
    // scalatest 2.2.6 pulls in scalacheck 1.12.5 so it only works with 1.12.5
    "org.scalatest" %% "scalatest" % basedOnVersion(scalacheckVersion.value, "2.2.6", "3.0.0") % Test
  ),

  // show full stack traces in test failures ()
  testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oF"),

  // force scala version
  ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) },

  // disable compilation of ScalaDocs, since this always breaks on links and isn't as helpful as source
  sources in(Compile, doc) := Seq.empty,

  // disable publishing empty ScalaDocs
  publishArtifact in (Compile, packageDoc) := false
)

def moduleName(projectName: String): SettingsDefinition = {
  name := basedOnVersion(scalacheckVersion.value, projectName + "_1.12", projectName + "_1.13")
}

val coreSettings = commonSettings ++ Seq(
  moduleName("scalacheck-ops-core")
)

val jodaSettings = commonSettings ++ Seq(
  moduleName("scalacheck-ops-joda"),
  libraryDependencies ++= Seq(
    "org.joda" % "joda-convert" % "1.8",
    "joda-time" % "joda-time" % "2.9.4"
  )
)

lazy val core = (project in file("core"))
  .settings(coreSettings: _*)
  .settings(
    scalacheckVersion := "1.13.2"
  )

lazy val joda = (project in file("joda"))
  .settings(jodaSettings: _*)
  .settings(
    scalacheckVersion := "1.13.2"
  )
  .dependsOn(core % "compile;test->test")

lazy val `core-1_12` = (project in file("core-1.12"))
  .settings(coreSettings: _*)
  .settings(
    scalacheckVersion := "1.12.5"
  )

lazy val `joda-1_12` = (project in file("joda-1.12"))
  .settings(jodaSettings: _*)
  .settings(
    scalacheckVersion := "1.12.5"
  )
  .dependsOn(`core-1_12` % "compile;test->test")
