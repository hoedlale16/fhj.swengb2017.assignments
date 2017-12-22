package at.fhj.swengb.apps.battleship.jfx

import java.net.URL
import java.util.ResourceBundle
import javafx.fxml.{FXML, FXMLLoader, Initializable}
import javafx.scene.Parent
import javafx.stage.{Modality, Stage}

class BattleShipHighscoreFxController extends Initializable {



  override def initialize(location: URL, resources: ResourceBundle) = {
  }

  /**
    * Go back to main Scene
    */
  @FXML def returnToMain(): Unit = {
    //Abort and switch back to main
    BattleShipFxApp.loadMainScene
  }

}
