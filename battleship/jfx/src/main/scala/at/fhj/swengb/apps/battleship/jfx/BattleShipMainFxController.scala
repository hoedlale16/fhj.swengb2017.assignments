package at.fhj.swengb.apps.battleship.jfx

import java.io.File
import java.net.URL
import java.nio.file.{Files, Paths}
import java.util.ResourceBundle
import javafx.fxml.{FXML, FXMLLoader, Initializable}
import javafx.scene.Parent
import javafx.stage.FileChooser.ExtensionFilter
import javafx.stage.{FileChooser, Modality, Stage}

import at.fhj.swengb.apps.battleship.BattleShipProtocol
import at.fhj.swengb.apps.battleship.model.{BattlePos, BattleShipGame}

class BattleShipMainFxController extends Initializable {



  override def initialize(location: URL, resources: ResourceBundle) = {
  }

  /**
    * Change Scene to Game scene
    */
  @FXML def onStartGame(): Unit = {
    BattleShipFxApp.loadGameScene
  }

  /**
    * Change Scene to Game scene
    */
  @FXML def onShowHighscore(): Unit = {
    BattleShipFxApp.loadHighscoreScene
  }

  /**
    * Change Scene to Game scene
    */
  @FXML def onShowCredits(): Unit = {
    BattleShipFxApp.loadCreditsScene
  }

}
