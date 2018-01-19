package at.fhj.swengb.apps.battleship.jfx

import java.net.URL
import java.util.ResourceBundle
import javafx.fxml.{FXML, Initializable}
import javafx.scene.Scene
import javafx.scene.web.WebView

import at.fhj.swengb.apps.battleship.model.BattleShipGameCredits

class BattleShipCreditsFxController extends Initializable {

  @FXML private var webView: WebView = _

  override def initialize(location: URL, resources: ResourceBundle): Unit = {
    //Load credits and display them
    val credits: String = BattleShipGameCredits().getCreditText()
    webView.getEngine.loadContent(credits)
  }

  /**
    * Go back to main menu
    */
  @FXML def returnToMain(): Unit = {
    //Abort and switch back to main menu
    val scene: Scene = BattleShipFxApp.getWelcomeScene
    BattleShipFxApp.showScene(scene, BattleShipFxApp.getRootStage)
  }

}
