package net.atos.ncf.common.objs

case class NCFActionDefinition(name: String, displayName: String, actionKey: String, actionNature: NCFActionNature.Value = NCFActionNature.Neutral, parameters: Seq[NCFParameterDefinition]) {
  override def toString = s"NCFActionDefinition(name = '$name', display-name = '$displayName', key = '$actionKey', action-nature = '$actionNature', parameters = [${parameters.mkString(", ")}])"
}
