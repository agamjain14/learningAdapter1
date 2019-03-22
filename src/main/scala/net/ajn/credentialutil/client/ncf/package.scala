package net.ajn.credentialutil.client

import net.ajn.credentialutil.client.learning.impl.LearningAdapterClientProxy
import net.ajn.credentialutil.svc.models.TokenRequest
import net.atos.ncf.common.objs.NCFBusinessContext
import org.joda.time.DateTime

import net.ajn.credentialutil.client.ncf.filter.Syntax._

package object ncf {

  sealed trait LearningItem {
    def key: LearningItemKey
  }

  sealed trait LearningItemKey {
    def collection: String
  }


  type TItemData = LearningItem

  type TItemId = LearningItemKey

  type TBusinessContext = NCFBusinessContext

  case class Todo(key: TodoKey,sku: String, title: String, description: String, userId: String, goToLink: String, courseLink: String, createdDate: Option[DateTime], creditHours: Option[Double], daysUntilDue: Option[Int], assignedBy: Option[String]) extends LearningItem

  case class Approval(key: ApprovalKey, title: String, description: String, requestor: String) extends LearningItem

  case class TodoKey(componentId : String, componentTypeId: String, revisionDate: Long) extends LearningItemKey {
    val collection: String = LearningContextManager.KnownCollections.todos
    def buildFilter() = {
      ("lisCriteria/itemID " === componentId and "lisCriteria/itemTypeID" === componentTypeId and "lisCriteria/revisionDate" === revisionDate and "lisCriteria/includeDeeplink" === true).toString
    }
  }

  case class ApprovalKey(key: Long) extends LearningItemKey {
    val collection: String = LearningContextManager.KnownCollections.approvals
  }


  object ApprovalKey {
    val key = "key"
  }

  object Todo {
    val sku = "sku"
    val title = "title"
    val description = "description"
    val userId = "userID"
    val creditHours = "creditHours"
    val goToLink = "itemDetailsDeeplink"
    val courseLink = "courseDeeplink"
    val daysUntilDue = "daysRemaining"
    val assignedBy = "addUserName"
    val createdDate = "assignedDate"
    val componentID = "componentID"
    val componentTypeID = "componentTypeID"
    val revisionDate = "revisionDate"
    val detailsSku = "catalogSKU"
  }


  object TodoKey {
    val componentID = "componentID"
    val componentTypeID = "componentTypeID"
    val revisionDate = "revisionDate"
  }


  object Approval {
    val key = "tap_instance_id"
    val title = "front_header"
    val description = "itemName"
    val requestor = "requestorFullName"
  }


  case class TSourceContext(endPoint: String, sso: SSOConfig, collection: String, tokenRequestGenerator: String => TokenRequest, filter: Option[String], format: Option[String], select: Option[String], expand: Option[String])


  case class TUserContext(userId: String, client: LearningAdapterClientProxy)

  case class SSOConfig(endPoint: String, clientId: String, clientSecret: String, tenantId: String, userType: String, resourceType: String)



}
