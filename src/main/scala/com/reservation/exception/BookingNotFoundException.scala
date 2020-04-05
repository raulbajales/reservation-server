package com.reservation.exception

case class BookingNotFoundException(id: String) extends RuntimeException
