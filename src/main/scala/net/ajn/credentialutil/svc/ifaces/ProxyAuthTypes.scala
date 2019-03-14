package net.ajn.credentialutil.svc.ifaces

object ProxyAuthTypes {

  sealed trait ProxyAuthType

  case object None extends ProxyAuthType

  case object Basic extends ProxyAuthType

  case object NTLM extends ProxyAuthType
}
