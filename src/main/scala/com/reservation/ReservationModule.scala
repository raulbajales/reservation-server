package com.reservation

import com.reservation.repository.impl.BookingRepositoryMongo
import com.reservation.service.{AvailabilityService, BookingService, ReservationService}
import com.reservation.service.actor.ReservationBehavior

package object ReservationModule {

  object ConcreteReservation extends ReservationBehavior
    with ReservationService
    with BookingRepositoryMongo
    with BookingService
    with AvailabilityService
    with AppConf
}
