import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import net.ajn.credentialutil.svc.ifaces.AuthTypes.AuthType
import net.ajn.credentialutil.svc.ifaces.ContentTypes.ContentType
import net.ajn.credentialutil.svc.ifaces.GrantTypes.ClientCredentials
import net.ajn.credentialutil.svc.ifaces.{AuthTypes, ContentTypes}
import net.ajn.credentialutil.svc.impl.TokenServiceFactory
import net.ajn.credentialutil.svc.models.{ClientCredentialsRequest, TenantId}
import io.circe._, io.circe.parser._
import scala.concurrent.ExecutionContext
// import net.ajn.credentialutil.client.adapter.Adapter
import io.circe.syntax._

object Main {


  private val config = ConfigFactory.load()
  def main(args: Array[String]): Unit = {

    //implicit val system: ActorSystem = ActorSystem(config.getString("ncf.adapters.success-factors.actor-system-name"), config)


    implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val tokenService = TokenServiceFactory.getInstance()

    val tokenRequest = LearningTokenRequest(
      userId = TokenServiceFactory.config.getString("adapter.learning.sso.scope.userId"),
      tokenEndpoint = TokenServiceFactory.config.getString("adapter.learning.serviceUri"),
      clientId = TokenServiceFactory.config.getString("adapter.learning.sso.username"),
      authType = AuthTypes.BasicAuth,
      contentType = ContentTypes.Json,
      tenantId = TokenServiceFactory.config.getString("adapter.learning.sso.scope.companyId"),
      clientSecret = TokenServiceFactory.config.getString("adapter.learning.sso.pwd")
    )
    tokenService.getToken(tokenRequest)
    //tokenRequest.stringifyRequestBody
    //println(tokenRequest)
  }



  case class LearningTokenRequest(userId: String,
   tokenEndpoint: String,
    clientId: String,
    authType: AuthType,
    contentType: ContentType,
    tenantId: String,
    clientSecret: String) extends ClientCredentialsRequest  with TenantId {


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
        println("test --> " + res.toString())
        res.toString()
    }


  }



}
