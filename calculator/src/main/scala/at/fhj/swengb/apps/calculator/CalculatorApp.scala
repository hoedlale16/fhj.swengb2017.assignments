package at.fhj.swengb.apps.calculator

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


  //All math function buttons of calculator
  @FXML var btFunctionMinus: Button = _
  @FXML var btFunctionPlus: Button =_
  @FXML var btFunctionMultiplication: Button = _
  @FXML var btFunctionDivision: Button = _

  private var writeValue2: Boolean = false
  private var firstNumberEntered: Boolean = false

  /**
    * Return expected output File
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
    val outputLabel = getCurrentOutValueLabel

    println(currButton.getId)
    currButton.getId match {
      case "btFunctionComma" => {
        outputLabel.setText(outputLabel.getText + ".")

        //Deactivate Comma Button
        btFunctionComma.setDisable(true);
      }
      case _   => {

        //Check if this is the first number of
        if ( ! firstNumberEntered) {
          firstNumberEntered = true
          outputLabel.setText(currButton.getText)
          btFunctionComma.setDisable(false)
        } else {
          outputLabel.setText(outputLabel.getText + currButton.getText)
        }
      }
    }
  }

  private def handleFunctionButtons(disable: Boolean): Unit = {
    btFunctionMinus.setDisable(disable)
    btFunctionPlus.setDisable(disable)
    btFunctionMultiplication.setDisable(disable)
    btFunctionDivision.setDisable(disable)
  }

  /**
    * Enter Button pressed - Indicator that first value is entered
    * @param event
    */
  @FXML private def onEnterButtonAction(event: ActionEvent): Unit = {
    writeValue2 = true
    btFunctionEnter.setDisable(true)


    //Deactivate Comma Button and reset flag for first digit in value
    btFunctionComma.setDisable(true);
    firstNumberEntered = false;

    //Activate function Buttons
    handleFunctionButtons(false);

  }

  /**
    * Reset Calculator - Clear button pressed
    */
  @FXML private def onClearButtonAction(event: ActionEvent): Unit = {
    //Check if Enter-Button is already set. If yes, deactivate button again
    btFunctionEnter.setDisable(false)

    //Reset comma button
    btFunctionComma.setDisable(true);
    firstNumberEntered = false;

    //Deactivate function buttons again
    handleFunctionButtons(true)

    //Reset values and output
    writeValue2 = false;
    lbValue1.setText("0");
    lbValue2.setText("0");
    lbResult.setText("");
  }

  @FXML private def onSignChangeButtonAction(event: ActionEvent): Unit = {

    val currLabel = getCurrentOutValueLabel
    val currText = currLabel.getText;

    if (currText.isEmpty) {
      currLabel.setText("-");
    } else {
      if (currText.head.equals('-'))
        currLabel.setText(currText.tail)
      else
        currLabel.setText("-" + currText)
    }
  }

  @FXML private def onMathFunctionButtonAction(event: ActionEvent): Unit = {

    val pressedButton = event.getSource.asInstanceOf[Button];

    var mathFunction: String = pressedButton.getText;
    val value1 = lbValue1.getText;
    val value2 = lbValue2.getText;

    val myResult = "TODO: Call polish calc api with: <" + value1 + "> - <" + value2 + "> (" + mathFunction +")";
    lbResult.setText(myResult)
  }

  override def initialize(location: URL, resources: ResourceBundle): Unit = {
    /*TODO*/
  }
}