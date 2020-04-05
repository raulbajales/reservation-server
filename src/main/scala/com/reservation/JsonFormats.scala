package com.reservation

import java.time.LocalDate

import com.reservation.model.{AvailabilityVO, Booking, DateRangeVO}
import spray.json.{DefaultJsonProtocol, JsArray, JsNumber, JsValue, JsonFormat, RootJsonFormat}

object JsonFormats {

  object LocalDateMarshalling {

    implicit object LocalDateFormat extends JsonFormat[LocalDate] {
      def write(localDate: LocalDate): JsArray =
        JsArray(JsNumber(localDate.getYear), JsNumber(localDate.getMonthValue), JsNumber(localDate.getDayOfMonth))

      def read(value: JsValue): LocalDate = value match {
        case JsArray(Vector(JsNumber(year), JsNumber(month), JsNumber(day))) =>
          LocalDate.of(year.toInt, month.toInt, day.toInt)
        case _ => throw new RuntimeException("LocalDate expected")
      }
    }

  }

  import DefaultJsonProtocol._
  import LocalDateMarshalling._

  implicit val availabilityJsonFormat: RootJsonFormat[AvailabilityVO] = jsonFormat2(AvailabilityVO)
  implicit val dateRangeJsonFormat: RootJsonFormat[DateRangeVO] = jsonFormat2(DateRangeVO)
  implicit val bookingJsonFormat: RootJsonFormat[Booking] = jsonFormat4(Booking)
}
