package net.atos.ncf.common.objs

import scala.collection.mutable.ListBuffer

case class NCFActionnableTaskCapabilities(
    supportsClaim: Boolean,
    supportsRelease: Boolean,
    supportsForward: Boolean,
    supportsUserDecisions: Seq[NCFActionDefinition]
) {

  def isActionable = supportsUserDecisions.nonEmpty

  override def toString = {
    val buffer = new ListBuffer[String]
    if (supportsClaim) {
      buffer.append("claim")
    }
    if (supportsRelease) {
      buffer.append("release")
    }
    if (supportsForward) {
      buffer.append("forward")
    }
    if (supportsUserDecisions.nonEmpty) {
      buffer.append("user-decisions(" + supportsUserDecisions.mkString(",") + ")")
    }
    buffer.mkString("NCFActionnableTaskCapabilities(", ", ", ")")
  }
}
