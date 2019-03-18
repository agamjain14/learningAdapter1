package net.ajn.credentialutil.client.utils

import java.util.concurrent.{CompletableFuture, Future, ScheduledExecutorService, TimeUnit}

import com.typesafe.scalalogging.Logger

import scala.concurrent.ExecutionException
import com.typesafe.scalalogging.StrictLogging







object JFuturePollingScheduler {
  def schedule(block: => AnyVal, logger: Logger)(implicit EC: ScheduledExecutorService):Unit = {
    //logger.debug("polling for Java Future completion")
    EC.schedule(new Runnable() { def run() = {
      //logger.debug("starting the execution of the future check ....")
      block
    }
    }, 1, TimeUnit.MILLISECONDS)
  }
}

object JFutureConverter {
    def apply[T](f: Future[T])(implicit EC: ScheduledExecutorService) = new JFutureConverter[T](f)

}

class JFutureConverter[A](val f:java.util.concurrent.Future[A])(implicit EC:ScheduledExecutorService) extends CompletableFuture[A] with StrictLogging {
  JFuturePollingScheduler.schedule(tryToComplete, logger)

  def tryToComplete(): Unit = {
    if (f.isDone) {
      try {
        logger.debug("Java Future isDone = true...")
        complete(f.get)
      }
      catch {
        case e: InterruptedException =>
          completeExceptionally(e)
        case e: ExecutionException =>
          completeExceptionally(e.getCause)
      }
      return
    }
    if (f.isCancelled) {
      cancel(true)
      return
    }
    JFuturePollingScheduler.schedule(tryToComplete(), logger)
  }
}

