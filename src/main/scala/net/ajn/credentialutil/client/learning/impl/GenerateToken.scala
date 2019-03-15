package net.ajn.credentialutil.client.learning.impl

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import net.ajn.credentialutil.svc.ifaces.CredentialService
import net.ajn.credentialutil.svc.impl.TokenServiceFactory
import net.ajn.credentialutil.svc.models.TokenRequest

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class GenerateToken(request: TokenRequest)(implicit system: ActorSystem, materializer: ActorMaterializer) {

  def getInstance()(implicit ec: ExecutionContext)= {
    getToken(TokenServiceFactory.getInstance())
  }

  private def getToken(tokenService: CredentialService)(implicit ec: ExecutionContext) = {
    tokenService.getToken(request).onComplete {
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
}

object GenerateToken {
  def init()(implicit system: ActorSystem, materializer: ActorMaterializer)= {
    val request: TokenRequest = CreateLearningRequest.createRequest()
    new GenerateToken(request)
  }
}