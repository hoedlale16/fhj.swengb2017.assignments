package at.fhj.swengb.apps.battleship.jfx

import java.net.URL
import java.util.ResourceBundle
import javafx.fxml.{FXML, Initializable}
import javafx.scene.Scene
import javafx.scene.web.WebView

class BattleShipCreditsFxController extends Initializable {

  @FXML private var webView: WebView = _

  private val text: String = "This application was procuded during university course SWENGB of INOFRMATIONMANAGEMENT [IMA16] at FH JOANNEUM.<br>"

  private val creators: String ="The application was produced by following students and supported by lecturer Dipl.-Ing. Robert Ladstätter." +
                                "<ul>" +
                                  "<li>FERNBARCH Gregor</li>" +
                                  "<li>HOEDL Alexander</li>" +
                                  "<li>QERIMI Ardian</li>" +
                                  "<li>WEILAND Sebastian</li>" +
                                "</ul>"
  private val sources: String = "All pictures come from following websites:<br>" +
                                "<ul>" +
                                  "<li>https://pixabay.com/p-1283475/?no_redirect</li>" +
                                  "<li>http://wallpaperpulse.com/wallpaper/4028691</li>" +
                                  "<li>https://www.flickr.com/photos/verfain/6388934159/</li>" +
                                  "<li>https://www.iconfinder.com/icons/42343/mute_sound_icon#size=32</li>" +
                                  "<li>https://www.iconfinder.com/icons/42341/audio_editing_sound_speaker_volume_icon#size=32</li>" +
                                "</ul>" +
                                "All music comes from following websites: <br>"+
                                "<ul>" +
                                  "<li>http://freemusicarchive.org/music/Dee_Yan-Key/Killing_Mozart/06--Dee_Yan-Key-Italian_Nightmare</li>" +
                                  "<li> http://soundbible.com/1986-Bomb-Exploding.html</li>" +
                                  "<li>http://soundbible.com/1463-Water-Balloon.html</li>" +
                                "</ul>"


  override def initialize(location: URL, resources: ResourceBundle) = {
    val builder: StringBuilder = new StringBuilder
    builder.append(text).append(creators).append(sources)

    webView.getEngine.loadContent(builder.toString())

  }

  /**
    * Go back to main Scene
    */
  @FXML def returnToMain(): Unit = {
    //Abort and switch back to main
    val scene: Scene = BattleShipFxApp.getMainScene
    BattleShipFxApp.loadScene(scene,BattleShipFxApp.getRootStage)
  }

}
