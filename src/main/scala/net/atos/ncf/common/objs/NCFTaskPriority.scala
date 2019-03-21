package net.atos.ncf.common.objs

object NCFTaskPriority {

  case class VeryHigh(priorityNumber: Int) extends NCFTaskPriority

  case class High(priorityNumber: Int) extends NCFTaskPriority

  case class Medium(priorityNumber: Int) extends NCFTaskPriority

  case class Low(priorityNumber: Int) extends NCFTaskPriority

  def zeros: Set[NCFTaskPriority] = Set(VeryHigh(0), High(0), Medium(0), Low(0))
}

sealed trait NCFTaskPriority {
  def priorityNumber: Int
}
