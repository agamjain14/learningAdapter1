package net.atos.ncf.itemprovider.adapter.lib.objs

case class AdapterContext[TSourceContext, TUserContext](
    sourceId: String,
    userId: String,
    sourceContext: TSourceContext,
    userContext: TUserContext
)
