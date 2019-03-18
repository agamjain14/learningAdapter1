package net.ajn.credentialutil.client.learning.impl

import com.typesafe.config.Config
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ValueReader

trait Request {
  def endpoint: String
  def collection: String
  def userId: Option[String]
  def format: Option[String]
}

trait ODataFeedRequest  extends Request {

  def filter: Option[String]
  def select: Option[List[String]]
  def expand: Option[List[String]]
}

trait EntryRequest extends Request {
  def entryId: String
}


case class LearningFeedRequest(endpoint: String, collection: String, userId: Option[String]  , filter: Option[String], select: Option[List[String]], expand: Option[List[String]]) extends ODataFeedRequest {
  final val format = Some("application/json")
  def setUser(usrId: String) = this.copy(userId = Some(usrId))

  def setFilter(fltr: String) = this.copy(filter = Some(fltr))

  def setExpand(xpd: List[String]) = this.copy(expand = Some(xpd))

  def setSelect(slct: List[String]) = this.copy(select = Some(slct))
}


object LearningFeedRequest {

  implicit object FeedRequestValueReader extends ValueReader[LearningFeedRequest] {
    override def read(config: Config, path: String): LearningFeedRequest = {
      val _config = config.getConfig(path)
      LearningFeedRequest(
        userId = None,
        endpoint = _config.as[String]("endpoint"),
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

  }


}