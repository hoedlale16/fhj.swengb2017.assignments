package at.fhj.swengb.apps.calculator

import java.util.NoSuchElementException

import scala.util.Try

/**
  * Companion object for our reverse polish notation calculator.
  */
object RpnCalculator {

  /**
    * Returns empty RpnCalculator if string is empty, otherwise pushes all operations
    * on the stack of the empty RpnCalculator.
    *
    * @param s a string representing a calculation, for example '1 2 +'
    * @return
    */
  def apply(s: String): Try[RpnCalculator] = {
    if (s.isEmpty)
      Try(RpnCalculator())
    else {
      try {
        val myStack: List[Op] = s.split(' ').map(e => Op(e)).toList
        myStack.foldLeft(Try(RpnCalculator()))(
          (acc, elem) => acc.get.push(elem))
      } catch {
        case e: Exception => Try[RpnCalculator](throw e)
      }
    }
  }

}

/**
  * Reverse Polish Notation Calculator.
  *
  * @param stack a datastructure holding all operations
  */
case class RpnCalculator(stack: List[Op] = Nil) {

  /**
    * By pushing Op on the stack, the Op is potentially executed. If it is a Val,  the op instance
    * is just put on the stack,
    * if not then the stack is examined and the correct operation is performed.
    *
    * @param op - New Value or Operation added to to stack
    * @return
    */
  def push(op: Op): Try[RpnCalculator] = {

    op match {
      case v: Val => Try(RpnCalculator(stack :+ v))
      case o: BinOp =>
        try {
          //Helper method to return next value or throws Exception
          def getNextVal(rpnCal: RpnCalculator): Val = {
            val myVal = rpnCal.peek()
            myVal match {
              case v: Val   => v
              case _: BinOp => throw new NoSuchElementException
            }
          }

          //Try to get first element and remove it from stack
          val fstVal = getNextVal(this)
          var remainCalc = pop()._2

          //Try to get snd element and remove it from stack
          val sndVal = getNextVal(remainCalc)
          remainCalc = remainCalc.pop()._2

          //Execute given Operation with values
          val result: Val = o.eval(fstVal, sndVal)
          //Add result on stack
          remainCalc.push(result)
        } catch {
          case e: Exception => Try[RpnCalculator](throw e)
        }
    }
  }

  /**
    * Pushes val's on the stack.
    *
    * If op is not a val, pop two numbers from the stack and apply the operation.
    *
    * @param op New op instance to add to calc
    * @return
    */
  def push(op: Seq[Op]): Try[RpnCalculator] =
    op.foldLeft(Try(RpnCalculator()))((acc, elem) => acc.get.push(elem))

  /**
    * Returns an tuple of Op and a RpnCalculator instance with the remainder of the stack.
    *
    * @return
    */
  def pop(): (Op, RpnCalculator) = (stack.head, RpnCalculator(stack.tail))

  /**
    * If stack is nonempty, returns the top of the stack.
    * If it is empty, this function throws a NoSuchElementException.
    *
    * @return
    */
  def peek(): Op = {
    if (stack.isEmpty)
      throw new NoSuchElementException
    else
      stack.head
  }

  /**
    * returns the size of the stack.
    *
    * @return
    */
  def size: Int = stack.size
}
