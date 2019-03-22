package net.ajn.credentialutil.client.ncf

import java.io.InputStream

import net.ajn.credentialutil.client.learning.impl.LearningFeedRequest
import net.ajn.credentialutil.svc.models.TokenRequest
import net.atos.ncf.common.objs.NCFActionDefinition
import net.atos.ncf.itemprovider.adapter.lib.ifaces.IQueryHandler
import net.atos.ncf.itemprovider.adapter.lib.objs.{AdapterContext, ItemDataWithId}
import scala.collection.JavaConverters._

import scala.concurrent.{ExecutionContext, Future}

class LearningQueryHandler extends IQueryHandler[TItemId, TItemData, TBusinessContext, TSourceContext, TUserContext]{
  /**
    * Pulls all elements under from a backend system.
    *
    * @param context Instance of [[AdapterContext]].
    */
  override def pullAll(context: AdapterContext[TSourceContext, TUserContext])(implicit ec: ExecutionContext): Future[Seq[ItemDataWithId[TItemId, TItemData]]] = {
    val client = context.userContext.client
    val request: LearningFeedRequest = LearningFeedRequest.buildFromTSourceContextWithUser(sourceContext = context.sourceContext, userId = context.userId)
    for {
      feed <- client.readFeed(request)
    }yield Converters.fromClientEntitySetToLearningItemSet(feed,context.sourceContext).map(item => ItemDataWithId(item.key,item))
  }
  /**
    * Pulls many elements by their ids from a backend system.
    *
    * @param context Instance of [[AdapterContext]].
    * @param itemIds ids of the items to pull.
    */
  override def pullMany(context: AdapterContext[TSourceContext, TUserContext], itemIds: TItemId*)(implicit ec: ExecutionContext): Future[Seq[ItemDataWithId[TItemId, TItemData]]] = {
    val client = context.userContext.client
    context.sourceContext.collection match {
      case LearningContextManager.KnownCollections.todos =>
        Future.sequence(itemIds.map {itemid =>
          val request = LearningFeedRequest.buildFromTSourceContextWithUser(sourceContext = context.sourceContext, userId = context.userId)
            .setCollection(LearningContextManager.KnownCollections.todoDetails)
            .setSelect(None)
            .setFilter(itemid.asInstanceOf[TodoKey].buildFilter())
            client.readFeed(request).map(entitySet => Converters.fromClientEntityDetailsCollectionToLearningItem(entitySet.getEntities.asScala.toList.head, context.sourceContext, LearningContextManager.KnownCollections.todoDetails, context.userId)).map(learningItem => ItemDataWithId(learningItem.key, learningItem))
        })
    }
  }
  /**
    * Pulls many elements business context by their ids from a backend system.
    *
    * @param context Instance of [[AdapterContext]].
    * @param itemIds ids of the items.
    */
  override def getBusinessContexts(context: AdapterContext[TSourceContext, TUserContext], itemIds: TItemId*)(implicit ec: ExecutionContext): Future[Seq[Option[TBusinessContext]]] = ???

  /**
    * Pulls many elements user decisions by their ids from a backend system.
    *
    * @param context Instance of [[AdapterContext]].
    * @param itemIds ids of the items.
    */
  override def getSupportedUserDecisions(context: AdapterContext[TSourceContext, TUserContext], itemIds: TItemId*)(implicit ec: ExecutionContext): Future[Seq[Seq[NCFActionDefinition]]] = ???

  /**
    * @param itemId       id of the item.
    * @param attachmentId id of the attachment.
    * @return A future containing the attachment input stream
    */
  override def getAttachmentStreamAndHeaders(context: AdapterContext[TSourceContext, TUserContext], itemId: TItemId, attachmentId: String)(implicit ec: ExecutionContext): Future[(Map[String, String], InputStream)] = ???
}
