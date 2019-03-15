package net.ajn.credentialutil.client.learning.impl

import com.typesafe.config.{Config, ConfigFactory}
import io.circe.Json
import io.circe.parser.parse
import net.ajn.credentialutil.svc.ifaces.AuthTypes.AuthType
import net.ajn.credentialutil.svc.ifaces.ContentTypes.ContentType
import net.ajn.credentialutil.svc.ifaces.GrantTypes.ClientCredentials
import net.ajn.credentialutil.svc.ifaces.{AuthTypes, ContentTypes, ProxyAuthTypes}
import net.ajn.credentialutil.svc.models.{ClientCredentialsRequest, Proxy, TenantId, TokenRequest}

object CreateLearningRequest {
  val config : Config = ConfigFactory.load

  def createRequest() : TokenRequest = {
    LearningTokenRequest(
      userId = config.getString("adapter.learning.sso.scope.userId"),
      tokenEndpoint = config.getString("adapter.learning.serviceUri"),
      clientId = config.getString("adapter.learning.sso.username"),
      authType = AuthTypes.BasicAuth,
      contentType = ContentTypes.Json,
      tenantId = config.getString("adapter.learning.sso.scope.companyId"),
      clientSecret = config.getString("adapter.learning.sso.pwd")
    )
  }

  case class LearningTokenRequest(userId: String,
                                  tokenEndpoint: String,
                                  clientId: String,
                                  authType: AuthType,
                                  contentType: ContentType,
                                  tenantId: String,
                                  clientSecret: String) extends ClientCredentialsRequest  with TenantId {

    override def getProxy : Option[Proxy] = {
      config.getBoolean("adapter.learning.proxyEnable") match {
        case true => {
          Some(Proxy(config.getString("adapter.learning.proxy.host"),config.getInt("adapter.learning.proxy.port"), "http", None, None, ProxyAuthTypes.None ))
        }
        case false => {
          None
        }
      }
    }
    override def stringifyRequestBody: String = {
      val json = s"""
                    |{
                    |    "grant_type": "${ClientCredentials.toString()}",
                    |    "scope" : {
                    |		  "userId": "$userId",
                    |		  "companyId": "$tenantId",
                    |		  "userType": "user",
                    |		  "resourceType": "learning_public_api"
                    |		}
                    |  }
        """.stripMargin

      val res: Json = parse(json).getOrElse(Json.Null)
      res.toString()
    }
  }

}
