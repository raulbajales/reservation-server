package com.reservation.service

import java.time.LocalDate
import java.time.temporal.ChronoUnit

import com.reservation.AppConf
import com.reservation.model.{AvailabilityVO, Booking, DateRangeVO}

import scala.concurrent.Future

trait BookingService {
  this: AvailabilityService with AppConf =>

  def isBookingCreationAllowed(booking: Booking): Future[Boolean] = {
    require(booking != null, "booking needs to be set")
    val dateRange = booking.dateRange
    checkPreconditions(dateRange)
    calculateAvailability(dateRange).map { availability =>
      isDateRangeInsideAvailability(dateRange, availability)
    }
  }

  def isBookingModificationAllowed(bookingId: String, newDateRange: DateRangeVO): Future[Boolean] = {
    require(bookingId != null && !bookingId.isEmpty, "bookingId needs to be set")
    require(newDateRange != null, "newDateRange needs to be set")
    checkPreconditions(newDateRange)
    calculateAvailabilityExcluding(bookingId, newDateRange).map { availability =>
      isDateRangeInsideAvailability(newDateRange, availability)
    }
  }

  private def checkPreconditions(dateRange: DateRangeVO): Unit = {
    if (dateRange.isOpen)
      throw new IllegalArgumentException("Cannot make (or update) reservation to an open date range")
    val now = LocalDate.now
    if (dateRange.from.isBefore(LocalDate.now))
      throw new IllegalArgumentException(s"Cannot book in the past, for ${dateRange}")
    dateRange.totalDays.map { total =>
      if (total > maxBookingDays)
        throw new IllegalArgumentException(s"Cannot book for more than ${maxBookingDays} days, for ${dateRange}")
    }
    val daysAhead = ChronoUnit.DAYS.between(now, dateRange.from)
    if (!(daysAhead >= minDaysAhead && (maxDaysAhead.equals(-1) || daysAhead <= maxDaysAhead)))
      throw new IllegalArgumentException(s"Minimum ${minDaysAhead} day(s) ahead of arrival and up to ${maxDaysAhead} days in advance, for ${dateRange}")
  }

  private def isDateRangeInsideAvailability(dateRange: DateRangeVO, availability: AvailabilityVO): Boolean = {
    if (availability.datesAvailable != null)
      for (availableDateRange <- availability.datesAvailable)
        if (dateRange.isInsideRange(availableDateRange)) return true
    false
  }
}