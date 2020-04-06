package com.reservation.model

import java.time.LocalDate

import org.scalatest.{FlatSpec, Matchers}

class AvailabilityVOSpec extends FlatSpec with Matchers {

  "Builder" should "build a valid AvailabilityVO" in {
    val now = LocalDate.now()
    val builder = AvailabilityVOBuilder(DateRangeVO())
    builder.addRange(now, Some(now.plusDays(3)))
    builder.addRange(now.plusDays(5), Some(now.plusDays(10)))
    builder.addRange(now.plusDays(15), Some(now.plusDays(20)))
    val availabilityVO = builder.build
    availabilityVO.inThisDateRange.from.isEqual(now) should be (true)
    availabilityVO.datesAvailable.size should be (3)
  }

  "Builder" should "not add invalid range (out off range)" in {
    val now = LocalDate.now()
    val builder = AvailabilityVOBuilder(DateRangeVO(now, Some(now.plusMonths(1))))
    a [IllegalArgumentException] should be thrownBy {
      builder.addRange(now.plusYears(1), Some(now.plusYears(1).plusMonths(1)))
    }
  }

  "Builder" should "not add invalid range (last date range is open)" in {
    val now = LocalDate.now()
    val builder = AvailabilityVOBuilder(DateRangeVO(now, None))
    builder.addRange(now,Some(now.plusDays(2)))
    builder.addRange(now.plusDays(5),None)
    a [IllegalArgumentException] should be thrownBy {
      builder.addRange(now.plusDays(7), Some(now.plusDays(9)))
    }
  }
}
