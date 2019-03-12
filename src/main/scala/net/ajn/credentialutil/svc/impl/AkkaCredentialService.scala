package net.ajn.credentialutil.svc.impl

import akka.actor.ActorSystem
import akka.http.scaladsl.HttpExt
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.stream.ActorMaterializer
import com.typesafe.config.Config
import net.ajn.credentialutil.svc.ifaces.CredentialService
import net.ajn.credentialutil.svc.models.{Token, TokenRequest}

import scala.concurrent.{ExecutionContext, Future}

class AkkaCredentialService(client: HttpExt) extends CredentialService {
  override def getToken(request: TokenRequest)/*(implicit ec: ExecutionContext)*/: String = {

    println(request.stringifyRequestBody)

    // IMPLEMENT LOGIC TO FETCH TOKEN
    //val token = Token("a", _ , "1800")
    "s"
  }
}


object AkkaCredentialService {
  def getInstance(config: Config): CredentialService = {

  }
}