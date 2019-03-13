package net.ajn.credentialutil.svc.impl

import java.net.InetSocketAddress

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{Accept, BasicHttpCredentials}
import akka.http.scaladsl.settings.ConnectionPoolSettings
import akka.http.scaladsl.{ClientTransport, Http, HttpExt}
import akka.stream.ActorMaterializer
import akka.util.ByteString
import io.circe.generic.auto._
import io.circe.parser.parse
import net.ajn.credentialutil.svc.ifaces.CredentialService
import net.ajn.credentialutil.svc.models.{Proxy, Token, TokenRequest}

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}

class AkkaCredentialService(client: HttpExt)(implicit system: ActorSystem, materializer: ActorMaterializer) extends CredentialService {

  override def close() : Future[Unit] = client.shutdownAllConnectionPools()


  def makeRequest(httpRequest: HttpRequest, proxy: Option[Proxy]): Future[HttpResponse] = {
    proxy match {
      case Some(proxy) => {
        val address = InetSocketAddress.createUnresolved(proxy.host, proxy.port)
        val proxySettings: ConnectionPoolSettings = ConnectionPoolSettings(system).withTransport(ClientTransport.httpsProxy(address))
        client.singleRequest(request = httpRequest, settings = proxySettings)
      }
      case None => client.singleRequest(request = httpRequest)
    }
  }

  override def getToken(request: TokenRequest)(implicit ec: ExecutionContext): Future[Token] = {

    val authorization = headers.Authorization(BasicHttpCredentials(request.clientId, request.clientSecret))
    val authHeaders = List(authorization, Accept(MediaTypes.`application/json`))
    val httpRequest = HttpRequest(method = HttpMethods.POST, uri = Uri(request.tokenEndpoint), headers = authHeaders, entity = HttpEntity(MediaTypes.`application/json`, request.stringifyRequestBody))

    for {
        HttpResponse(_,_,entity,_) <- makeRequest(httpRequest, request.getProxy)
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
  def getInstance()(implicit system: ActorSystem, materializer: ActorMaterializer): CredentialService = {
    val http = Http()
    new AkkaCredentialService(http)

  }
}