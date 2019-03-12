package net.ajn.credentialutil.svc.ifaces

object AuthTypes {

  sealed trait AuthType

  case object BasicAuth extends AuthType

  case object PayloadAuth extends AuthType

  case object BearerAuth extends AuthType

}
