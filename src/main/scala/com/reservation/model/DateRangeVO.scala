package com.reservation.model

import java.time.LocalDate
import java.time.temporal.ChronoUnit

case class DateRangeVO(from: LocalDate = LocalDate.now(), to: Option[LocalDate] = None) extends Ordered[DateRangeVO] {
  this.to.map { toDate =>
    require(toDate.isAfter(this.from), s"Invalid date range, from: ${this.from}, to: ${toDate}")
  }

  def isOpen = !this.to.isDefined

  def totalDays: Option[Long] = this.to.map(ChronoUnit.DAYS.between(this.from, _))

  def isInsideRange(inThisDateRange: DateRangeVO) = {
    (this.from.isEqual(inThisDateRange.from) || this.from.isAfter(inThisDateRange.from) && (inThisDateRange.isOpen
      || this.to.map { toDate =>
      (toDate.isEqual(inThisDateRange.to.get) || toDate.isBefore(inThisDateRange.to.get))
    }.get))
  }

  def minus(other: DateRangeVO): (Option[DateRangeVO], Option[DateRangeVO]) = {
    if (this.to.get.isBefore(other.from) || this.from.isAfter(other.to.get))
      (None, Some(DateRangeVO(this.from, this.to)))
    else if (this.from.isBefore(other.from) && this.to.get.isAfter(other.to.get))
      (Some(DateRangeVO(this.from, Some(other.from))), Some(DateRangeVO(other.to.get, this.to)))
    else if (this.from.isAfter(other.from) && this.to.get.isAfter(other.to.get))
      (None, Some(DateRangeVO(other.to.get, this.to)))
    else if (this.from.isBefore(other.from) && this.to.get.isBefore(other.to.get))
      (Some(DateRangeVO(this.from, Some(other.from))), None)
    else (None, None)
  }

  override def compare(that: DateRangeVO): Int = {
    if (this.from.equals(that.from)) {
      if ((this.to == null && that.to == null) || this.to.equals(that.to)) return 0
      if (this.to == null && that.to != null) return 1
      if (this.to != null && that.to == null) return -1
    }
    this.from.compareTo(that.from)
  }

  override def equals(obj: Any): Boolean = {
    val vo = obj.asInstanceOf[DateRangeVO]
    val cond1 = (this.from != null && vo.from != null && this.from.isEqual(vo.from)) || (this.from == null && vo.from == null)
    val cond2 = (this.to.isDefined && vo.to.isDefined && this.to.get.isEqual(vo.to.get)) || (this.to == None && vo.to == None)
    cond1 && cond2
  }

  override def hashCode: Int = this.from.hashCode * this.to.hashCode
}