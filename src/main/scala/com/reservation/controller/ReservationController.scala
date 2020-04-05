package com.reservation.controller

import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{as, complete, entity, onSuccess, pathPrefix, post, _}
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.reservation.AppConf
import com.typesafe.scalalogging.LazyLogging
import com.reservation.JsonFormats._
import com.reservation.model.Booking
import com.reservation.service.actor.ReservationProtocol

import scala.concurrent.Future

class ReservationController(delegate: ActorRef[ReservationProtocol.Command])(implicit val system: ActorSystem[_]) extends AppConf with LazyLogging {

  private implicit val timeout: Timeout = Timeout.create(routesAskTimeout)

  def makeReservation(booking: Booking): Future[Booking] =
    delegate ? (ReservationProtocol.MakeReservationCommand(booking, _))

  val routes: Route = pathPrefix("reservations") {
    post { // POST /reservations
      entity(as[Booking]) { booking =>
        logger.debug(s"Will create booking ${booking}")
        onSuccess(makeReservation(booking)) { complete(StatusCodes.Created, _) }
      }
    }
  }

  // WIP:
  // PUT /reservations/{bookingId}
  // GET /reservations/{bookingId}
  // DELETE /reservations/{bookingId}
  // GET /reservations/find-availability?from=yyyy-MM-dd&to=yyyy-MM-dd


}