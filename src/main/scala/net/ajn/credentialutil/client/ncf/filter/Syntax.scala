package net.ajn.credentialutil.client.ncf.filter

object Syntax {
  import Expression._

  implicit class PimpedString(a: String) {
    def ===(b: String): Expression = Property(a) eq StringLiteral(b)
    def ===(b: Int): Expression = Property(a) eq NumberLiteral(b)
    def ===(b:Boolean): Expression = Property(a) eq BooleanLiteral(b)
    def !==(b: String): Expression = Property(a) ne StringLiteral(b)
    def !==(b:Int): Expression = Property(a) ne NumberLiteral(b)
    def !==(b:Boolean): Expression = Property(a) ne BooleanLiteral(b)
    def ===(l:Long): Expression = Property(a) eq LongLiteral(l)
    def !==(l:Long): Expression = Property(a) ne LongLiteral(l)
  }


}
