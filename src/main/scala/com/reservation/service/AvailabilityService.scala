package com.reservation.service

import com.reservation.model.{AvailabilityVO, Booking, DateRangeVO}
import com.reservation.repository.BookingRepository

import scala.concurrent.{ExecutionContext, Future}

trait AvailabilityService {
  this: BookingRepository =>

  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  def calculateAvailability(inThisDateRange: DateRangeVO): Future[AvailabilityVO] = {
    require(inThisDateRange != null, "inThisDateRange needs to be set")
    findBookingByDateRange(inThisDateRange).flatMap { bookings =>
      calculateFor(inThisDateRange, bookings)
    }
  }

  def calculateAvailabilityExcluding(bookingId: String, inThisDateRange: DateRangeVO): Future[AvailabilityVO] = {
    require(inThisDateRange != null, "inThisDateRange needs to be set")
    require(bookingId != null && !bookingId.isEmpty, "bookingId needs to be set")
    findBookingByDateRangeExcluding(inThisDateRange, bookingId).flatMap { bookings =>
      calculateFor(inThisDateRange, bookings)
    }
  }

  def calculateFor(inThisDateRange: DateRangeVO, bookings: Seq[Booking]): Future[AvailabilityVO] = {
    val builder: AvailabilityVO.Builder = AvailabilityVO.Builder(inThisDateRange)
    var dateRangeToProcess: Option[DateRangeVO] = Some(inThisDateRange)
    Future {
      for (booking <- bookings) dateRangeToProcess.map { dateRange =>
        val pair = dateRange.minus(booking.dateRange)
        pair._1.map(builder.addRange)
        dateRangeToProcess = pair._2
      }
      dateRangeToProcess.map(builder.addRange)
      builder.build
    }
  }
}