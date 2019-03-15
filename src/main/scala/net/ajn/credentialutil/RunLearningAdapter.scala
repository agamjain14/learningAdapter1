import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import net.ajn.credentialutil.client.learning.impl.GenerateToken

object RunLearningAdapter {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher
    val objectInstance =  GenerateToken.init()
    objectInstance.getInstance()
  }
}
