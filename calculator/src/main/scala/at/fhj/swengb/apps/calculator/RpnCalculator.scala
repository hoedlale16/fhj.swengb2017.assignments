package at.fhj.swengb.apps.calculator

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
      val myStack: List[Op] = s.split(' ').map(e => Op(e)).toList
      var myCalc: Try[RpnCalculator] = Try(RpnCalculator())

      for (elem <- myStack) {
        myCalc = myCalc.get.push(elem)
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

    if (op.isInstanceOf[Val]) {
      //Return a new instance of RpnCalculator with a higher stack
      Try(RpnCalculator(op :: stack))
    } else {

      //Check if there are still values on stack
      //TODO: We have to throw here a exception if it is empty

      //Operation detected try to execute it
      val fstVal = this.pop()._1.asInstanceOf[Val]
      val sndVal = this.pop()._2.pop()._1.asInstanceOf[Val]

      //new Calc-Obj 2 elements above removed
      var myStack = this.pop()._2.pop()._2.stack

      val result: Val = op.asInstanceOf[BinOp].eval(sndVal, fstVal)
      push(result :: myStack)
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
