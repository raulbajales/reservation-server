package com.reservation.model

import java.time.LocalDate

case class AvailabilityVO(inThisDateRange: DateRangeVO, datesAvailable: Set[DateRangeVO])

case class AvailabilityVOBuilder(inThisDateRange: DateRangeVO) {
  var datesAvailable: Set[DateRangeVO] = Set[DateRangeVO]()

  def addRange(from: LocalDate, to: Option[LocalDate]): AvailabilityVOBuilder = {
    require(from != null, "from needs to be set")
    addRange(DateRangeVO(from, to))
  }

  def addRange(dateRange: DateRangeVO): AvailabilityVOBuilder = {
    require(dateRange != null, "dateRange needs to be set")
    require(dateRange.isInsideRange(this.inThisDateRange), s"Cannot add date range ${dateRange} because it's out of availability range ${this.inThisDateRange}")
    if (this.datesAvailable.isEmpty)
      this.datesAvailable += dateRange
    else {
      require(!this.datesAvailable.last.isOpen, s"Cannot add date range ${dateRange} into ${this.datesAvailable} because last is open")
      this.datesAvailable += dateRange
    }
    this
  }

  def build: AvailabilityVO = AvailabilityVO(this.inThisDateRange, this.datesAvailable)
}