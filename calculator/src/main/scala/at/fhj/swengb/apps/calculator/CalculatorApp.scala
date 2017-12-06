package at.fhj.swengb.apps.calculator

import java.awt.event.MouseEvent
import java.net.URL
import java.util.ResourceBundle
import javafx.application.Application
import javafx.event.{ActionEvent, EventHandler}
import javafx.beans.property.{ObjectProperty, SimpleObjectProperty}
import javafx.fxml.{FXML, FXMLLoader, Initializable}
import javafx.scene.control.{Button, Label}
import javafx.scene.{Parent, Scene}
import javafx.stage.Stage

import scala.util.{Failure, Success}
import scala.util.control.NonFatal

object CalculatorApp {

  def main(args: Array[String]): Unit = {
    Application.launch(classOf[CalculatorFX], args: _*)
  }
}

class CalculatorFX extends javafx.application.Application {

  val fxml = "/at/fhj/swengb/apps/calculator/calculator.fxml"
  val css = "/at/fhj/swengb/apps/calculator/calculator.css"

  def mkFxmlLoader(fxml: String): FXMLLoader = {
    new FXMLLoader(getClass.getResource(fxml))
  }

  override def start(stage: Stage): Unit =
    try {
      stage.setTitle("Calculator")
      setSkin(stage, fxml, css)
      stage.show()
      stage.setMinWidth(stage.getWidth)
      stage.setMinHeight(stage.getHeight)
      stage.setResizable(false)



    } catch {
      case NonFatal(e) => e.printStackTrace()
    }

  def setSkin(stage: Stage, fxml: String, css: String): Boolean = {
    val scene = new Scene(mkFxmlLoader(fxml).load[Parent]())
    stage.setScene(scene)
    stage.getScene.getStylesheets.clear()
    stage.getScene.getStylesheets.add(css)
  }

}

class CalculatorFxController extends Initializable {

  val calculatorProperty: ObjectProperty[RpnCalculator] = new SimpleObjectProperty[RpnCalculator](RpnCalculator())

  def getCalculator() : RpnCalculator = calculatorProperty.get()

  def setCalculator(rpnCalculator : RpnCalculator) : Unit = calculatorProperty.set(rpnCalculator)

  //Output labels
  @FXML private var lbValue1: Label = _
  @FXML private var lbValue2: Label = _
  @FXML private var lbResult: Label = _

  //Functionality Buttons
  @FXML private var btFunctionEnter: Button = _
  @FXML private var btFunctionComma: Button = _
  @FXML private var btFunctionClear: Button = _
  @FXML private var btFunctionSign: Button = _

  //All math function buttons of calculator
  @FXML var btFunctionMinus: Button = _
  @FXML var btFunctionPlus: Button =_
  @FXML var btFunctionMultiplication: Button = _
  @FXML var btFunctionDivision: Button = _

  //All number buttons of calculator
  @FXML var btNumberZero: Button = _
  @FXML var btNumberOne: Button = _
  @FXML var btNumberTwo: Button = _
  @FXML var btNumberThree: Button = _
  @FXML var btNumberFour: Button = _
  @FXML var btNumberFive: Button = _
  @FXML var btNumberSix: Button = _
  @FXML var btNumberSeven: Button = _
  @FXML var btNumberEight: Button = _
  @FXML var btNumberNine: Button = _


  private var writeValue2: Boolean = false
  private var firstNumberEntered: Boolean = false
  private var firstTimeClearPressed = true

  /**
    * Return expected output label(value1 or value2)
    * @return
    */
  private def getCurrentOutValueLabel: Label = {
    if (writeValue2 == true)
      lbValue2
    else
      lbValue1
  }

  @FXML private def onNumberButtonAction(event: ActionEvent): Unit = {
    val currButton = event.getSource.asInstanceOf[Button]

    currButton.getId match {
      case "btFunctionComma" => {
        //Print '.' on output and deactivate Comma Button for this time
        getCurrentOutValueLabel.setText(getCurrentOutValueLabel.getText + ".")
        btFunctionComma.setDisable(true);
      }
      case _   => {
        //It was a number button:
        //Check if this is the first digit. In this case we have to replace the default value
        if ( ! firstNumberEntered) {
          firstNumberEntered = true
          getCurrentOutValueLabel.setText(currButton.getText)
          btFunctionComma.setDisable(false)
        } else {
          getCurrentOutValueLabel.setText(getCurrentOutValueLabel.getText + currButton.getText)
        }
      }
    }
  }

