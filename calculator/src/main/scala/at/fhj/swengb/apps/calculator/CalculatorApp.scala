package at.fhj.swengb.apps.calculator

import java.net.URL
import java.util.ResourceBundle
import javafx.application.Application
import javafx.event.ActionEvent
import javafx.beans.property.{ObjectProperty, SimpleObjectProperty}
import javafx.fxml.{FXML, FXMLLoader, Initializable}
import javafx.scene.control.{Button, Label, TextArea}
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
      stage.setTitle("Calculator by Alexander Hödl (IMA16 - SWENGB)")
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

  val calculatorProperty: ObjectProperty[RpnCalculator] =
    new SimpleObjectProperty[RpnCalculator](RpnCalculator())

  def getCalculator(): RpnCalculator = calculatorProperty.get

  def setCalculator(rpnCalculator: RpnCalculator): Unit =
    calculatorProperty.set(rpnCalculator)

  //Output labels
  @FXML private var lbValue1: Label = _
  @FXML private var lbValue2: Label = _
  @FXML private var lbResult: Label = _
  @FXML private var taStack: TextArea = _

  //Functionality Buttons
  @FXML private var btFunctionComma: Button = _

  //All math function buttons of calculator
  @FXML var btFunctionMinus: Button = _
  @FXML var btFunctionPlus: Button = _
  @FXML var btFunctionMultiplication: Button = _
  @FXML var btFunctionDivision: Button = _

  private var writeToLabel2: Boolean = false
  private var replaceLabelText: Boolean = true
  private var clearJustLabelContent = true
  private var positiveNumber: Boolean = true;

  /**
    * Return expected output label(value1 or value2)
    * @return
    */
  private def getCurrentOutValueLabel: Label = {
    if (writeToLabel2)
      lbValue2
    else
      lbValue1
  }

  @FXML private def onNumberButtonAction(event: ActionEvent): Unit = {
    val currButton = event.getSource.asInstanceOf[Button]

    currButton.getId match {
      case "btFunctionComma" =>
        //Print '.' on output and deactivate Comma Button for this time
        getCurrentOutValueLabel.setText(getCurrentOutValueLabel.getText + ".")
        btFunctionComma.setDisable(true)
      case _ => {
        //It was a number button:
        //Check if this is the first digit. In this case we have to replace the default value
        if (replaceLabelText) {
          replaceLabelText = false
          getCurrentOutValueLabel.setText(currButton.getText)
          btFunctionComma.setDisable(false)
        } else {
          getCurrentOutValueLabel.setText(
            getCurrentOutValueLabel.getText + currButton.getText)
        }
      }
    }
  }

  /**
    * Enter Button pressed - Indicator that first value is entered
    * @param event
    */
  @FXML private def onEnterButtonAction(event: ActionEvent): Unit = {

    var labelText: String = getCurrentOutValueLabel.getText
    if (labelText.isEmpty)
      labelText = "0" //Fallback - Wurde nichts eingegeben, wirds 0

    //Add number to stack
    getCalculator().push(Op(labelText)) match {
      case Success(c) => setCalculator(c)
      case Failure(e) => println("Error occured: " + e.getMessage)
    }

    //Reset variables for second number
    this.replaceLabelText = true;
    this.writeToLabel2 = true;
    this.positiveNumber = true;

    //Refresh Stack on GUI
    taStack.setText(getCalculator().stack.mkString("\n"))
  }

  /**
    * Reset Calculator - Clear button pressed
    * If its pressed the first time, just reset curent value label
    * If it pressed the second time, reset whole calculator
    */

  @FXML private def onClearButtonAction(event: ActionEvent): Unit = {
    //On first click on Clear button, just reset label context of current value
    if (clearJustLabelContent) {
      //Reset handling variables
      replaceLabelText = true
      clearJustLabelContent = false

      //Reset Output
      getCurrentOutValueLabel.setText("")
    } else {
      //Reset Stack and Refresh Stack on GUI
      setCalculator(RpnCalculator())
      taStack.setText(getCalculator().stack.mkString("\n"))

      //Reset handling variables
      writeToLabel2 = false
      replaceLabelText = true
      clearJustLabelContent = true

      //Reset output
      lbValue1.setText("")
      lbValue2.setText("")
      lbResult.setText("")
    }
  }

  @FXML private def onSignChangeButtonAction(event: ActionEvent): Unit = {

    val currLabel = getCurrentOutValueLabel
    val currText = currLabel.getText;

    if (positiveNumber) {
      positiveNumber = false
      currLabel.setText("-" + currText)
    } else {
      positiveNumber = true
      if (currText.head.equals('-'))
        currLabel.setText(currText.tail) //just remove '-'
      else
        currLabel.setText(currText)
    }
  }

  /**
    * User pressed a math function.
    * Calculate result and print on output line
    * @param event
    */
  @FXML private def onMathFunctionButtonAction(event: ActionEvent): Unit = {

    val pressedButton = event.getSource.asInstanceOf[Button];
    var mathFunction: BinOp = null;
    pressedButton.getText match {
      case "+" => mathFunction = Add
      case "-" => mathFunction = Sub
      case "*" => mathFunction = Mul
      case "/" => mathFunction = Div
    }

    getCalculator().push(mathFunction) match {
      case Success(c) => {
        setCalculator(c)

        //Refresh Stack on GUI
        taStack.setText(getCalculator().stack.mkString("\n"))

        getCalculator().stack.last match {
          case v: Val => {
            if (v.value.isNaN)
              lbResult.setText("Not a number!")
            else
              lbResult.setText(v.value.toString)
          }
          case b: BinOp =>
            lbResult.setText(
              "Error in stack - Detected BinOp - Reset Calculator!");
        }
      }
      case Failure(e) => lbResult.setText("Error in stack - Reset Calculator!");
    }

    //Assume that user pressed already once for clear to reset whole calc on
    //fist button click
    clearJustLabelContent = false
  }

  override def initialize(location: URL, resources: ResourceBundle): Unit = {}
}
