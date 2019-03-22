import java.util.concurrent.{Executors, ThreadFactory}

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import net.ajn.credentialutil.client.learning.impl.LearningAdapterClientProxy
import net.ajn.credentialutil.client.ncf.{LearningContextManager, LearningItemDataSerializer, LearningQueryHandler, TodoKey}
import net.ajn.credentialutil.svc.impl.AkkaCredentialService
import net.atos.ncf.common.objs.NCFItemExternalId
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

    val serializer = new LearningItemDataSerializer

    learningContextManager.buildUserContext(sourceId,userId,sourceContext).onComplete {
      case Success(userContext) =>
        val adapterContext = AdapterContext(sourceId,userId,sourceContext, userContext)
        val learningQueryHandler = new LearningQueryHandler()
        learningQueryHandler.pullAll(adapterContext).onComplete {
          case Success(itemDataWithIdList) => itemDataWithIdList.foreach(item =>
            {
              val ncfItem = serializer.serializeItem(adapterContext, item.id, item.data)
              println(ncfItem)



            }
          )
          case Failure(exception) => println(exception.printStackTrace())
        }

        val key1 = TodoKey(componentTypeId = "UNSCHE", componentId = "A9S0017713", revisionDate = 1536909180000L)
        val key2 = TodoKey(componentTypeId = "UNSCHE", componentId = "A9S0017349", revisionDate = 1532331240000L)




        learningQueryHandler.pullMany(context = adapterContext,key1, key2).onComplete {
          case Success(learningItems) => learningItems.foreach(learningItem => {
            val ncfItem = serializer.serializeItem(adapterContext, learningItem.id, learningItem.data)
            println(ncfItem)
          })
          case Failure(exception) => println(exception.printStackTrace())
        }


      case Failure(exception) => println(exception)
      }



   /* sts.close().onComplete {

      case Success(value) =>
        println("Press Enter to Finish")
        scala.io.StdIn.readLine()
        materializer.shutdown()
        system.terminate()
    }*/



    }
}
