package net.atos.ncf.itemprovider.adapter.lib.ifaces

import net.atos.ncf.common.objs.NCFBusinessContext

trait IBusinessContextSerializer[TBusinessContext] {
  /**
   * Converts [internal representation of a business context] -> [NCF wide representation of business context]
   */
  def serializeBusinessContext(bc: TBusinessContext): NCFBusinessContext
}
