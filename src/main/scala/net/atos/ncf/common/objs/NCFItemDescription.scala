package net.atos.ncf.common.objs

object NCFItemDescription {

  case object None extends NCFItemDescription {
    override def content = ""

    override def toString = s"Description(empty)"
  }

  case class PlainText(content: String) extends NCFItemDescription {
    override def toString = s"Description('$content')"
  }

  case class RichText(content: String, mimeType: String) extends NCFItemDescription {
    override def toString = s"Description[$mimeType]('$content')"
  }

}

sealed trait NCFItemDescription {
  def content: String
}
