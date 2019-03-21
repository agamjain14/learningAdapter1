package net.atos.ncf.itemprovider.adapter.lib.ifaces

import net.atos.ncf.common.objs.NCFActionDefinition
import net.atos.ncf.common.objs.NCFParameterDefinition
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import net.atos.ncf.itemprovider.adapter.lib.objs.AdapterContext

trait IActionHandler[TItemId, TItemData, TSourceContext, TUserContext] {
  def claim(context: AdapterContext[TSourceContext, TUserContext], itemId: TItemId, comment: Option[String])(implicit ec: ExecutionContext): Future[Option[TItemData]]

  def release(context: AdapterContext[TSourceContext, TUserContext], itemId: TItemId)(implicit ec: ExecutionContext): Future[Option[TItemData]]

  def forward(context: AdapterContext[TSourceContext, TUserContext], itemId: TItemId, forwardTo: String, comment: Option[String])(implicit ec: ExecutionContext): Future[Option[TItemData]]

  def applyDecision(context: AdapterContext[TSourceContext, TUserContext], itemId: TItemId, actionKey: String, parameters: Map[String, String])(implicit ec: ExecutionContext): Future[Option[TItemData]]
}
