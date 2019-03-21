package net.atos.ncf.common.objs

/**
 * Represents a parameter definition.
 */
case class NCFParameterDefinition(name: String, displayName: String, inputType: String, mandatory: Boolean) {
  override def toString = s"$name: $inputType => $displayName (required = $mandatory)"
}
