package com.reservation.service.actor

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import com.reservation.service.ReservationService

import scala.concurrent.ExecutionContext

trait ReservationBehavior {
  this: ReservationService =>

  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  val behavior: Behavior[ReservationProtocol.Command] = Behaviors.receiveMessage {
    case ReservationProtocol.FindAvailabilityCommand(dateRange, replyTo) =>
      findAvailability(dateRange).map(replyTo ! _)
      Behaviors.same
    case ReservationProtocol.MakeReservationCommand(booking, replyTo) =>
      makeReservation(booking).map(replyTo ! _)
      Behaviors.same
    case ReservationProtocol.ModifyReservationCommand(bookingId, newDateRange, replyTo) =>
      modifyReservation(bookingId, newDateRange).map(replyTo ! _)
      Behaviors.same
    case ReservationProtocol.GetReservationInfoCommand(bookingId, replyTo) =>
      getReservationInfo(bookingId).map(replyTo ! _)
      Behaviors.same
    case ReservationProtocol.CancelReservationCommand(bookingId, replyTo) =>
      cancelReservation(bookingId).map(replyTo ! _)
      Behaviors.same
  }
}