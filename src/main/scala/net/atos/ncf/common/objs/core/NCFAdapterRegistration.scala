package net.atos.ncf.common.objs.core

import net.atos.ncf.common.objs.NCFParameterDefinition


object NCFAdapterRegistration {

  case class EmbeddedResource(resourceName: String, mimeType: String)

  case class ResourceBundle(name: String, templateVersion: Int, resourceBundleVersion: Int, resources: Seq[EmbeddedResource], templateMimeType: String, resourceBundleMimeType: String)

}

case class NCFAdapterRegistration(
    id: String,
    adapterName: String,
    adapterVersion: Int,
    parameters: Seq[NCFParameterDefinition],
    resourceBundles: Seq[NCFAdapterRegistration.ResourceBundle]
)
