package net.ajn.credentialutil.client.ncf.filter

trait Expression {
  import Expression._

  def eq(that: Expression): Expression = Eq(this, that)
  def ne(that: Expression): Expression = Ne(this, that)
  def or(that: Expression): Expression = Or(this, that)
  def and(that: Expression): Expression = And(this, that)
}

private[filter] object Expression {
  case class StringLiteral(value: String) extends Expression {
    override def toString: String = "'" + value + "'"
  }

  case class LongLiteral(value: Long) extends Expression {
    override def toString: String = value.toString
  }

  case class NumberLiteral(value: Int) extends Expression {
    override def toString: String = value.toString
  }

  case class BooleanLiteral(value: Boolean) extends Expression {
    override def toString: String = value.toString
  }

  case class Property(name: String) extends Expression {
    override def toString: String = name
  }

  case class Eq(a: Expression, b: Expression) extends Expression {
    override def toString: String = s"${a.toString} eq ${b.toString}"
  }

  case class Ne(a: Expression, b: Expression) extends Expression {
    override def toString: String = s"${a.toString} eq ${b.toString}"
  }

  case class Or(a: Expression, b: Expression) extends Expression {
    override def toString: String = s"($a) or ($b)"
  }

  case class And(a: Expression, b: Expression) extends Expression {
    override def toString: String = s"($a) and ($b)"
  }
}
