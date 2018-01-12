package at.fhj.swengb.apps.battleship.jfx

import java.net.URL
import java.util.ResourceBundle
import javafx.fxml.{FXML, Initializable}
import javafx.scene.Scene
import javafx.scene.web.WebView

class BattleShipCreditsFxController extends Initializable {

  @FXML private var webView: WebView = _

  private val bgcolor: String = "<body bgcolor=\"#61380B\">"

  private val text: String =  "<h1>Project Description</h1>" +
                              "This Battleship Game is part of a students' project.\n" +
                              "It was created for the subject \"Software Engineering Basics (SWENGB)\"\n." +
                              "This subject is part of the bachelor program Information Management held by the UAS JOANNEUM in the third semester."

  private val bg: String = "<img src=\"/at/fhj/swengb/apps/battleship/jfx/pics/IMA-logo.png\">"

  private val creators: String = "<h1>Project Team</h1>" +
                                  "<br>The project Team which is responsible for the creation of this application consists of the following persons:" +
                                  "<ul>" +
                                    "<li>FERNBACH Gregor</li>" +
                                    "<li>HOEDL Alexander</li>" +
                                    "<li>QERIMI Ardian</li>" +
                                    "<li>WEILAND Sebastian</li>" +
                                  "</ul>" +
                                  "Moreover this project was guided and supported by lecturer Dipl.-Ing. Robert Ladstätter. <br>"
  private val roles: String = "<h1>Project Roles</h1>" +
                              "The roles of the project team were as follows:" +
                              "<ul>" +
                                  "<li>Head of documentation & project management: Fernbach Gregor</li>" +
                                  "<li>Head of development: Hoedl Alexander</li>" +
                                  "<li>Head of Design and Layout: Qerimi Ardian</li>" +
                                  "<li>Head of Testing and quality management: Weiland Sebastian</li>" +
                              "</ul>"
  private val sources: String = "<h1>Resources</h1>" +
                                "Pictures included in this application were extracted from the following websites:<br>" +
                                "<ul>" +
                                  "<li>https://pixabay.com/p-1283475/?no_redirect</li>" +
                                  "<li>http://wallpaperpulse.com/wallpaper/4028691</li>" +
                                  "<li>https://www.flickr.com/photos/verfain/6388934159/</li>" +
                                  "<li>https://www.iconfinder.com/icons/42343/mute_sound_icon#size=32</li>" +
                                  "<li>https://www.iconfinder.com/icons/42341/audio_editing_sound_speaker_volume_icon#size=32</li>" +
                                "</ul>" +
                                "Music & Sound effects included in this application were extracted from the following websites: <br>"+
                                "<ul>" +
                                  "<li>http://freemusicarchive.org/music/Dee_Yan-Key/Killing_Mozart/06--Dee_Yan-Key-Italian_Nightmare</li>" +
                                  "<li> http://soundbible.com/1986-Bomb-Exploding.html</li>" +
                                  "<li>http://soundbible.com/1463-Water-Balloon.html</li>" +
                                "</ul>"


  override def initialize(location: URL, resources: ResourceBundle) = {
    val builder: StringBuilder = new StringBuilder
    builder.append(bgcolor).append(text).append(bg).append(creators).append(roles).append(sources)

    webView.getEngine.loadContent(builder.toString())

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
