package com.reservation.service

import com.reservation.AppConf
import com.reservation.model.{AvailabilityVO, Booking, DateRangeVO}
import com.reservation.repository.BookingRepository

import scala.concurrent.Future

trait ReservationService {
  this: BookingRepository with BookingService with AvailabilityService with AppConf =>

  def findAvailability(dateRange: DateRangeVO): Future[AvailabilityVO] = {
    require(dateRange != null, "dateRange needs to be set")
    calculateAvailability {
      if (!dateRange.isOpen)
        dateRange
      else
        DateRangeVO(dateRange.from, Some(dateRange.from.plusMonths(defaultMonthsForAvailabilityRequest)))
    }
  }

  def makeReservation(booking: Booking): Future[Booking] = {
    require(booking != null, "booking needs to be set")
    isBookingCreationAllowed(booking).flatMap { isAllowed =>
      if (isAllowed)
        createBooking(booking)
      else {
        throw new IllegalArgumentException("No availability")
      }
    }
  }

  def modifyReservation(bookingId: String, newDateRange: DateRangeVO): Future[Booking] = {
    require(bookingId != null && !bookingId.isEmpty, "bookingId needs to be set")
    require(newDateRange != null, "newDateRange needs to be set")
    findBookingById(bookingId) flatMap { booking =>
      isBookingModificationAllowed(bookingId, newDateRange).flatMap { isAllowed =>
        if (isAllowed)
          updateBooking(bookingId, booking.copy(dateRange = newDateRange))
        else {
          throw new IllegalArgumentException("No availability")
        }
      }
    }
  }

  def getReservationInfo(bookingId: String): Future[Booking] = {
    require(bookingId != null && !bookingId.isEmpty, "bookingId needs to be set")
    findBookingById(bookingId)
  }

  def cancelReservation(bookingId: String): Future[Boolean] = {
    require(bookingId != null && !bookingId.isEmpty, "bookingId needs to be set")
    deleteBookingById(bookingId)
  }
}