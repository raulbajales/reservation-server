package com.reservation.controller

import akka.actor.testkit.typed.scaladsl.ActorTestKit
import akka.actor.typed.scaladsl.adapter._
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.reservation.ReservationModule.ConcreteReservation
import com.reservation.model.AvailabilityVO
import com.reservation.service.actor.ReservationProtocol
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Matchers, WordSpec}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import com.reservation.JsonFormats._

class ReservationControllerSpec extends WordSpec with Matchers with ScalaFutures with ScalatestRouteTest {

  lazy val testKit = ActorTestKit()

  implicit def typedSystem: ActorSystem[Nothing] = testKit.system

  override def createActorSystem(): akka.actor.ActorSystem = testKit.system.toClassic

  val reservationActor: ActorRef[ReservationProtocol.Command] = testKit.spawn(ConcreteReservation.behavior)
  lazy val routes: Route = new ReservationController(reservationActor).routes

  "ReservationController" should {

    "look for availability for the next month when no from/to parameters are set (GET /reservations/find-availability)" in {
      val request = HttpRequest(uri = "/reservations/find-availability")
      request ~> routes ~> check {
        status should ===(StatusCodes.OK)
        contentType should ===(ContentTypes.`application/json`)
        entityAs[AvailabilityVO] should ===("""{"availability":[]}""")
      }
    }

  }
}
