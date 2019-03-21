package net.ajn.credentialutil.client.learning.impl

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}
import io.circe.Json
import io.circe.parser.parse
import net.ajn.credentialutil.svc.ifaces.AuthTypes.AuthType
import net.ajn.credentialutil.svc.ifaces.ContentTypes.ContentType
import net.ajn.credentialutil.svc.ifaces.GrantTypes.ClientCredentials
import net.ajn.credentialutil.svc.ifaces.{AuthTypes, ContentTypes, CredentialService, ProxyAuthTypes}
import net.ajn.credentialutil.svc.impl.TokenServiceFactory
import net.ajn.credentialutil.svc.models.{ClientCredentialsRequest, Proxy, TenantId, Token, TokenRequest}

import scala.concurrent.{ExecutionContext, Future}


trait TokenProvider {
  def getToken(userId: String)(implicit ec: ExecutionContext): Future[Token]
}

class LearningTokenProvider(sts: CredentialService, requestGenerator: String => TokenRequest) extends TokenProvider{
  def getToken(userId: String)(implicit ec: ExecutionContext): Future[Token] = sts.getToken(requestGenerator(userId))

  def getSts(): CredentialService = sts


}

object LearningTokenProvider {



  val config : Config = ConfigFactory.load


  def requestGenerator = (userId: String) => LearningTokenRequest(
    userId = userId,
    tokenEndpoint = config.getString("adapter.learning.tokenEndPoint"),
    clientId = config.getString("adapter.learning.sso.username"),
    authType = AuthTypes.BasicAuth,
    contentType = ContentTypes.Json,
    tenantId = config.getString("adapter.learning.sso.scope.companyId"),
    clientSecret = config.getString("adapter.learning.sso.pwd")
  )


  def getInstance()(implicit system: ActorSystem, materializer: ActorMaterializer,executionContext: ExecutionContext): LearningTokenProvider = {



    val tokenService = TokenServiceFactory.getInstance()



    new LearningTokenProvider(tokenService, requestGenerator)

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
      s"""
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

      /*val res: Json = parse(json).getOrElse(Json.Null)
      res.toString()*/
    }
  }




}



