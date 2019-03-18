import java.util.concurrent.{Executors, ScheduledExecutorService, ThreadFactory}

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import net.ajn.credentialutil.client.learning.impl.{LearningAdapterClientProxy, LearningFeedRequest, LearningTokenProvider, ODataFeedRequest}
import org.apache.olingo.client.api.domain.ClientEntitySet

import scala.collection.JavaConverters._

import scala.util.{Failure, Success}

object RunLearningAdapter {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher
    implicit val EC = Executors.newSingleThreadScheduledExecutor(
      new ThreadFactory() {
        override def newThread(runnable: Runnable): Thread = {
          val thread = new Thread(runnable, "Java Future Polling Thread")
          thread.setDaemon(false)
          thread
        }
      })

    /*val objectInstance: GenerateToken =  GenerateToken.init()
    objectInstance.getInstance().map(token => println(token))
    println("Press Enter to Finish")
    scala.io.StdIn.readLine()
    materializer.shutdown()
    system.terminate()*/


    def printFeed(feed: ClientEntitySet): Unit = {
      feed.getEntities().asScala.foreach(entity => entity.getProperties.asScala.foreach(p => println(s"${p.getName} = ${p.getValue.toString}")))
    }



    val config = ConfigFactory.load()
    val tokenProvider = LearningTokenProvider.getInstance()
    val adapter = LearningAdapterClientProxy.getInstance(config, tokenProvider)
    val odataFeedRequest = LearningFeedRequest.buildFromConfig(config, "todos")
    adapter.readFeed(odataFeedRequest.setUser("NL06715")).onComplete {
      case Success(feed) => printFeed(feed)
      case Failure(exception) => exception.printStackTrace()
    }
    println("###############################")
    val odataFeedRequest1 = LearningFeedRequest.buildFromConfig(config, "approvals")
    adapter.readFeed(odataFeedRequest1.setUser("NL06715")).onComplete {
      case Success(feed) => printFeed(feed)
      case Failure(exception) => exception.printStackTrace()
    }

  }




}
