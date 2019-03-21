package net.atos.ncf.itemprovider.adapter.lib.ifaces

import net.atos.ncf.itemprovider.adapter.lib.objs.AdapterContext
import scala.concurrent.Future
import net.atos.ncf.itemprovider.adapter.lib.objs.ItemDataWithId
import scala.concurrent.ExecutionContext
import net.atos.ncf.common.objs.NCFActionDefinition
import java.io.InputStream

trait IQueryHandler[TItemId, TItemData, TBusinessContext, TSourceContext, TUserContext] {

  /**
   * Pulls all elements under from a backend system.
   *
   * @param context Instance of [[AdapterContext]].
   */
  def pullAll(context: AdapterContext[TSourceContext, TUserContext])(implicit ec: ExecutionContext): Future[Seq[ItemDataWithId[TItemId, TItemData]]]

  /**
   * Pulls many elements by their ids from a backend system.
   *
   * @param context Instance of [[AdapterContext]].
   * @param itemIds ids of the items to pull.
   */
  def pullMany(context: AdapterContext[TSourceContext, TUserContext], itemIds: TItemId*)(implicit ec: ExecutionContext): Future[Seq[ItemDataWithId[TItemId, TItemData]]]

  /**
   * Pulls many elements business context by their ids from a backend system.
   *
   * @param context Instance of [[AdapterContext]].
   * @param itemIds ids of the items.
   */
  def getBusinessContexts(context: AdapterContext[TSourceContext, TUserContext], itemIds: TItemId*)(implicit ec: ExecutionContext): Future[Seq[Option[TBusinessContext]]]

  /**
   * Pulls many elements user decisions by their ids from a backend system.
   *
   * @param context Instance of [[AdapterContext]].
   * @param itemIds ids of the items.
   */
  def getSupportedUserDecisions(context: AdapterContext[TSourceContext, TUserContext], itemIds: TItemId*)(implicit ec: ExecutionContext): Future[Seq[Seq[NCFActionDefinition]]]

  /**
   * @param itemId       id of the item.
   * @param attachmentId id of the attachment.
   * @return A future containing the attachment input stream
   */
  def getAttachmentStreamAndHeaders(context: AdapterContext[TSourceContext, TUserContext], itemId: TItemId, attachmentId: String)(implicit ec: ExecutionContext): Future[(Map[String, String], InputStream)]
}
