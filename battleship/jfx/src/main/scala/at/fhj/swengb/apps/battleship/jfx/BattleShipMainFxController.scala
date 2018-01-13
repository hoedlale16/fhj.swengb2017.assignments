package at.fhj.swengb.apps.battleship.jfx

import java.net.URL
import java.util.{Optional, ResourceBundle}
import javafx.fxml.{FXML, Initializable}
import javafx.scene.Scene
import javafx.scene.control.Button

import at.fhj.swengb.apps.battleship.model.BattleShipJukeBox

class BattleShipMainFxController extends Initializable {

  @FXML var btSound: Button = _


  override def initialize(location: URL, resources: ResourceBundle) = {
    setSoundButtonLayout
  }

  /**
    * Change Scene to Game scene
    */
  @FXML def onStartGame(): Unit = {
    val scene: Scene = BattleShipFxApp.getGameScene
    BattleShipFxApp.showScene(scene,BattleShipFxApp.getRootStage)
  }

  /**
    * Change Scene to Game scene
    */
  @FXML def onShowHighscore(): Unit = {
    val scene: Scene = BattleShipFxApp.getHighscoreScene
    BattleShipFxApp.showScene(scene,BattleShipFxApp.getRootStage)

  }

  /**
    * Change Scene to Game scene
    */
  @FXML def onShowCredits(): Unit = {
    val scene: Scene = BattleShipFxApp.getCreditsScene
    BattleShipFxApp.showScene(scene,BattleShipFxApp.getRootStage)
  }


  @FXML def onSwitchSoundSetting(): Unit = {
    val jukeBox: BattleShipJukeBox = BattleShipFxApp.getBattleShipJukeBox
    //Set opponent value of current mute state on button click
    jukeBox.setTotalMute(!jukeBox.isTotalMute)
    setSoundButtonLayout
  }

  private def setSoundButtonLayout(): Unit = {
    //Set corret style accoring new state
    btSound.getStyleClass.clear
    BattleShipFxApp.getBattleShipJukeBox.isTotalMute match {
      case true => btSound.getStyleClass.add("buttonSoundOff")
      case false => btSound.getStyleClass.add("buttonSoundOn")
    }
  }


  @FXML def onChangeApplicationSettings(): Unit = {
    val result: Optional[(Boolean,Boolean)] = BattleShipFxDialogHandler().showApplicationSettingDialog()
    if(result.isPresent) {
      result.get() match {
        case (background, effects) => {
          BattleShipFxApp.getBattleShipJukeBox.playMusic(background, effects)
          //Refresh in the case that user disabled both
          setSoundButtonLayout()
        }
      }
    }
  }

}
