package at.fhj.swengb.apps.calculator

import java.util.NoSuchElementException

import scala.util.{Try}

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
      val myStack: List[Op] = s.split(' ').map(e => Op(e)).toList

      //Create new empty Calc and push all elements on stack
      var myCalc: Try[RpnCalculator] = Try(RpnCalculator())
      try {
        for (elem <- myStack) {
          if (myCalc.isFailure) {
            throw new NoSuchElementException;
          } else {
            myCalc = myCalc.get.push(elem)
          }
        }
      }catch {
        case _ => Try[RpnCalculator](throw new NoSuchElementException)
      }

      myCalc
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
    * @param op
    * @return
    */
  def push(op: Op): Try[RpnCalculator] = {

    op match {
      case v: Val => Try(RpnCalculator(stack :+ op))
      case o: BinOp => {
        try {
          /*Operation detected: try to execute it
          -> Get first element from stack (is possible, otherwise peek returns exception)
          -> Remove first element(pop) and continue with remaining stack
          -> Try to get snd element from stack( if possible, otherwise peek returns exception)
          -> Remove snd element(pop) and continue with remaining stack
          -> Execute Operation and push result on remaining Stack
         */

          //Try to get first element and remove it from stack
          val fstVal = peek.asInstanceOf[Val]
          var remainCalc = pop._2

          //Try to get snd element and remove it from stack
          val sndVal = remainCalc.peek.asInstanceOf[Val]
          remainCalc = remainCalc.pop._2

          val result: Val = o.eval(fstVal, sndVal)
          //Ass result to remaining Calculator
          remainCalc.push(result)
        } catch {
          case _ => Try[RpnCalculator](throw new NoSuchElementException)
        }
       }

    }
  }

  /**
    * Pushes val's on the stack.
    *
    * If op is not a val, pop two numbers from the stack and apply the operation.
    *
    * @param op
    * @return
    */
  def push(op: Seq[Op]): Try[RpnCalculator] = {

    var myCalc = RpnCalculator()
    for (elem <- op.toList) {
      myCalc = myCalc.push(elem).get
    }
    Try(myCalc)
  }

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
      stack.head;
  }

  /**
    * returns the size of the stack.
    *
    * @return
    */
  def size: Int = stack.size
}
