package net.ajn.credentialutil.svc.impl

import java.net.InetSocketAddress

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{Accept, Authorization, BasicHttpCredentials}
import akka.http.scaladsl.settings.ConnectionPoolSettings
import akka.http.scaladsl.{ClientTransport, Http, HttpExt}
import akka.stream.ActorMaterializer
import akka.util.ByteString
import com.typesafe.config.Config
import io.circe.generic.auto._
import io.circe.parser.parse
import net.ajn.credentialutil.svc.ifaces.{AuthTypes, CredentialService}
import net.ajn.credentialutil.svc.models.{Proxy, Token, TokenRequest}

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}

class AkkaCredentialService(client: AkkaHttpService)(implicit system: ActorSystem, materializer: ActorMaterializer) extends CredentialService {

  override def close() : Future[Unit] = client.close()

  override def getToken(request: TokenRequest)(implicit ec: ExecutionContext): Future[Token] = {

    val authorization  = request.authType match {
      case AuthTypes.BasicAuth => headers.Authorization(BasicHttpCredentials(request.clientId, request.clientSecret))
      case AuthTypes.BearerAuth => null
      case AuthTypes.PayloadAuth => null
    }
    val authHeaders = List(authorization, Accept(MediaTypes.`application/json`))
    val httpRequest = HttpRequest(method = HttpMethods.POST, uri = Uri(request.tokenEndpoint), headers = authHeaders, entity = HttpEntity(MediaTypes.`application/json`, request.stringifyRequestBody))
    for {
        HttpResponse(_,_,entity,_) <- client.singleRequest(httpRequest)
        stringEntity <- loadEntity(entity)
        token <- parseToken(stringEntity)
    } yield token
  }

  private def loadEntity(entity: HttpEntity)(implicit ec: ExecutionContext): Future[String] =
    for {
      strict <- entity.toStrict(500.millisecond)
      bytes <- strict.dataBytes.runFold(ByteString.empty)(_ ++ _)
    } yield bytes.decodeString("UTF-8")

  private def parseToken(data: String): Future[Token] = {
    val accessToken = for {
      json <- parse(data).right
      token <- json.as[Token].right
    } yield token

    accessToken match {
      case Right(value) => Future.successful(value)
      case Left(error) => Future.failed(new RuntimeException(error))
    }
  }
}

object AkkaCredentialService {
  def getInstance(config: Config)(implicit system: ActorSystem, ec: ExecutionContext, materializer: ActorMaterializer): CredentialService = {

    val http = Http()
    createInstance(config,http)
  }


  def getAnInstanceFromHttpExt(config: Config, http: HttpExt)(implicit system: ActorSystem, ec: ExecutionContext, materializer: ActorMaterializer): CredentialService = {
      createInstance(config,http)
  }

  private def createInstance(config: Config, http: HttpExt)(implicit system: ActorSystem, ec: ExecutionContext, materializer: ActorMaterializer) ={
    if (config.getBoolean("sts.proxyEnable")) {
      val host = config.getString("sts.proxy.host")
      val port = config.getInt("sts.proxy.port")
      val address = InetSocketAddress.createUnresolved(host,port)
      new AkkaCredentialService(AkkaHttpService(http,Some(address)))
    }
    else {
      new AkkaCredentialService(AkkaHttpService(http, None))
    }
  }
}