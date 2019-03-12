package net.ajn.credentialutil.svc.impl

import com.typesafe.config.{Config, ConfigFactory}
import net.ajn.credentialutil.svc.ifaces.CredentialService
import net.ajn.credentialutil.svc.models.Constants

object TokenServiceFactory {

  val config : Config = ConfigFactory.load

  def getDefaultImplementation(): String = {
    config.getString(Constants.DEFAULT_CLIENT_CONFIG_PARAM)

  }

  def getInstance(impl: String = getDefaultImplementation()): CredentialService = impl match {
    case Constants.AKKA_SERVICE_ID => AkkaCredentialService.getInstance(config)
    case Constants.APACHE_SERVICE_ID => AkkaCredentialService.getInstance(config)
  }
}