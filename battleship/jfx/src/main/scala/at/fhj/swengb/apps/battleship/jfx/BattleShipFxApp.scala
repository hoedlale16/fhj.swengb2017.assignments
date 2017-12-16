package at.fhj.swengb.apps.battleship.jfx

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.{Parent, Scene}
import javafx.stage.Stage

import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}

object BattleShipFxApp {

  def main(args: Array[String]): Unit = {
    Application.launch(classOf[BattleShipFxApp], args: _*)
  }

}


class BattleShipFxApp extends Application {

  val fxml = "/at/fhj/swengb/apps/battleship/jfx/battleshipfx.fxml"
  val css = "/at/fhj/swengb/apps/battleship/jfx/battleshipfx.css"

  val triedRoot = Try(FXMLLoader.load[Parent](getClass.getResource(fxml)))

  override def start(stage: Stage) = {
    triedRoot match {
      case Success(root) =>
        stage.setScene(new Scene(root))
        stage.setTitle("BattleshipGame by Alexander Hödl/ Gregor Fernbach (IMA16 - SWENGB)")
        setSkin(stage,fxml,css)
        stage.show()
      case Failure(e) => e.printStackTrace()
    }
  }

  def setSkin(stage: Stage, fxml: String, css: String): Boolean = {
    val scene = new Scene(new FXMLLoader(getClass.getResource(fxml)).load[Parent]())
    stage.setScene(scene)
    stage.getScene.getStylesheets.clear()
    stage.getScene.getStylesheets.add(css)
  }

  /*
  def mkFxmlLoader(fxml: String): FXMLLoader = {
    new FXMLLoader(getClass.getResource(fxml))
  }

  override def start(stage: Stage): Unit =
    try {
      stage.setTitle("BattleshipGame by Alexander Hödl/ Gregor Fernbach (IMA16 - SWENGB)")
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
  */

}