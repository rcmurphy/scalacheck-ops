package org.scalacheck.ops.time

import java.time.temporal._
import java.time.{Duration, Instant}

object JavaInstantGenerators extends JavaInstantGenerators
trait JavaInstantGenerators extends AbstractTimeGenerators {
  override type InstantType = Instant
  override type DurationType = TemporalAmount
  override type ParamsType = JavaTimeParams

  override protected[time] def datetime(millis: Long)
    (implicit params: JavaTimeParams): Instant = Instant.ofEpochMilli(millis)

  override protected[time] def duration(millis: Long): TemporalAmount = Duration.ofMillis(millis)

  override protected[time] def millis(duration: TemporalAmount): Long = duration.get(ChronoUnit.MILLIS)

  override protected[time] def millis(datetime: Instant)
    (implicit params: JavaTimeParams): Long = datetime.toEpochMilli

  override protected[time] def addToCeil(
    datetime: Instant,
    duration: TemporalAmount
  )(implicit params: JavaTimeParams): Instant = {
    try datetime plus duration
    catch {
      case ex: ArithmeticException => Instant.MAX
    }
  }

  override protected[time] def subtractToFloor(
    datetime: Instant,
    duration: TemporalAmount
  )(implicit params: JavaTimeParams): Instant = {
    try datetime minus duration
    catch {
      case ex: ArithmeticException => Instant.MIN
    }
  }

  override def defaultParams: JavaTimeParams = JavaTimeParams.isoUTC
}
