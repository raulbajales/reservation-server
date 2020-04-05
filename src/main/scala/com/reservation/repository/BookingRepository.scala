package com.reservation.repository

import com.reservation.model.{Booking, DateRangeVO}

import scala.concurrent.Future

trait BookingRepository {

  def createBooking(booking: Booking): Future[Booking]

  def updateBooking(id: String, booking: Booking): Future[Booking]

  def deleteBookingById(id: String): Future[Boolean]

  def findBookingById(id: String): Future[Booking]

  def findBookingByDateRange(dateRange: DateRangeVO): Future[Seq[Booking]]

  def findBookingByDateRangeExcluding(dateRange: DateRangeVO, id: String): Future[Seq[Booking]]
}