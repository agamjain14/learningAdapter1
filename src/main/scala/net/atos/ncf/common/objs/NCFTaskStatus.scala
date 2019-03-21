package net.atos.ncf.common.objs

object NCFTaskStatus extends Enumeration {
  type NCFTaskStatus = Value
  val Cancelled, Checked, Executed, Completed, Error, Excpcaught, Excphandler, Assigned, Reserved, InProgress, ForResubmission = Value
}
