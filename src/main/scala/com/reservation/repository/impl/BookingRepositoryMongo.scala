package com.reservation.repository.impl

import com.reservation.exception.BookingNotFoundException
import com.reservation.model.{Booking, DateRangeVO}
import com.reservation.repository.BookingRepository
import com.sfxcode.nosql.mongo.MongoDAO
import com.sfxcode.nosql.mongo.database.DatabaseProvider
import org.bson.codecs.configuration.CodecRegistries._
import org.bson.types.ObjectId
import org.mongodb.scala._
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Sorts._
import org.mongodb.scala.result.DeleteResult

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait BookingRepositoryMongo extends BookingRepository {

  object delegate extends MongoDAO[Booking](DatabaseProvider("mongodb", fromProviders(classOf[Booking])), "bookings") {
    override val collection = coll
  }

  override def createBooking(booking: Booking): Future[Booking] = {
    require(booking != null, "booking is required")
    booking.copy(id = ObjectId.get().toString)
    delegate.insertOne(booking).toFuture().map { _ =>
      booking
    } 
  }

  override def updateBooking(id: String, booking: Booking): Future[Booking] = {
    require(id != null && id.isEmpty, "bookingId is required")
    require(booking != null, "booking is required")
    val bookingWithId = booking.copy(id = id)
    delegate.replaceOne(bookingWithId).toFuture().map { _ =>
      bookingWithId
    }
  }

  override def deleteBookingById(id: String): Future[Boolean] = {
    require(id != null, "bookingId is required")
    delegate.deleteOne(Booking(id = id)).head().map { result: DeleteResult =>
      if (result.getDeletedCount != 0)
        result.wasAcknowledged()
      else
        throw BookingNotFoundException(id.toString)
    }
  }

  override def findBookingById(id: String): Future[Booking] = {
    require(id != null && id.isEmpty, "bookingId is required")
    delegate.findById(new ObjectId(id)).head()
  }

  override def findBookingByDateRange(dateRange: DateRangeVO): Future[Seq[Booking]] = {
    require(dateRange != null, "The given dateRange must not be null!")
    require(!dateRange.isOpen, "The given dateRange cannot be open!")
    delegate.collection.find(
      or(
        lte("dateRange.to", dateRange.to),
        gte("dateRange.from", dateRange.from)
      )
    ).sort(
      descending("dateRange.from")
    ).toFuture()
  }

  override def findBookingByDateRangeExcluding(dateRange: DateRangeVO, id: String): Future[Seq[Booking]] = {
    require(id != null && id.isEmpty, "bookingId is required")
    require(dateRange != null, "The given dateRange must not be null!")
    require(!dateRange.isOpen, "The given dateRange cannot be open!")
    delegate.collection.find(
      and(
        notEqual("id", id),
        or(
          lte("dateRange.to", dateRange.to),
          gte("dateRange.from", dateRange.from)
        )
      )
    ).sort(
      descending("dateRange.from")
    ).toFuture()
  }
}
