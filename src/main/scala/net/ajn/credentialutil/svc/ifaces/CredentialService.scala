package net.ajn.credentialutil.svc.ifaces

import net.ajn.credentialutil.svc.models.{Token, TokenRequest}

import scala.concurrent.{ExecutionContext, Future}

trait CredentialService {
  def getToken(request: TokenRequest)(implicit ec: ExecutionContext): Future[Token]
  def close(): Future[Unit]
}


