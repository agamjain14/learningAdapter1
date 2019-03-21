package net.atos.ncf.common.objs

import net.atos.ncf.common.objs.NCFLinkType.NCFLinkType

case class NCFExternalLink(url: String, rel: Option[String], linkType: NCFLinkType, displayName: Option[String], description: Option[String]) {
  override def toString = {
    val builder = new StringBuilder()
    builder.append("NCFExternalLink(url = '")
    builder.append(url)
    builder.append("'")
    rel.foreach { v =>
      builder.append(", rel = '")
      builder.append(v)
      builder.append("'")
    }
    builder.append(", linType = '")
    builder.append(linkType)
    builder.append("'")
    displayName.foreach { v =>
      builder.append(", display-name = '")
      builder.append(v)
      builder.append("'")
    }
    description.foreach { v =>
      builder.append(", description = '")
      builder.append(v)
      builder.append("'")
    }
    builder.append(")")
    builder.toString()
  }
}
