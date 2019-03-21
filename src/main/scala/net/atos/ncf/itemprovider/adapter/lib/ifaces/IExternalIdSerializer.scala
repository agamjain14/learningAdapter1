package net.atos.ncf.itemprovider.adapter.lib.ifaces

import net.atos.ncf.common.objs.NCFItemExternalId

trait IExternalIdSerializer[TItemId] {
  /**
   * Converts [internal representation of an item id] -> [NCF wide representation of an item id]
   */
  def serializeId(id: TItemId): NCFItemExternalId

  /**
   * Converts [NCF wide representation of an item id] -> [internal representation of an item id]
   */
  def deserializeId(id: NCFItemExternalId): TItemId
}
