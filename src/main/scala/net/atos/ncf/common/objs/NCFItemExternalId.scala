package net.atos.ncf.common.objs

import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.EqualsBuilder

/**
 * Represents an item external id.
 */
case class NCFItemExternalId(fields: Map[String, String]) {
  override def toString = {
    fields.map {
      case (key, value) => s"$key = '$value'"
    }
      .mkString("NCFItemExternalId(", ", ", ")")
  }

  override def hashCode() = {
    val builder = new HashCodeBuilder()
    for ((key, value) <- fields) {
      builder.append(key -> value)
    }
    builder.toHashCode
  }

  override def equals(obj: Any) = {
    Option(obj) match {
      case Some(other: NCFItemExternalId) =>
        if (this.fields.size != other.fields.size) {
          false
        } else {
          this.fields.foldLeft(true) {
            case (true, (key, thisValue)) =>
              other.fields.get(key) match {
                case Some(`thisValue`) => true
                case _                 => false
              }
            case (false, _) => false
          }
        }
      case _ => false
    }

  }
}
