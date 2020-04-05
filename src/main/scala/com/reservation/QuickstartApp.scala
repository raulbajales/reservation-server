package com.reservation

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.adapter._
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import com.reservation.ReservationModule._
import com.reservation.controller.ReservationController

import scala.util.{Failure, Success}

object QuickstartApp {

  def main(args: Array[String]): Unit = {
    val rootBehavior = Behaviors.setup[Nothing] { context =>
      val reservationServiceActor = context.spawn(ConcreteReservation.behavior, "ReservationServiceActor")
      context.watch(reservationServiceActor)
      val routes = new ReservationController(reservationServiceActor)(context.system)
      startHttpServer(routes.routes, context.system)
      Behaviors.empty
    }
    ActorSystem[Nothing](rootBehavior, "HelloAkkaHttpServer")
  }

  private def startHttpServer(routes: Route, system: ActorSystem[_]): Unit = {
    implicit val classicSystem: akka.actor.ActorSystem = system.toClassic
    import system.executionContext
    val futureBinding = Http().bindAndHandle(routes, "localhost", 8080)
    futureBinding.onComplete {
      case Success(binding) =>
        system.log.info(s"Server online at http://${binding.localAddress.getHostString}:${binding.localAddress.getPort}/")
      case Failure(ex) =>
        system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
        system.terminate()
    }
  }
}