  /**
    * Helper function to disable all function buttons
    * @param disable
    */
  private def handleMathFunctionButtons(disable: Boolean): Unit = {
    btFunctionMinus.setDisable(disable)
    btFunctionPlus.setDisable(disable)
    btFunctionMultiplication.setDisable(disable)
    btFunctionDivision.setDisable(disable)
  }

  private def handleNumberButtons(disable: Boolean): Unit = {
    btNumberZero.setDisable(disable)
    btNumberOne.setDisable(disable)
    btNumberTwo.setDisable(disable)
    btNumberThree.setDisable(disable)
    btNumberFour.setDisable(disable)
    btNumberFive.setDisable(disable)
    btNumberSix.setDisable(disable)
    btNumberSeven.setDisable(disable)
    btNumberEight.setDisable(disable)
    btNumberNine.setDisable(disable)

    btFunctionSign.setDisable(disable)
  }

  /**
    * Enter Button pressed - Indicator that first value is entered
    * @param event
    */
  @FXML private def onEnterButtonAction(event: ActionEvent): Unit = {
    //Now we write get information about value 2
    writeValue2 = true

    //Reset function buttons(Comma,Clear) and flag which informs that the first digit of the value is set
    btFunctionComma.setDisable(true)
    firstNumberEntered = false
    firstTimeClearPressed = true

    /*Disable Enter button:  We do not support more than 3 values
      Activate Math-Function-Buttons: After the number we expect a math function
    */
    btFunctionEnter.setDisable(true)
    handleMathFunctionButtons(false);
  }

  /**
    * Reset Calculator - Clear button pressed
    * If its pressed the first time, just reset curent value label
    * If it pressed the second time, reset whole calculator
    */

  @FXML private def onClearButtonAction(event: ActionEvent): Unit = {

    //On first click on Clear button, just reset label context of current value
    if (firstTimeClearPressed ) {
      getCurrentOutValueLabel.setText("0")
      firstNumberEntered = false;
      firstTimeClearPressed = false;
    } else {
      //Reset Enter button(Activate it again)
      //Reset comma button(Deactivate the button)
      btFunctionEnter.setDisable(false)
      btFunctionComma.setDisable(true)
      firstNumberEntered = false
      firstTimeClearPressed = true

      //Deactivate function buttons again
      handleMathFunctionButtons(true)
      handleNumberButtons(false)


      //Reset values and output
      writeValue2 = false;
      lbValue1.setText("0");
      lbValue2.setText("0");
      lbResult.setText("");
    }
  }

  @FXML private def onSignChangeButtonAction(event: ActionEvent): Unit = {

    val currLabel = getCurrentOutValueLabel
    val currText = currLabel.getText;

    //Due to default value there exists always a head
    if (currText.head.equals('-'))
      currLabel.setText(currText.tail) //just remove '-'
    else
      currLabel.setText("-" + currText)
  }

  /**
    * User pressed a math function.
    * Calculate result and print on output line
    * @param event
    */
  @FXML private def onMathFunctionButtonAction(event: ActionEvent): Unit = {

    val pressedButton = event.getSource.asInstanceOf[Button];
    val value1: Val = Val(lbValue1.getText.toDouble)
    val value2: Val = Val(lbValue2.getText.toDouble)

    var mathFunction: BinOp = null;
    pressedButton.getText match {
      case "+" => mathFunction = Add
      case "-" => mathFunction = Sub
      case "*" => mathFunction = Mul
      case "/" => mathFunction = Div
      case _ => println("Not supoorted function <" + pressedButton.getText + ">")

    }

    if(mathFunction == null){
      lbResult.setText("Not supported math function! - Contact this crazy developer!")
    }

    //Calc result and print on output
    var result: Val = mathFunction.eval(value1,value2)
    if(result.value.isNaN)
      lbResult.setText("Not a number!")
    else
      lbResult.setText(result.value.toString)


    //Now deactivate all buttons except Clear
    handleMathFunctionButtons(true)
    handleNumberButtons(true)
  }

  override def initialize(location: URL, resources: ResourceBundle): Unit = {}
}