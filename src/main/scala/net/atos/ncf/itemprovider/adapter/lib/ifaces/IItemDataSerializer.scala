package net.atos.ncf.itemprovider.adapter.lib.ifaces

import net.atos.ncf.common.objs.NCFItemData
import net.atos.ncf.itemprovider.adapter.lib.objs.ItemDataWithId
import net.atos.ncf.itemprovider.adapter.lib.objs.ItemDataWithId
import net.atos.ncf.itemprovider.adapter.lib.objs.AdapterContext

trait IItemDataSerializer[TSourceContext, TUserContext, TItemId, TItemData] {
  /**
   * @param id   internal representation of item id
   * @param data internal representation of item data
   * @return NCF wide representation of business context
   */
  def serializeItem(context: AdapterContext[TSourceContext, TUserContext], id: TItemId, data: TItemData): NCFItemData

  /**
   * @param dataWithId internal representation of item data with id
   * @return NCF wide representation of business context
   */
  private[lib] def serializeItem(context: AdapterContext[TSourceContext, TUserContext], dataWithId: ItemDataWithId[TItemId, TItemData]): NCFItemData = {
    this.serializeItem(context, dataWithId.id, dataWithId.data)
  }
}
