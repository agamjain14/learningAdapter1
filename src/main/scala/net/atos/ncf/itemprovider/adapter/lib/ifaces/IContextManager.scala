package net.atos.ncf.itemprovider.adapter.lib.ifaces

import scala.concurrent.Future
import scala.concurrent.ExecutionContext

/**
 * Service that should implement the logic for creating checking source/user contexts.
 *
 * @tparam TSourceContext Type of a source context.
 * @tparam TUserContext   Type of a user context.
 */
trait IContextManager[TSourceContext, TUserContext] {
  /**
   * Builds a source context from source parameters.
   *
   * @param sourceId Id of the source.
   * @param params   Key-value map of source parameters.
   * @return A SourceContext object.
   */
  def buildSourceContext(sourceId: String, params: Map[String, String]): TSourceContext

  /**
   * Builds a new user context.
   *
   * @param sourceContext The source context.
   * @param userId        User id.
   * @param ec            Execution context.
   * @return A [[Future]] value of the user context.
   */
  def buildUserContext(sourceId: String, userId: String, sourceContext: TSourceContext)(implicit ec: ExecutionContext): Future[TUserContext]
}
