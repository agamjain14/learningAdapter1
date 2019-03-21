package net.ajn.credentialutil.svc.impl

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}
import net.ajn.credentialutil.svc.ifaces.CredentialService
import net.ajn.credentialutil.svc.models.Constants

import scala.concurrent.ExecutionContext

object TokenServiceFactory {

  val config : Config = ConfigFactory.load



  def getDefaultImplementation(): String = {
    config.getString(Constants.DEFAULT_CLIENT_CONFIG_PARAM)

  }

  def getInstance(impl: String = getDefaultImplementation())(implicit system: ActorSystem, ec: ExecutionContext, materializer: ActorMaterializer): CredentialService = impl match {
    case Constants.AKKA_SERVICE_ID => AkkaCredentialService.getInstance(config)
    case Constants.APACHE_SERVICE_ID => AkkaCredentialService.getInstance(config)
  }
}