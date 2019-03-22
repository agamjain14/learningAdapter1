package net.ajn.credentialutil.client.ncf

import net.atos.ncf.common.objs.NCFLinkType.NCFLinkType
import net.atos.ncf.common.objs._
import net.atos.ncf.itemprovider.adapter.lib.ifaces.{IBusinessContextSerializer, IExternalIdSerializer, IItemDataSerializer}
import net.atos.ncf.itemprovider.adapter.lib.objs.AdapterContext
import org.joda.time.DateTime

class LearningItemDataSerializer extends IItemDataSerializer[TSourceContext, TUserContext, TItemId, TItemData]
  with IExternalIdSerializer[TItemId]
  with IBusinessContextSerializer[TBusinessContext] {
  /**
    * @param id   internal representation of item id
    * @param data internal representation of item data
    * @return NCF wide representation of business context
    */
  //NCFActionnableTask(
  //    externalId: NCFItemExternalId,
  //    externalOwner: NCFExternalUserInfo,
  //    title: String,
  //    description: NCFItemDescription,
  //    createdAt: DateTime,
  //    createdBy: Option[NCFExternalUserInfo],
  //
  //    startDeadLine: Option[DateTime],
  //    completionDeadLine: Option[DateTime],
  //    expiryDate: Option[DateTime],
  //    status: NCFTaskStatus.Value,
  //    priority: NCFTaskPriority,
  //
  //    processor: Option[NCFExternalUserInfo],
  //    substitutedUser: Option[NCFExternalUserInfo],
  //    potentialOwners: Seq[NCFExternalUserInfo],
  //
  //    attachements: Seq[NCFExternalAttachment],
  //    links: Seq[NCFExternalLink],
  //
  //    actionCapabilities: NCFActionnableTaskCapabilities
  override def serializeItem(context: AdapterContext[TSourceContext, TUserContext], id: TItemId, data: TItemData): NCFItemData = data match {
    case todo: Todo =>
      val d = data.asInstanceOf[Todo]
      NCFActionnableTask(
        externalId = serializeId(id),
        externalOwner = NCFExternalUserInfo.OnlyId(d.userId),
        title = d.title,
        description = NCFItemDescription.PlainText(d.description),
        createdAt = d.createdDate.getOrElse(DateTime.now),
        createdBy = d.assignedBy.flatMap(p => parseCreatedByForTodo(p)),
        startDeadLine = None,
        completionDeadLine = d.daysUntilDue.flatMap(day => calculateCompletionDeadLineForTodo(day)),
        expiryDate = None,
        status = NCFTaskStatus.Assigned,
        priority = calculatePriorityForTodo(d.daysUntilDue),
        processor = Some(NCFExternalUserInfo.OnlyId(d.userId)),
        substitutedUser = None,
        potentialOwners = Nil,
        attachements = Nil,
        links = List(NCFExternalLink(d.goToLink,Some("gui-link"), NCFLinkType.Desktop, None, None)),
        actionCapabilities = NCFActionnableTaskCapabilities(supportsClaim = false,supportsRelease = false,supportsForward = false,supportsUserDecisions = Nil)
      )
  }



  def calculatePriorityForTodo(daysRemaining: Option[Int]) : NCFTaskPriority = daysRemaining match {
    case Some(days) =>
      if (days > 90) NCFTaskPriority.Low(0)
      else if (days > 60) NCFTaskPriority.Medium(1)
      else if (days > 30) NCFTaskPriority.High(2)
      else NCFTaskPriority.VeryHigh(3)
    case None => NCFTaskPriority.Low(0)
  }

  def calculateCompletionDeadLineForTodo(daysRemaining: Int): Option[DateTime] = {
    Some(DateTime.now.plusDays(daysRemaining))
  }

  def parseCreatedByForTodo(assignedBy: String):Option[NCFExternalUserInfo] = {
    val l = assignedBy.split(",")
    if (l.size == 3 && l(1).trim.size > 0 && l(2).trim.size > 0) {
      Some(NCFExternalUserInfo.IdAndName(l(0), s"${l(1)} ${l(2).trim}"))
    } else {
      Some(NCFExternalUserInfo.OnlyId(l(0)))
    }
  }


  /**
    * Converts [internal representation of an item id] -> [NCF wide representation of an item id]
    */
  override def serializeId(id: TItemId): NCFItemExternalId = id match {
    case k @ TodoKey(compId,compTId, revDate) => NCFItemExternalId(Map(
      TodoKey.componentID -> compId,
      TodoKey.componentTypeID -> compTId,
      TodoKey.revisionDate -> revDate.toString,
      LearningAdapterConstants.keyDiscriminator -> k.collection
    ))

    case k @ ApprovalKey(key) => NCFItemExternalId(Map(
      ApprovalKey.key -> key.toString,
      LearningAdapterConstants.keyDiscriminator -> k.collection
    ))
  }
  /**
    * Converts [NCF wide representation of an item id] -> [internal representation of an item id]
    */
  override def deserializeId(id: NCFItemExternalId): TItemId = id.fields(LearningAdapterConstants.keyDiscriminator) match {
    case LearningContextManager.KnownCollections.todos =>
      val key = for {
        componentId <- id.fields.get(TodoKey.componentID)
        componentTypeId <- id.fields.get(TodoKey.componentTypeID)
        revisionDate <- id.fields.get(TodoKey.revisionDate)
      } yield TodoKey(componentId, componentTypeId, revisionDate.toLong)
      key.get
    case LearningContextManager.KnownCollections.approvals =>
      val key = for {
        key <- id.fields.get(ApprovalKey.key)
      } yield ApprovalKey(key.toLong)
      key.get
  }

  /**
    * Converts [internal representation of a business context] -> [NCF wide representation of business context]
    */
  override def serializeBusinessContext(bc: TBusinessContext): TBusinessContext = bc
}