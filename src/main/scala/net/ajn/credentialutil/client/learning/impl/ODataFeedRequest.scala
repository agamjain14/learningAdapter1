package net.ajn.credentialutil.client.learning.impl

// import com.typesafe.config.Config
import net.ajn.credentialutil.client.ncf.TSourceContext
import net.ajn.credentialutil.svc.models.TokenRequest
// import net.ceedubs.ficus.Ficus._
//import net.ceedubs.ficus.readers.ValueReader
//
trait Request {
  def endpoint: String
  def collection: String
  def userId: Option[String]
  def format: Option[String]
  def tokenRequestGenerator: String => TokenRequest
}

trait ODataFeedRequest  extends Request {

  def filter: Option[String]
  def select: Option[List[String]]
  def expand: Option[List[String]]

}

trait EntryRequest extends Request {
  def entryId: String
}


case class LearningFeedRequest(endpoint: String, collection: String, tokenRequestGenerator: String => TokenRequest ,userId: Option[String]  , filter: Option[String], select: Option[List[String]], expand: Option[List[String]]) extends ODataFeedRequest {
  final val format = Some("application/json")
  def setUser(usrId: String) = this.copy(userId = Some(usrId))

  def setFilter(fltr: String) = this.copy(filter = Some(fltr))

  def setExpand(xpd: List[String]) = this.copy(expand = Some(xpd))

  def setSelect(slct: Option[List[String]]) = this.copy(select = slct)

  def setCollection(coll : String) = this.copy(collection = coll)



}


object LearningFeedRequest {

  /*implicit object FeedRequestValueReader extends ValueReader[LearningFeedRequest] {
    override def read(config: Config, path: String): LearningFeedRequest = {
      val _config = config.getConfig(path)
      LearningFeedRequest(
        userId = None,
        endpoint = _config.as[String]("endpoint"),
        tokenRequestGenerator = LearningTokenProvider.requestGenerator,
        collection = _config.as[String]("collection"),
        filter = _config.as[Option[String]]("filter"),
        select = _config.as[Option[String]]("select").map(s => s.split(",").toList.map(t => t.trim())),
        expand = _config.as[Option[String]]("expand").map(s => s.split(",").toList.map(t => t.trim()))
      )
    }
  }


  def buildFromConfig(config: Config, source: String): LearningFeedRequest = {
    val p = s"adapter.learning.${source}"
    config.as[LearningFeedRequest](p)

  }*/

  private def buildFromTSourceContext(source: TSourceContext) : LearningFeedRequest = {
    LearningFeedRequest(
      userId = None,
      endpoint = source.endPoint,
      tokenRequestGenerator = source.tokenRequestGenerator,
      collection = source.collection,
      filter = source.filter,
      select = source.select.map(s => s.split(",").toList.map(t => t.trim())),
      expand = source.expand.map(s => s.split(",").toList.map(t => t.trim()))
    )
  }




  def buildFromTSourceContextWithUser(sourceContext: TSourceContext, userId: String) ={
    buildFromTSourceContext(sourceContext).setUser(userId)
  }


}