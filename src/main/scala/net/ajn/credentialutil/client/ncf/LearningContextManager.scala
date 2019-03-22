package net.ajn.credentialutil.client.ncf

import com.typesafe.config.Config
import net.ajn.credentialutil.client.learning.impl.LearningAdapterClientProxy
import net.ajn.credentialutil.svc.ifaces.{AuthTypes, ContentTypes}
import net.ajn.credentialutil.svc.models
import net.ajn.credentialutil.svc.models.{ClientCredentialsRequest, TokenRequest}
import net.atos.ncf.itemprovider.adapter.lib.ifaces.IContextManager

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

class LearningContextManager(config: Config, clientProxy: LearningAdapterClientProxy) extends IContextManager[TSourceContext, TUserContext] {
  /**
    * Builds a source context from source parameters.
    *
    * @param sourceId Id of the source.
    * @param params   Key-value map of source parameters.
    * @return A SourceContext object.
    */
  override def buildSourceContext(sourceId: String, params: Map[String, String]): TSourceContext = {
    (for {
      _sso <- buildSSOConfig(sourceId, params)
      _endpoint <- Try(params(LearningContextManager.businessEndpoint))
      _collection <- Try(params(LearningContextManager.collection))
    } yield TSourceContext(
      endPoint = _endpoint,
      sso = _sso,
      tokenRequestGenerator = buildTokenRequestGeneratorFromConfig(_sso),
      collection = _collection,
      filter = params.get(LearningContextManager.filter),
      format = params.get(LearningContextManager.format),
      select = params.get(LearningContextManager.select),
      expand = params.get(LearningContextManager.expand))) match {
      case Success(sourceContext: TSourceContext) => sourceContext
      case Failure(exception) => throw new NoSuchElementException(s"Exception while trying to read parameters for ${sourceId}: ${exception.getMessage}")
    }
  }


  def buildTokenRequestGeneratorFromConfig(sso: SSOConfig): String => TokenRequest = (usr: String) => {
    new ClientCredentialsRequest() {
      val userId: String = usr

      val tokenEndpoint: String = sso.endPoint

      val clientId: String = sso.clientId

      val contentType: ContentTypes.ContentType = ContentTypes.Json

      val clientSecret: String = sso.clientSecret

      val stringifyRequestBody: String =
        s"""
           |{
           |"grant_type" : "${grantType.toString()}",
           |"scope" : {
           |"userId" : "$userId",
           |"companyId" : "${sso.tenantId}",
           |"userType" : "${sso.userType}",
           |"resourceType" : "${sso.resourceType}"
           |}
           |}
         """.stripMargin

      val getProxy: Option[models.Proxy] = None

      override val authType: AuthTypes.AuthType = AuthTypes.BasicAuth
    }
  }

  private def buildSSOConfig(sourceId: String, params: Map[String, String]): Try[SSOConfig] = {
    for {
      _endpoint <- Try(params(LearningContextManager.SSOConfig.tokenEndpoint))
      _clientId <- Try(params(LearningContextManager.SSOConfig.clientId))
      _clientSecret <- Try(params(LearningContextManager.SSOConfig.clientSecret))
      _tenantId <- Try(params(LearningContextManager.SSOConfig.tenantId))
      _resourceType <- Try(params(LearningContextManager.SSOConfig.resourceType))
      _userType <- Try(params(LearningContextManager.SSOConfig.userType))
    } yield SSOConfig(endPoint = _endpoint,
      clientId = _clientId,
      clientSecret = _clientSecret,
      tenantId = _tenantId,
      resourceType = _resourceType,
      userType = _userType)
  }


  /**
    * Builds a new user context.
    *
    * @param sourceContext The source context.
    * @param userId        User id.
    * @param ec            Execution context.
    * @return A [[Future]] value of the user context.
    */
  override def buildUserContext(sourceId: String, userId: String, sourceContext: TSourceContext)(implicit ec: ExecutionContext): Future[TUserContext] = {
    Future.successful(TUserContext(userId, client = clientProxy))
  }
}


object LearningContextManager {

  object SSOConfig {
    val tokenEndpoint = "token_endpoint"
    val clientId = "client_id"
    val clientSecret = "client_secret"
    val tenantId = "tenant_id"
    val resourceType = "resource_type"
    val userType = "user_type"
  }

  val businessEndpoint = "business_endpoint"
  val collection = "collection"
  val format = "format"
  val filter = "filter"
  val select = "select"
  val expand = "expand"
  val isActionable = "is_actionable"
  val needsComment = "needs_comment"
  val learningEndpoint = "approval_base_uri"



  object KnownCollections {
    val todos = "UserTodoLearningItems"
    val approvals = "learningapprovals"
    val todoDetails = "LearningItemDetails"

  }


  val sampleParamsMap = Map(
    businessEndpoint -> "https://atosstaging.plateau.com/learning/odatav4/public/user/learningPlan/v1",
    collection -> KnownCollections.todos,
    format -> "application/json",
    filter -> "criteria/maxRowNum eq  50 and criteria/includeVLSlink eq true and criteria/includeDeeplink eq true",
    select -> "sku,cpnt_classification,title,description,userID,componentTypeDesc,componentID,componentTypeID,revisionDate,creditHours,daysRemaining,addUserName,origin,itemDetailsDeeplink,courseDeeplink,onlineLaunched,requirementTypeDescription,assignedDate",
    isActionable -> "false",
    needsComment -> "true",
    SSOConfig.clientId -> "atosinter",
    SSOConfig.clientSecret -> "d1ec824e7519f7b3641f5baa1d9d17a50dd89d6adf9361028926fa9491a2390f40a7103f0bb20c506905cd9143c6a92d",
    SSOConfig.tokenEndpoint -> "https://atosstaging.plateau.com/learning/oauth-api/rest/v1/token",
    SSOConfig.userType -> "user",
    SSOConfig.resourceType -> "learning_public_api",
    SSOConfig.tenantId -> "atosinter"
  )


  val sampleParamsMapForApprovals = Map(
    businessEndpoint -> "https://atosstaging.plateau.com/learning/odatav4/public/user/user-service/v1",
    collection -> KnownCollections.approvals,
    isActionable -> "true",
    format -> "application/json",
    needsComment -> "true",
    SSOConfig.clientId -> "atosinter",
    SSOConfig.clientSecret -> "d1ec824e7519f7b3641f5baa1d9d17a50dd89d6adf9361028926fa9491a2390f40a7103f0bb20c506905cd9143c6a92d",
    SSOConfig.tokenEndpoint -> "https://atosstaging.plateau.com/learning/oauth-api/rest/v1/token",
    SSOConfig.userType -> "user",
    SSOConfig.resourceType -> "learning_public_api",
    SSOConfig.tenantId -> "atosinter"
  )




}