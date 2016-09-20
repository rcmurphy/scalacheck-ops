package org.scalacheck.ops.time

import java.time.Instant

import org.scalacheck.Arbitrary

import scala.reflect.ClassTag

class JavaInstantGeneratorSpec extends GenericDateTimeGeneratorsSpec(JavaInstantGenerators, "JavaTimeInstantGenerators") {
  override protected val arbDateTimeType: Arbitrary[Instant] = implicitly
  override protected val clsTagDateTimeType: ClassTag[Instant] = implicitly
}
