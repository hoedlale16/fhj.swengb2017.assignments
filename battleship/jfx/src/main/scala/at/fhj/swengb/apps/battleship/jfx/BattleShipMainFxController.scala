package at.fhj.swengb.apps.battleship.jfx

import java.net.URL
import java.util.ResourceBundle
import javafx.fxml.{FXML, FXMLLoader, Initializable}
import javafx.scene.Parent
import javafx.stage.{Modality, Stage}

class BattleShipMainFxController extends Initializable {



  override def initialize(location: URL, resources: ResourceBundle) = {
  }

  /**
    * Change Scene to Game scene
    */
  @FXML def onStartNewGame(): Unit = {
    BattleShipFxApp.sceneManager.loadGameScene
  }

  /**
    * Change Scene to Game scene
    */
  @FXML def onShowHighscore(): Unit = {
    BattleShipFxApp.sceneManager.loadHighscoreScene
  }

  /**
    * Change Scene to Game scene
    */
  @FXML def onShowCredits(): Unit = {
    BattleShipFxApp.sceneManager.loadCreditsScene
  }

}
