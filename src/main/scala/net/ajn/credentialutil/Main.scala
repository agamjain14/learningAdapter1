import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import io.circe._
import io.circe.parser._
import net.ajn.credentialutil.svc.ifaces.AuthTypes.AuthType
import net.ajn.credentialutil.svc.ifaces.ContentTypes.ContentType
import net.ajn.credentialutil.svc.ifaces.GrantTypes.ClientCredentials
import net.ajn.credentialutil.svc.ifaces.{AuthTypes, ContentTypes, ProxyAuthTypes}
import net.ajn.credentialutil.svc.impl.TokenServiceFactory
import net.ajn.credentialutil.svc.models.{ClientCredentialsRequest, Proxy, TenantId}

import scala.util.{Failure, Success}

object Main {


  val config = TokenServiceFactory.config

  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher


    val tokenService = TokenServiceFactory.getInstance()

    val tokenRequest = LearningTokenRequest(
      userId = config.getString("adapter.learning.sso.scope.userId"),
      tokenEndpoint = config.getString("adapter.learning.serviceUri"),
      clientId = config.getString("adapter.learning.sso.username"),
      authType = AuthTypes.BasicAuth,
      contentType = ContentTypes.Json,
      tenantId = config.getString("adapter.learning.sso.scope.companyId"),
      clientSecret = config.getString("adapter.learning.sso.pwd")
    )
    tokenService.getToken(tokenRequest).onComplete {
      case Success(value) =>

        println(value.access_token)


      case Failure(exception) => println(exception)
    }
    println("Press Enter to Finish")
    scala.io.StdIn.readLine()
    tokenService.close().onComplete {
      case _ => materializer.shutdown()
                system.terminate()
    }

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
