package net.ajn.credentialutil.svc.ifaces

object ContentTypes {
  sealed trait ContentType

  case object Json extends ContentType {
    override def toString: String = {
      "application/json"
    }
  }

  case object URLFormEncoded extends ContentType {
    override def toString: String = {
      "application/x-www-form-urlencoded"
    }
  }

  case object XML extends ContentType {
    override def toString: String = {
      "application/xml"
    }
  }


  case object PlainText extends ContentType {
    override def toString: String = {
      "text/plain"
    }
  }

}
