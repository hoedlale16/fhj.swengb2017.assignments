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
      Try(RpnCalculator(myStack))
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
    * is just put on the
    * stack, if not then the stack is examined and the correct operation is performed.
    *
    * @param op
    * @return
    */
  def push(op: Op): Try[RpnCalculator] = {
    op match {
      case Val(x) => Try(RpnCalculator((stack :+ op)))
      case _ => {
        val value1: Val = stack(0).asInstanceOf[Val];
        val value2: Val = stack(1).asInstanceOf[Val];

        val result: List[Op] = List(op.asInstanceOf[BinOp].eval(value1,value2))
        Try(RpnCalculator(result))
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

    var myCalc = RpnCalculator(op.toList)
    for(elem <- myCalc.stack) {
      if (elem.isInstanceOf[BinOp]) {
        myCalc = myCalc.push(elem).get
      }
    }

    Try(myCalc)

  }

  /**
    * Returns an tuple of Op and a RevPolCal instance with the remainder of the stack.
    *
    * @return
    */
  def pop(): (Op, RpnCalculator) = (stack.head,RpnCalculator(stack.tail))

  /**
    * If stack is nonempty, returns the top of the stack.
    * If it is empty, this function throws a NoSuchElementException.
    *
    * @return
    */
  def peek(): Op = {

    var myInteralStack: List[Val] = List();

    for( elem <- stack) {
      //If current element is a Value, add to stack
      if (elem.isInstanceOf[Val])
        myInteralStack = elem.asInstanceOf[Val] :: myInteralStack
      else
        //If current element is an operation, check if enough values on stack
        if (myInteralStack.size >= 2 ) {
          /* If so, take first 2 elements and apply function.
          *  Add result of function to stack agein...*/

          //Take first elemet from internal stack
          val fstValue = myInteralStack.head
          myInteralStack = myInteralStack.tail

          //Take second element from stack
          val sndValue = myInteralStack.head
          myInteralStack = myInteralStack.tail

          //Calc result and add result on stack
          val result = elem.asInstanceOf[BinOp].eval(fstValue,sndValue)
          myInteralStack = result :: myInteralStack

        } else
          //Not enoung elements to execute operation!
          throw new NoSuchElementException()
    }

    //We're done, return result which is the first element of the internal stack
    if(myInteralStack.isEmpty)
      throw new NoSuchElementException()
    else
      myInteralStack.head
  }

  /**
    * returns the size of the stack.
    *
    * @return
    */
  def size: Int = stack.size
}