package at.fhj.swengb.apps.battleship.jfx

import java.net.URL
import java.util.ResourceBundle
import javafx.fxml.{FXML, Initializable}
import javafx.scene.Scene

class BattleShipMainFxController extends Initializable {



  override def initialize(location: URL, resources: ResourceBundle) = {
  }

  /**
    * Change Scene to Game scene
    */
  @FXML def onStartGame(): Unit = {
    val scene: Scene = BattleShipFxApp.getGameScene
    BattleShipFxApp.loadScene(scene,BattleShipFxApp.getRootStage())
  }

  /**
    * Change Scene to Game scene
    */
  @FXML def onShowHighscore(): Unit = {
    val scene: Scene = BattleShipFxApp.getHighscoreScene
    BattleShipFxApp.loadScene(scene,BattleShipFxApp.getRootStage())

  }

  /**
    * Change Scene to Game scene
    */
  @FXML def onShowCredits(): Unit = {
    val scene: Scene = BattleShipFxApp.getCreditsScene
    BattleShipFxApp.loadScene(scene,BattleShipFxApp.getRootStage())
  }

}
