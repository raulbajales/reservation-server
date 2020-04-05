package com.reservation.model

case class Booking(id: String, email: String = "", fullName: String = "", dateRange: DateRangeVO = DateRangeVO())