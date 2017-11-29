package at.fhj.swengb.apps.calculator

import java.net.URL
import java.util.ResourceBundle
import javafx.application.Application
import javafx.event.{ActionEvent, EventHandler}
import javafx.fxml.{FXML, FXMLLoader, Initializable}
import javafx.scene.control.{Button, Label}
import javafx.scene.input.MouseEvent
import javafx.scene.{Parent, Scene}
import javafx.stage.Stage

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
      stage.setTitle("Calculator by Alexander Hödl")
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

  @FXML private var lbValue1: Label = null;
  @FXML private var lbValue2: Label = null;
  @FXML private var lbResult: Label = null;
  @FXML private var btEnter: Button = null;

  private var writeValue2: Boolean = false;

  /**
    * Return expected output File
    * @return
    */
  private def getCurrentOutValueLabel(): Label = {
    if (writeValue2 == true)
      lbValue2;
    else
      lbValue1;
  }
  @FXML private def onNumberButtonAction(event: ActionEvent): Unit = {
    val currButton = event.getSource.asInstanceOf[Button];
    val outputLabel = getCurrentOutValueLabel

    currButton.getId match {
      case "btComma" => outputLabel.setText(outputLabel.getText() + ".")
      case _   => outputLabel.setText(outputLabel.getText() + currButton.getText)
    }
  }

  /**
    * Enter Button pressed - Indicator that first value is entered
    * @param event
    */
  @FXML private def onEnterButtonAction(event: ActionEvent): Unit = {
    writeValue2 = true;
    btEnter = event.getSource.asInstanceOf[Button]
    btEnter.setDisable(true);
  }

  /**
    * Reset Calculator - Clear button pressed
    */
  @FXML private def onClearButtonAction(event: ActionEvent): Unit = {
    //Check if Enter-Button is already set. If yes, deactivate button again
    if (btEnter != null)
      btEnter.setDisable(false)

    //Reset values and output
    writeValue2 = false;
    lbValue1.setText("");
    lbValue2.setText("");
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

  override def initialize(location: URL, resources: ResourceBundle) = {
    /*TODO*/
  }

  def sgn(): Unit = {
    println("an event has happened")
  }
}
