import java.util.concurrent.{Executors, ThreadFactory}

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import net.ajn.credentialutil.client.learning.impl.LearningAdapterClientProxy
import net.ajn.credentialutil.client.ncf.{LearningContextManager, LearningQueryHandler}
import net.ajn.credentialutil.svc.impl.{AkkaCredentialService, TokenServiceFactory}
import net.atos.ncf.itemprovider.adapter.lib.objs.AdapterContext

import scala.util.{Failure, Success}

object RunLearningAdapter {

  def main(args: Array[String]): Unit = {


    /*val objectInstance: GenerateToken =  GenerateToken.init()
    objectInstance.getInstance().map(token => println(token))
    println("Press Enter to Finish")
    scala.io.StdIn.readLine()
    materializer.shutdown()
    system.terminate()*/



    /****

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
**************/

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
    val config = ConfigFactory.load()
    val sts = AkkaCredentialService.getInstance(config)
    val sourceId = "learning"
    val userId = "NL06715"

    val learningAdapterClientProxy = LearningAdapterClientProxy.getInstance(config, sts)
    val learningContextManager = new LearningContextManager(config, learningAdapterClientProxy)
    val sourceContext = learningContextManager.buildSourceContext(sourceId, LearningContextManager.sampleParamsMap)
    learningContextManager.buildUserContext(sourceId,userId,sourceContext).onComplete {
      case Success(userContext) =>
        val adapterContext = AdapterContext(sourceId,userId,sourceContext, userContext)
        new LearningQueryHandler().pullAll(adapterContext).onComplete {
          case Success(itemDataWithIdList) => itemDataWithIdList.foreach(item =>
            {
              println(item.id)
              println(item.data)
            }
          )
          case Failure(exception) => println(exception.printStackTrace())
        }

      case Failure(exception) => println(exception)
      }
    }

}
