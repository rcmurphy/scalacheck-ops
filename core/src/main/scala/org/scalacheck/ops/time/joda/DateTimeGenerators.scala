package org.scalacheck.ops.time.joda

import org.joda.time.DateTime
import org.scalacheck.ops.time.joda.ChronologyOps._

sealed trait DateTimeGenerators extends JodaDateTimeGenerators with UTCTimeZoneDefault with ISOChronologyDefault {
  override type DateTimeType = DateTime

  override protected[time] def datetime(millis: Long)(implicit params: JodaTimeParams): DateTime =
    new DateTime(millis, params.chronology)

  override protected[time] def millis(datetime: DateTime)(implicit params: JodaTimeParams): Long = datetime.getMillis

  override protected[time] def addToCeil(datetime: DateTimeType, duration: DurationType)(implicit params: ParamsType): DateTimeType = {
    try datetime plus duration
    catch {
      case tooLarge: ArithmeticException => params.chronology.maxDateTime
    }
  }

  override protected[time] def subtractToFloor(datetime: DateTimeType, duration: DurationType)(implicit params: ParamsType): DateTimeType = {
    try datetime minus duration
    catch {
      case tooSmall: ArithmeticException => params.chronology.minDateTime
    }
  }

}

object DateTimeGenerators extends DateTimeGenerators
