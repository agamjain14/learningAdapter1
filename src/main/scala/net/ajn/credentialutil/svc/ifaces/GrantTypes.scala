package net.ajn.credentialutil.svc.ifaces

object GrantTypes {
  sealed trait GrantType

  case object ClientCredentials extends  GrantType {
    override def toString(): String = {
      "client_credentials"
    }
  }

  case object SAMLBearerAssertion extends GrantType {
    override def toString: String = {
      "urn:ietf:params:oauth:grant-type:saml2-bearer"
    }
  }
}
