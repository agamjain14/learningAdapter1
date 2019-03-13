package net.ajn.credentialutil.svc.models

import net.ajn.credentialutil.svc.ifaces.AuthTypes.AuthType
import net.ajn.credentialutil.svc.ifaces.ContentTypes.ContentType
import net.ajn.credentialutil.svc.ifaces.GrantTypes.{ClientCredentials, GrantType, SAMLBearerAssertion}



trait TokenRequest {
  def userId: String
  def tokenEndpoint: String
  def clientId: String
  // def grantType: String
  def authType: AuthType
  def grantType: GrantType
  def contentType: ContentType
  def clientSecret: String

  def stringifyRequestBody : String
  def getProxy: Option[Proxy]


}

trait TenantId {
  def tenantId: String
}

trait ClientCredentialsRequest extends TokenRequest {
  final val grantType = ClientCredentials
}

trait SAMLBearerAssertionRequest extends TokenRequest {
  final val grantType = SAMLBearerAssertion

  def idpEndpoint: String
  def idpContentType: ContentType
  def idpAuthType: AuthType
  def idpUser: String
  def idpPassword: String
  def stringifyIdpPayload: String
}

