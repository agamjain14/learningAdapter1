package net.ajn.credentialutil.client.learningconfig

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import io.circe.generic.auto._
import io.circe.syntax._
import net.ajn.credentialutil.svc.models.{ClientCredentialsRequest, SAMLBearerAssertionRequest}


/*
case class LearningRequest(userId: String, endPoint: String, clientId: String, clientSecret: String, userType: String, resourceType: String, companyId: String) extends ClientCredentialsRequest with SAMLBearerAssertionRequest {

  override def buildEntity(): HttpEntity = {
    val payload = LearningPayload(grant_type = grantType,scope = ScopeConfig(userId = userId, companyId = companyId, userType = userType, resourceType = resourceType))
    val jsonString: String = payload.asJson.noSpaces
    HttpEntity(ContentTypes.`application/json`, jsonString)
  }
}
*/
