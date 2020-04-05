package com.reservation.service.actor

import akka.actor.typed.ActorRef
import com.reservation.model.{AvailabilityVO, Booking, DateRangeVO}

package object ReservationProtocol {
  sealed trait Command

  final case class FindAvailabilityCommand(dateRange: DateRangeVO, replyTo: ActorRef[AvailabilityVO]) extends Command

  final case class MakeReservationCommand(booking: Booking, replyTo: ActorRef[Booking]) extends Command

  final case class ModifyReservationCommand(bookingId: String, newDateRange: DateRangeVO, replyTo: ActorRef[Booking]) extends Command

  final case class GetReservationInfoCommand(bookingId: String, replyTo: ActorRef[Booking]) extends Command

  final case class CancelReservationCommand(bookingId: String, replyTo: ActorRef[Boolean]) extends Command
}