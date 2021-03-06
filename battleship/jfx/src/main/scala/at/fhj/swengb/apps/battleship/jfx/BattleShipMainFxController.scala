package at.fhj.swengb.apps.battleship.jfx

import java.net.URL
import java.util.{Optional, ResourceBundle}
import javafx.fxml.{FXML, Initializable}
import javafx.scene.Scene
import javafx.scene.control.Button

import at.fhj.swengb.apps.battleship.model.{BattleShipJukeBox, FleetConfig}

class BattleShipMainFxController extends Initializable {

  @FXML var btSound: Button = _

  override def initialize(location: URL, resources: ResourceBundle): Unit = {
    setSoundButtonLayout()
  }

  /**
    * Change Scene to Game scene
    */
  @FXML def onStartGame(): Unit = {
    val scene: Scene = BattleShipFxApp.getGameScene
    BattleShipFxApp.showScene(scene, BattleShipFxApp.getRootStage)
  }

  /**
    * Change Scene to Game scene
    */
  @FXML def onShowHighscore(): Unit = {
    val scene: Scene = BattleShipFxApp.getHighscoreScene
    BattleShipFxApp.showScene(scene, BattleShipFxApp.getRootStage)

  }

  /**
    * Change Scene to Game scene
    */
  @FXML def onShowCredits(): Unit = {
    val scene: Scene = BattleShipFxApp.getCreditsScene
    BattleShipFxApp.showScene(scene, BattleShipFxApp.getRootStage)
  }


  @FXML def onSwitchSoundSetting(): Unit = {
    val jukeBox: BattleShipJukeBox = BattleShipFxApp.getBattleShipJukeBox
    //Set opponent value of current mute state on button click
    jukeBox.setTotalMute(!jukeBox.isTotalMute)
    setSoundButtonLayout()
  }

  private def setSoundButtonLayout(): Unit = {
    //Set correct style according to the new state
    btSound.getStyleClass.clear()
    if (BattleShipFxApp.getBattleShipJukeBox.isTotalMute) {
      btSound.getStyleClass.add("buttonSoundOff")
    } else {
      btSound.getStyleClass.add("buttonSoundOn")
    }
  }

  @FXML def onChangeApplicationSettings(): Unit = {
    val result: Optional[(Boolean, Boolean, FleetConfig)] = BattleShipFxDialogHandler().showApplicationSettingDialog()
    if (result.isPresent) {
      result.get() match {
        case (music, soundEffects, fleetConfig) =>
          BattleShipFxApp.getBattleShipJukeBox.playMusic(music, soundEffects)
          //Refresh in case the user disabled both
          setSoundButtonLayout()

          //Set FleetConfig
          BattleShipFxApp.setUsedFleetConfig(fleetConfig)
      }
    }
  }

}
