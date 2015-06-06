import com.typesafe.sbt.SbtNativePackager._
import NativePackagerKeys._
import sbt._
import Keys._

object ProjectBuild extends Build {

  def standardSettings = Defaults.defaultSettings ++ Seq(
    initialCommands in console := """import scalaz._
      import Scalaz._
      import scalaz.stream._
      import crea.nlp._
      import Patterns._
      import Terms._
      import Trees._
    """,
    name := "knowledge-extraction",
    version := "1.1",
    scalaVersion := "2.11.1",
    maintainer in Debian := "Mark Farrell",
    packageDescription in Debian := "Extracts knowledge from text articles.",
    resolvers ++= Seq(
      "Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases",
      "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
      "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases",
      "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
    ),
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "2.2.0" % "test",
      "org.slf4j" % "slf4j-api" % "1.7.7",
      "org.slf4j" % "slf4j-log4j12" % "1.7.7",
      "log4j" % "log4j" % "1.2.17",
      "it.uniroma1.dis.wsngroup.gexf4j" % "gexf4j" % "0.4.4-BETA",
      "com.github.scopt" %% "scopt" % "3.2.0",
      "edu.stanford.nlp" % "stanford-corenlp" % "3.3.1",
      "edu.stanford.nlp" % "stanford-corenlp" % "3.3.1" classifier "models",
      "org.scalaz" %% "scalaz-core" % "7.1.0",
      "org.scalaz.stream" %% "scalaz-stream" % "0.5",
      "com.chuusai" %% "shapeless" % "2.0.0",
      "com.bizo" % "mighty-csv_2.11" % "0.2",
      "net.sourceforge.owlapi" % "owlapi-distribution" % "3.4.10",
      "net.sf.jwordnet" % "jwnl" % "1.4_rc3",
      "org.scalanlp" %% "epic" % "0.2",
      "org.twitter4j" % "twitter4j-core" % "4.0.2",
      "org.twitter4j" % "twitter4j-stream" % "4.0.2",
      "com.github.tototoshi" %% "scala-csv" % "1.1.1",
      "pircbot" % "pircbot" % "1.5.0",
      "org.http4s" %% "http4s-core" % "0.4.1",
      "org.http4s" %% "http4s-server" % "0.4.1",
      "org.http4s" %% "http4s-dsl" % "0.4.1",
      "org.http4s" %% "http4s-servlet" % "0.4.1",
      "org.http4s" %% "http4s-jetty" % "0.4.1",
      "org.http4s" %% "http4s-blazecore" % "0.4.1",
      "org.http4s" %% "http4s-blazeserver" % "0.4.1",
      "org.log4s" %% "log4s" % "1.1.3",
      "com.jcabi" % "jcabi-log" % "0.15.2",
      "org.scalikejdbc" %% "scalikejdbc" % "2.2.7",
      "com.h2database" % "h2" % "1.4.187",
      "ch.qos.logback" % "logback-classic" % "1.1.3",
      "org.scalaj" %% "scalaj-http" % "1.1.4"
    )
  )

  lazy val root = Project(
    id = "root",
    base = file("."),
    settings = packageArchetype.java_application ++ standardSettings
  )

}
