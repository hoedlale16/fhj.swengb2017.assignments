package at.fhj.swengb.apps.battleship.jfx

import java.nio.file.Paths
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.image.Image
import javafx.scene.{Parent, Scene}
import javafx.stage.Stage

import scala.util.{Failure, Success, Try}

object BattleShipFxApp {
  //Become initialized when GUI starts
  var rootStage: Stage = _; //If this stays null, some crazy shit is going on...

  def main(args: Array[String]): Unit = {
    //TODO: Splash-Screen: Implement Splashscreen while loading project

    Application.launch(classOf[BattleShipFxApp], args: _*)
  }

  def loadMainScene(): Unit = loadScene("/at/fhj/swengb/apps/battleship/jfx/fxml/battleshipMainfx.fxml")
  def loadGameScene(): Unit = loadScene("/at/fhj/swengb/apps/battleship/jfx/fxml/battleshipGamefx.fxml")
  def loadHighscoreScene(): Unit = loadScene("/at/fhj/swengb/apps/battleship/jfx/fxml/battleshipHighscorefx.fxml")
  def loadCreditsScene(): Unit = loadScene("/at/fhj/swengb/apps/battleship/jfx/fxml/battleshipCreditsfx.fxml")


  /**
    * Internal method to loadScene scene from given fxml
    * Scene
    *
    * @param fxml - Path to fxml to load
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
          stage.setResizable(false)

          //Set CSS Style
          stage.getScene.getStylesheets.clear()
          val css = "/at/fhj/swengb/apps/battleship/jfx/battleshipfx.css"
          stage.getScene.getStylesheets.add(css)

          //Set Icon and Display stage...
          stage.getIcons().add(new Image(getClass.getResource("/at/fhj/swengb/apps/battleship/jfx/pics/logo.jpg").toString))
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

    //TODO: Play some background music
  }
}