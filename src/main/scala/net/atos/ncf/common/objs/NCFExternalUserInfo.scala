package net.atos.ncf.common.objs

object NCFExternalUserInfo {

  sealed trait HaveExternalId {
    self: NCFExternalUserInfo =>

    def userId: String
  }

  sealed trait HaveDisplayName {
    self: NCFExternalUserInfo =>

    def displayName: String
  }

  case class OnlyId(userId: String) extends NCFExternalUserInfo with HaveExternalId {
    override def toString = s"NCFExternalUserInfo(user-id = '$userId'})"
  }

  case class IdAndName(userId: String, displayName: String) extends NCFExternalUserInfo with HaveExternalId with HaveDisplayName {
    override def toString = s"NCFExternalUserInfo(user-id = '$userId', display-name = '$displayName')"
  }

}

sealed trait NCFExternalUserInfo
