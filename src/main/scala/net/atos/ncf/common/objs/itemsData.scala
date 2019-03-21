package net.atos.ncf.common.objs

import org.apache.commons.lang3.builder.HashCodeBuilder

object NCFItemData {
  final val HashCodeListSeparator = 1
}

sealed trait NCFItemData {
  def externalId: NCFItemExternalId

  def externalOwner: NCFExternalUserInfo

  def title: String

  def description: NCFItemDescription

  def createdAt: DateTime

  def createdBy: Option[NCFExternalUserInfo]

  def attachements: Seq[NCFExternalAttachment]

  def links: Seq[NCFExternalLink]

  def isTask: Boolean

  def computeHashCode(): Int = {
    val builder = new HashCodeBuilder()
      .append(externalId)
      .append(externalOwner)
      .append(title)
      .append(description)
      .append(createdAt.getMillis)
      .append(createdBy)
    builder.append(NCFItemData.HashCodeListSeparator)
    attachements.foreach(builder.append)
    builder.append(NCFItemData.HashCodeListSeparator)
    links.foreach(builder.append)
    builder.toHashCode
  }
}

sealed trait NCFTaskData extends NCFItemData {
  def startDeadLine: Option[DateTime]

  def completionDeadLine: Option[DateTime]

  def expiryDate: Option[DateTime]

  def status: NCFTaskStatus.Value

  def priority: NCFTaskPriority

  def processor: Option[NCFExternalUserInfo]

  def substitutedUser: Option[NCFExternalUserInfo]

  def potentialOwners: Seq[NCFExternalUserInfo]

  override def computeHashCode(): Int = {
    val builder = new HashCodeBuilder()
      .appendSuper(super.computeHashCode())
      .append(startDeadLine.map(_.getMillis))
      .append(completionDeadLine.map(_.getMillis))
      .append(expiryDate.map(_.getMillis))
      .append(status)
      .append(priority)
      .append(processor)
      .append(substitutedUser)
    builder.append(NCFItemData.HashCodeListSeparator)
    potentialOwners.foreach(builder.append)
    builder.toHashCode
  }

  override final val isTask = true

  def withStatus(v: NCFTaskStatus.Value): NCFTaskData
}

case class NCFNotificationData(
    externalId: NCFItemExternalId,
    externalOwner: NCFExternalUserInfo,
    title: String,
    description: NCFItemDescription,
    createdAt: DateTime,
    createdBy: Option[NCFExternalUserInfo],
    attachements: Seq[NCFExternalAttachment],
    notAfter: Option[DateTime],
    links: Seq[NCFExternalLink],
    reason: Option[String]
) extends NCFItemData {

  override final val isTask = false
}

case class NCFActionnableTask(
    externalId: NCFItemExternalId,
    externalOwner: NCFExternalUserInfo,
    title: String,
    description: NCFItemDescription,
    createdAt: DateTime,
    createdBy: Option[NCFExternalUserInfo],

    startDeadLine: Option[DateTime],
    completionDeadLine: Option[DateTime],
    expiryDate: Option[DateTime],
    status: NCFTaskStatus.Value,
    priority: NCFTaskPriority,

    processor: Option[NCFExternalUserInfo],
    substitutedUser: Option[NCFExternalUserInfo],
    potentialOwners: Seq[NCFExternalUserInfo],

    attachements: Seq[NCFExternalAttachment],
    links: Seq[NCFExternalLink],

    actionCapabilities: NCFActionnableTaskCapabilities
) extends NCFTaskData {
  override def computeHashCode(): Int = {
    val builder = new HashCodeBuilder()
      .appendSuper(super.computeHashCode())
      .append(actionCapabilities)
    builder.toHashCode
  }

  override def withStatus(v: NCFTaskStatus.Value) = this.copy(status = v)
}

case class NCFSummaryTask(
    externalId: NCFItemExternalId,
    externalOwner: NCFExternalUserInfo,
    title: String,
    description: NCFItemDescription,
    createdAt: DateTime,
    createdBy: Option[NCFExternalUserInfo],

    startDeadLine: Option[DateTime],
    completionDeadLine: Option[DateTime],
    expiryDate: Option[DateTime],
    backendStatus: NCFTaskStatus.Value,
    priority: NCFTaskPriority,

    processor: Option[NCFExternalUserInfo],
    substitutedUser: Option[NCFExternalUserInfo],
    potentialOwners: Seq[NCFExternalUserInfo],

    attachements: Seq[NCFExternalAttachment],
    links: Seq[NCFExternalLink],

    count: Double,
    uom: Option[NCFSummaryUnitOfMesure],
    aggregationLevel: Option[NCFSummaryAggregationLevel]
) extends NCFTaskData {
  override def computeHashCode(): Int = {
    val builder = new HashCodeBuilder()
      .appendSuper(super.computeHashCode())
      .append(backendStatus)
      .append(count)
      .append(uom)
      .append(aggregationLevel)
    builder.toHashCode
  }

  override def status: NCFTaskStatus.Value = backendStatus

  override def withStatus(v: NCFTaskStatus.Value) = this.copy(backendStatus = v)
}
