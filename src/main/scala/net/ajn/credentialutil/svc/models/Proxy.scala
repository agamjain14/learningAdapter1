package net.ajn.credentialutil.svc.models

import net.ajn.credentialutil.svc.ifaces.ProxyAuthTypes.ProxyAuthType

case class Proxy (host: String, port: Int, protocol: String, userId: Option[String], pwd: Option[String], authType : ProxyAuthType )
