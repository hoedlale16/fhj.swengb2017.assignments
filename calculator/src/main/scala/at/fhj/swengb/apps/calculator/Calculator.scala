package at.fhj.swengb.apps.calculator

import scala.util.matching.Regex

case class Calculator(val number1: Number,
                      val number2: Number) {




}

case class Number(strValue: String) {

  /*
    ^(\\-|[0-9]) .. Number is allowed to start either with a '-' or a Numeric digit
    [0-9]*       .. Followed by 0..n numeric digicts
    (\.{1}[0-9]*){0,1} .. Followd by . and Numbers. {0 .. 1 times} -> It is not allowed to end with a '.'
   */
  val myNumberRegex: String = "^(\\-|[0-9])[0-9]*(\\.{1}[0-9]*){0,1}"

  require(strValue.matches(myNumberRegex));

  /**
    * Returns given Number as Double vaue
    * @return
    */
  def getNumber(): Double = strValue.toDouble

}
