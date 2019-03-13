package net.ajn.credentialutil.svc.impl

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.{Http, HttpExt}
import akka.stream.ActorMaterializer
import akka.util.ByteString
import com.typesafe.config.Config
import io.circe.generic.auto._
import io.circe.parser.parse
import net.ajn.credentialutil.svc.ifaces.CredentialService
import net.ajn.credentialutil.svc.models.{Token, TokenRequest}
import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}

class AkkaCredentialService(client: HttpExt)(implicit system: ActorSystem, materializer: ActorMaterializer) extends CredentialService {
  override def getToken(request: TokenRequest)(implicit ec: ExecutionContext): Future[Token] = {

    for {
        HttpResponse(_,_,entity,_) <- client.singleRequest(HttpRequest(uri = request.tokenEndpoint, method = HttpMethods.POST, entity = request.stringifyRequestBody))
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
  def getInstance(config: Config)(implicit system: ActorSystem, materializer: ActorMaterializer): CredentialService = {
    val http = Http()
    // NEEDED TO ADD HTTP PROXY LAYER
    new AkkaCredentialService(http)
  }
}