package at.fhj.swengb.apps.battleship.jfx

import java.net.URL
import java.util.ResourceBundle
import javafx.fxml.{FXML, Initializable}
import javafx.scene.Scene
import javafx.scene.web.WebView

import at.fhj.swengb.apps.battleship.model.BattleShipGameCredits

class BattleShipCreditsFxController extends Initializable {

  @FXML private var webView: WebView = _


  override def initialize(location: URL, resources: ResourceBundle) = {
    //Load credits and show it
    val credits: String = BattleShipGameCredits().getCreditText
    webView.getEngine.loadContent(credits)
  }

  /**
    * Go back to main Scene
    */
  @FXML def returnToMain(): Unit = {
    //Abort and switch back to main
    val scene: Scene = BattleShipFxApp.getWelcomeScene
    BattleShipFxApp.showScene(scene,BattleShipFxApp.getRootStage)
  }

}
