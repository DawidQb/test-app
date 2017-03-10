package pl.dawid.calculator.model

import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.DefaultFormats
import org.json4s.native.Serialization


trait JsonMarshalling extends Json4sSupport {
  implicit val formats = DefaultFormats
  implicit val serialization = Serialization
}
