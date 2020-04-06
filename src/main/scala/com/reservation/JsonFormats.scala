package com.reservation

import java.time.LocalDate

import com.reservation.model.{AvailabilityVO, Booking, DateRangeVO}
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat, RootJsonFormat}

object JsonFormats {

  object LocalDateMarshalling {

    implicit object LocalDateFormat extends JsonFormat[LocalDate] {
      def write(localDate: LocalDate): JsString =
        JsString("%04d".format(localDate.getYear) + "-" + "%02d".format(localDate.getMonthValue) + "-" + "%02d".format(localDate.getDayOfMonth))

      def read(value: JsValue): LocalDate = value match {
        case JsString(value) => LocalDate.parse(value)
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
