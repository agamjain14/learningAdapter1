package net.ajn.credentialutil.svc.impl

import java.net.InetSocketAddress

import akka.actor.ActorSystem
import akka.http.scaladsl.{ClientTransport, HttpExt}
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.settings.ConnectionPoolSettings

import scala.concurrent.{ExecutionContext, Future}

trait AkkaHttpService {
  def singleRequest(request: HttpRequest): Future[HttpResponse]
  def httpExt() : HttpExt
  def close(): Future[Unit]
}

object AkkaHttpService {


  class DirectAkkaHttpService(ext: HttpExt) extends AkkaHttpService {
    override def singleRequest(request: HttpRequest): Future[HttpResponse] = ext.singleRequest(request)

    override def httpExt(): HttpExt = ext

    override def close(): Future[Unit] = ext.shutdownAllConnectionPools()
  }


  class ProxyAkkaHttpService(ext: HttpExt, address: InetSocketAddress)(implicit system: ActorSystem) extends  AkkaHttpService {
    val settings =  ConnectionPoolSettings(system).withTransport(ClientTransport.httpsProxy(address))

    override def singleRequest(request: HttpRequest): Future[HttpResponse] = ext.singleRequest(request = request, settings = settings)

    override def httpExt(): HttpExt = ext

    override def close(): Future[Unit] = ext.shutdownAllConnectionPools()
  }


  def apply(http: HttpExt, proxy: Option[InetSocketAddress])(implicit sysytem: ActorSystem, executionContext: ExecutionContext): AkkaHttpService = {
    proxy match {
      case Some(address) => new ProxyAkkaHttpService(http, address)
      case None => new DirectAkkaHttpService(http)
    }
  }
}
