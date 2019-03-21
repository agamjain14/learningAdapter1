package net.atos.ncf.common.objs

import org.apache.commons.lang3.builder.HashCodeBuilder

case class NCFExternalAttachment(
    uniqueId: String,
    fileName: String,
    fileDisplayName: Option[String],
    fileSize: Int,
    mimeType: String,
    isBusinessContextAttachment: Boolean,
    createdAt: DateTime,
    createdBy: Option[NCFExternalUserInfo]
) {

  override def hashCode() = {
    //DO NOT include these properties in calculation of the hashCode: fileName, createdAt
    val builder = new HashCodeBuilder()
      .append(uniqueId)
      .append(fileDisplayName)
      .append(fileSize)
      .append(mimeType)
      .append(isBusinessContextAttachment)
      .append(createdBy)
    builder.toHashCode

  }
}
