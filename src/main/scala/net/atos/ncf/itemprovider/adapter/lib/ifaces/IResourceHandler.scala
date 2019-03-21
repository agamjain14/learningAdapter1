package net.atos.ncf.itemprovider.adapter.lib.ifaces

import java.io.InputStream

import net.atos.ncf.common.objs.core.NCFAdapterRegistration

import scala.concurrent.{ExecutionContext, Future}

trait IResourceHandler[TSourceContext, TUserContext] {
  def getResource(context: TSourceContext, resourceBundleName: String, resourceName: String)(implicit ec: ExecutionContext): Future[InputStream]

  def getResourceBundle(context: TSourceContext, resourceBundleName: String, resourceBundle: NCFAdapterRegistration.ResourceBundle)(implicit ec: ExecutionContext): Future[InputStream]

  def getTemplate(context: TSourceContext, resourceBundleName: String)(implicit ec: ExecutionContext): Future[InputStream]
}
