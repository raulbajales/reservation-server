package com.reservation

import java.time.Duration

import com.typesafe.config.{Config, ConfigFactory}

trait AppConf {

  private lazy val conf: Config = ConfigFactory.load()

  //
  // Server configurations:
  //

  def routesAskTimeout: Duration = conf.getDuration("reservation-server.routes.ask-timeout")

  //
  // Domain specific configurations:
  //

  def maxBookingDays: Int = conf.getInt("reservation.max-booking-days")

  def minDaysAhead: Int = conf.getInt("reservation.min-days-ahead")

  def maxDaysAhead: Int = conf.getInt("reservation.max-days-ahead")

  def defaultMonthsForAvailabilityRequest: Int = conf.getInt("reservation.default-months-for-availability-request")

}
