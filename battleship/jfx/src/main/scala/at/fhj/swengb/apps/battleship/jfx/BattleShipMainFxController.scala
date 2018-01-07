package at.fhj.swengb.apps.battleship.jfx

import java.net.URL
import java.util.ResourceBundle
import javafx.fxml.{FXML, Initializable}
import javafx.scene.Scene
import javafx.scene.control.Button

import at.fhj.swengb.apps.battleship.model.BattleShipJukeBox

class BattleShipMainFxController extends Initializable {

  @FXML var btSound: Button = _


  override def initialize(location: URL, resources: ResourceBundle) = {
  }

  /**
    * Change Scene to Game scene
    */
  @FXML def onStartGame(): Unit = {
    val scene: Scene = BattleShipFxApp.getGameScene
    BattleShipFxApp.loadScene(scene,BattleShipFxApp.getRootStage)
  }

  /**
    * Change Scene to Game scene
    */
  @FXML def onShowHighscore(): Unit = {
    val scene: Scene = BattleShipFxApp.getHighscoreScene
    BattleShipFxApp.loadScene(scene,BattleShipFxApp.getRootStage)

  }

  /**
    * Change Scene to Game scene
    */
  @FXML def onShowCredits(): Unit = {
    val scene: Scene = BattleShipFxApp.getCreditsScene
    BattleShipFxApp.loadScene(scene,BattleShipFxApp.getRootStage)
  }


  @FXML def onSwitchSoundSetting(): Unit = {

    val jukeBox: BattleShipJukeBox =  BattleShipFxApp.getBattleShipJukeBox


    btSound.getStyleClass.clear
    //Is jukebos is currently mute, enable sound
    if (jukeBox.isMute) {
      //Enable sound
      btSound.getStyleClass.add("buttonSoundOn")
      jukeBox.setMute(false)
    } else {
      //Disable sound
      btSound.getStyleClass.add("buttonSoundOff")
      jukeBox.setMute(true)
    }
  }

}
