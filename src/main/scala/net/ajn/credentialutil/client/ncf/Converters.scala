package net.ajn.credentialutil.client.ncf

import java.util.Calendar

import com.typesafe.scalalogging.StrictLogging
import org.apache.olingo.client.api.domain.{ClientEntity, ClientEntitySet}
import org.joda.time.{DateTime, DateTimeZone, Instant}

import scala.reflect.ClassTag
import scala.collection.JavaConverters._


object Converters extends StrictLogging {


  implicit class PimpedClientEntity(entity:ClientEntity) {
    val props = entity.getProperties.asScala.toList.map(el => (el.getName,el)).toMap

    def getNullable[A : ClassTag](key:String): Option[A] = props.get(key).flatMap(x => Option(x)).map(_.getPrimitiveValue.toValue.asInstanceOf[A])
    def getString(key:String):Option[String] = getNullable[String](key)
    def getInt(key:String):Option[Int] = getNullable[Int](key)
    def getLong(key: String):Option[Long] = getNullable[Long](key)
    def getDecimal(key:String):Option[scala.math.BigDecimal] = getNullable[java.math.BigDecimal](key).map(d => scala.math.BigDecimal(d))
    def getDateTime(key:String):Option[java.time.OffsetDateTime] = getNullable[java.util.Calendar](key).map(cal => java.time.OffsetDateTime.ofInstant(cal.toInstant, java.time.ZoneId.of("GMT")))
    def getBoolean(key:String):Option[Boolean] = getNullable[Boolean](key)
    def getShort(key:String):Option[Int] = getNullable[java.lang.Short](key).map(s => Short.short2int(s))
    def getDouble(key:String):Option[Double] = getNullable[Double](key)
    def getFloat(key:String):Option[Float] = getNullable[Float](key)
    def getBigInt(key:String):Option[scala.math.BigInt] = getNullable[java.math.BigInteger](key).map(b => scala.math.BigInt(b))
    def getJodaTimeFromMillis(key:String):Option[org.joda.time.DateTime] = getNullable[Long](key).map(millis => Instant.ofEpochMilli(millis).toDateTime(DateTimeZone.UTC))
  }







  /*  implicit class Pimped(clientEntity: ClientEntity) {
      private val properties = clientEntity.getProperties.asScala.toList.map(p => (p.getName, p)).toMap

      def getString(key: String): Option[String] = getNullable[String](key)

      def getInt(key: String): Option[Int] = getNullable[Int](key)

      def getDouble(key: String): Option[Double] = getNullable[Double](key)

      def getDateTime(key: String): Option[DateTime] = getNullable[Calendar](key).map(x => new DateTime(x))

      // new
      def getLong(key: String): Option[Long] = getNullable[Long](key)

      // new
      def getDecimal(key: String): Option[scala.math.BigDecimal] = getNullable[java.math.BigDecimal](key).map(d => scala.math.BigDecimal(d))

      def getJodaDateTimeFromMillis(key: String): Option[org.joda.time.DateTime] = getNullable[Long](key).map(l => org.joda.time.Instant.ofEpochMilli(l).toDateTime)

      private def getNullable[A: ClassTag](key: String): Option[A] =
        properties.get(key).flatMap(p => Option(p.getValue.asPrimitive().toValue)).map(v => v.asInstanceOf[A])
    }*/

//(key: TodoKey,sku: String, title: String, description: String, userId: String, goToLink: String, courseLink: String, createdDate: DateTime, creditHours: Option[Double], daysUntilDue: Option[Int], assignedBy: Option[String])
  // TodoKey(componentId : String, componentTypeId: String, revisionDate: Long) extends LearningItemKey
  def fromClientEntityToLearningItem(entity: ClientEntity, sourceContext: TSourceContext): LearningItem = sourceContext.collection match {

    case LearningContextManager.KnownCollections.todos =>
      logger.debug(s"entering decoding for ${LearningContextManager.KnownCollections.todos}")
      val todoOption = for {
      _sku <- entity.getString(Todo.sku)
      _componentId <- entity.getString(Todo.componentID)
      _componentTypeId <- entity.getString(Todo.componentTypeID)
      _revisionDate <- entity.getLong(Todo.revisionDate)
      _title <- entity.getString(Todo.title)
      _description <- entity.getString(Todo.description)
      _userId <- entity.getString(Todo.userId)
      _goToLink <- entity.getString(Todo.goToLink)
      _courseLink <- entity.getString(Todo.courseLink)
      _createdDate <- entity.getJodaTimeFromMillis(Todo.createdDate)
    } yield Todo(key = TodoKey(componentId = _componentId,componentTypeId = _componentTypeId, revisionDate = _revisionDate), sku =  _sku, title = _title, description = _description, userId = _userId, goToLink = _goToLink,courseLink = _courseLink, createdDate = _createdDate, creditHours =  entity.getDouble(Todo.creditHours), daysUntilDue =  entity.getInt(Todo.daysUntilDue), assignedBy =  entity.getString(Todo.assignedBy))

      todoOption match {
      case Some(todo) => todo
      case None => throw new NoSuchElementException(s"An error has occurred while converting from clientEntity with Id ${entity.getString(Todo.sku)} to Todo for collection ${LearningContextManager.KnownCollections.todos}")
    }
  }


  def fromClientEntitySetToLearningItemSet(feed: ClientEntitySet, sourceContext: TSourceContext) = sourceContext.collection match {
    case LearningContextManager.KnownCollections.todos => {
      feed.getEntities.asScala.toList.map(p => fromClientEntityToLearningItem(p, sourceContext))
    }
  }

}
