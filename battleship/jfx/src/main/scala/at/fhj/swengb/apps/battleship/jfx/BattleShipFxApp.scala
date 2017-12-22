package at.fhj.swengb.apps.battleship.jfx

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.{Parent, Scene}
import javafx.stage.Stage

import scala.util.{Failure, Success, Try}

object BattleShipFxApp {
  //Represents the main Window frame... Get initialized when application get started
  var rootStage: Stage = null //If this stays null some cray shit is going on...

  def main(args: Array[String]): Unit = {
    Application.launch(classOf[BattleShipFxApp], args: _*)
  }

  def loadMainScene: Unit = loadScene("/at/fhj/swengb/apps/battleship/jfx/battleshipMainfx.fxml")
  def loadGameScene: Unit = loadScene("/at/fhj/swengb/apps/battleship/jfx/battleshipGamefx.fxml")
  def loadHighscoreScene: Unit = loadScene("/at/fhj/swengb/apps/battleship/jfx/battleshipHighscorefx.fxml")
  def loadCreditsScene: Unit = loadScene("/at/fhj/swengb/apps/battleship/jfx/battleshipCreditsfx.fxml")

  /**
    * Internal method to loadScene scene from given fxml
    * Scene
    *
    * @param fxml
    */
  private def loadScene(fxml: String): Unit = {
    val stage: Stage = BattleShipFxApp.rootStage

    if (stage == null) {
      println("Window currently not set - Abort!")
      System.exit(1) //If we're here either the developer is stupid and want to
      // loadScene a scene without window-frame or
      // some crazy shit was going on
    } else {
      val triedScene = Try(FXMLLoader.load[Parent](getClass.getResource(fxml)))
      triedScene match {
        case Success(root) =>
          stage.setTitle("BattleshipGame by SC1G0 (SWENGB-IMA16)")
          stage.setScene(new Scene(root))

          //Set CSS Style
          stage.getScene.getStylesheets.clear()
          val css = "/at/fhj/swengb/apps/battleship/jfx/battleshipfx.css"
          stage.getScene.getStylesheets.add(css)

          //Display stage...
          stage.show()
        case Failure(e) => e.printStackTrace()
      }
    }
  }
}

/*
Here starts the application. rootStage (represents frame/window) get initializied and main Scene(Content from FXML)
get loaded
 */
class BattleShipFxApp extends Application {
  override def start(stage: Stage): Unit = {
    //Assign main root stage to allow to set differenct sceens in this frame
    BattleShipFxApp.rootStage = stage
    //Load MAIN scene for start
    BattleShipFxApp.loadMainScene
  }
}

/**
  * Class to switch between scenes which represents the main windows
  * -> Main-Menu
  * -> Game-Scene
  * -> Highscore
  * -> Credits
  */
class SceneManager {


}
