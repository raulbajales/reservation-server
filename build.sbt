lazy val akkaHttpVersion = "10.1.11"
lazy val akkaVersion    = "2.6.4"

resolvers += "sxfcode Bintray Repo" at "https://dl.bintray.com/sfxcode/maven/"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "com.reservation",
      scalaVersion    := "2.13.1"
    )),
    name := "reservation-server",
    libraryDependencies ++= Seq(
      "com.typesafe.akka"          %% "akka-http"                % akkaHttpVersion,
      "com.typesafe.akka"          %% "akka-http-spray-json"     % akkaHttpVersion,
      "com.typesafe.akka"          %% "akka-actor-typed"         % akkaVersion,
      "com.typesafe.akka"          %% "akka-stream"              % akkaVersion,
      "com.typesafe.scala-logging" %% "scala-logging"            % "3.9.2",
      "com.sfxcode.nosql"          %% "simple-mongo"             % "1.7.1",
      "com.typesafe"               % "config"                    % "1.4.0",

      "com.typesafe.akka"          %% "akka-http-testkit"        % akkaHttpVersion % Test,
      "com.typesafe.akka"          %% "akka-actor-testkit-typed" % akkaVersion     % Test,
      "org.scalatest"              %% "scalatest"                % "3.0.8"         % Test
    )
  )
