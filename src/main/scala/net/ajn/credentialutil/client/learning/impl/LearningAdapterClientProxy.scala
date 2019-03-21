package net.ajn.credentialutil.client.learning.impl

import java.net.URI
import java.util.concurrent.ScheduledExecutorService

import akka.http.javadsl.model.Uri
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.StrictLogging
import net.ajn.credentialutil.client.utils.JFutureConverter
import net.ajn.credentialutil.svc.ifaces.CredentialService
import net.ajn.credentialutil.svc.models.Token
import org.apache.olingo.client.api.ODataClient
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest
import org.apache.olingo.client.api.domain.ClientEntitySet
import org.apache.olingo.client.core.ODataClientFactory
import org.apache.olingo.client.core.http.ProxyWrappingHttpClientFactory

import scala.compat.java8.FutureConverters
import scala.concurrent.{ExecutionContext, Future}

class LearningAdapterClientProxy(client: ODataClient, sts: CredentialService)(implicit EC: ScheduledExecutorService) extends StrictLogging {
  def readFeed(feedRequest: ODataFeedRequest)(implicit ec: ExecutionContext): Future[ClientEntitySet] = {
    for {
      token <- sts.getToken(feedRequest.tokenRequestGenerator(feedRequest.userId.get))
      response <- FutureConverters.toScala(JFutureConverter(buildRequest(client, feedRequest, token, buildFeedURI(client, feedRequest)).asyncExecute()))
    } yield response.getBody
  }

  def readEntry(entryRequest: EntryRequest)(implicit ec: ExecutionContext) = {
    for {
      token <- sts.getToken(entryRequest.tokenRequestGenerator(entryRequest.userId.get))
      response <- FutureConverters.toScala(JFutureConverter(buildRequest(client, entryRequest, token, buildEntryUri(client, entryRequest)).asyncExecute()))
    } yield response.getBody
  }

  def buildRequest(client: ODataClient, feedRequest: Request, token: Token, uri: URI): ODataEntitySetRequest[ClientEntitySet] = {
    val httpRequest = client.getRetrieveRequestFactory.getEntitySetRequest(uri)
    httpRequest.setAccept(feedRequest.format.get)
    httpRequest.addCustomHeader("Authorization",s"Bearer ${token.access_token}")
    httpRequest
  }

  def buildFeedURI(client: ODataClient, feedRequest: ODataFeedRequest) = {
    val builder = client.newURIBuilder(feedRequest.endpoint).appendEntitySetSegment(feedRequest.collection)

    feedRequest.filter match {
      case Some(f) => builder.filter(f)
      case None =>
    }

    feedRequest.select match {
      case Some(s) => builder.select(s: _*)
      case None =>
    }

    feedRequest.expand match {
      case Some(e) => builder.expand(e: _*)
      case None =>
    }
    builder.build()
  }


  def buildEntryUri(client: ODataClient, entryRequest: EntryRequest) = {
    val builder = client.newURIBuilder(entryRequest.endpoint).appendEntitySetSegment(entryRequest.collection).appendEntityIdSegment(entryRequest.entryId)

    builder.build()
  }
}


object LearningAdapterClientProxy {
  def getInstance(config: Config, sts: CredentialService)(implicit pollingExecutor: ScheduledExecutorService, ec: ExecutionContext) = {

    new LearningAdapterClientProxy(initODataClient(config), sts)
  }

  private def initODataClient(config: Config) = {
    val client = ODataClientFactory.getClient
    if (config.getBoolean("adapter.learning.proxyEnable")) {
      client.getConfiguration.setHttpClientFactory(new ProxyWrappingHttpClientFactory(URI.create(s"http://${config.getString("adapter.learning.proxy.host")}:${config.getInt("adapter.learning.proxy.port").toString}")))
    }
    client
  }
}