import com.typesafe.sbt.packager.docker.ExecCmd

val AkkaVersion = "2.6.18"

lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging, DockerPlugin, AshScriptPlugin)
  .settings(
    scalaVersion := "2.13.3",
    name := "DiscordBot2",
    maintainer := "XY",
    version := "0.1",

    mainClass in (Compile, run) := Some("Main"),
    resolvers ++= Seq(
      "m2-dv8tion" at "https://m2.dv8tion.net/releases",
      "jcenter" at "https://jcenter.bintray.com",
      "bintray" at "https://dl.bintray.com/dv8fromtheworld/maven"
    ),
    libraryDependencies ++= Seq(
      "net.dv8tion" % "JDA" % "4.4.0_350",
      "com.sedmelluq" % "lavaplayer" % "1.3.77",
      "ch.qos.logback" % "logback-classic" % "1.2.8",
      "com.typesafe.akka" %% "akka-actor" % AkkaVersion
    ),
    defaultLinuxInstallLocation in Docker := "/opt/application",
    executableScriptName := "app",
    dockerBaseImage := "openjdk:17-jdk",
    dockerUpdateLatest := true,
    dockerCommands := dockerCommands.value.filter {
      case ExecCmd("CMD", _*) => false
      case _ => true
    }.map {
      case ExecCmd("ENTRYPOINT", args @ _*) => ExecCmd("CMD", args: _*)
      case other => other
    }
  )
