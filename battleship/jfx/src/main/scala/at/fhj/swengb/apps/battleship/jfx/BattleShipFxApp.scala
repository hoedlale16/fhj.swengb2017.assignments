package at.fhj.swengb.apps.battleship.jfx

import javafx.application.Preloader.StateChangeNotification
import javafx.application.{Application, Preloader}
import javafx.fxml.FXMLLoader
import javafx.scene.control.ProgressBar
import javafx.scene.image.{Image, ImageView}
import javafx.scene.layout.BorderPane
import javafx.scene.media.Media
import javafx.scene.{Parent, Scene}
import javafx.stage.{Stage, StageStyle}

import at.fhj.swengb.apps.battleship.model.{BattleShipJukeBox, FleetConfig}
import com.sun.javafx.application.LauncherImpl

import scala.util.{Failure, Success, Try}

object BattleShipFxApp {
  //Becomes initialized when GUI starts
  private var rootStage: Stage = _
  private var jukeBox: BattleShipJukeBox = _
  private var usedFleetConfig: FleetConfig = FleetConfig.Standard

  private var welcomeScreen: Scene = _
  private var gameScreen: Scene = _
  private var highscoreScreen: Scene = _
  private var creditScreen: Scene = _

  /**
    * Returns the RootStage of Application. This Stage get initialized once the application starts
    *
    * @return root-Stage of application
    */
  def getRootStage: Stage = rootStage

  /**
    * Returns the initialized jukebox, which plays all soundfiles heard in the game.
    *
    * @return
    */
  def getBattleShipJukeBox: BattleShipJukeBox = jukeBox

  def getWelcomeScene: Scene = welcomeScreen

  def getGameScene: Scene = gameScreen

  def getHighscoreScene: Scene = parseScene("/at/fhj/swengb/apps/battleship/jfx/fxml/battleshipHighscorefx.fxml")

  def getCreditsScene: Scene = parseScene("/at/fhj/swengb/apps/battleship/jfx/fxml/battleshipCreditsfx.fxml")

  /**
    * Method to create a Scene from a given FXML and return said scene
    *
    * @param fxml - Path of the fxml to load
    */
  private def parseScene(fxml: String): Scene = {
    val triedScene = Try(FXMLLoader.load[Parent](getClass.getResource(fxml)))
    triedScene match {
      case Success(root) =>
        val scene: Scene = new Scene(root)
        //Set CSS Style
        scene.getStylesheets.clear()
        scene.getStylesheets.add("/at/fhj/swengb/apps/battleship/jfx/battleshipfx.css")

        //Return scene
        scene
      case Failure(e) =>
        e.printStackTrace()
        null //If this happens, we successfully killed our application…
    }
  }

  def getUsedFleetConfig: FleetConfig = usedFleetConfig

  def setUsedFleetConfig(newFleetConfig: FleetConfig): Unit = {
    usedFleetConfig = newFleetConfig
  }

  /**
    * Parses and initializes all the main scenes
    */
  def initializeScenes(): Unit = {
    welcomeScreen = parseScene("/at/fhj/swengb/apps/battleship/jfx/fxml/battleshipMainfx.fxml")
    gameScreen = parseScene("/at/fhj/swengb/apps/battleship/jfx/fxml/battleshipGamefx.fxml")
    //HighscoreScreen not possible to load once because we need the games played in this session in the highscore table as well
    //This is necessary because we do not save highscores in memory during the game. We keep them in a file on the user's hard disk and read it when it is required
    //highscoreScreen = parseScene("/at/fhj/swengb/apps/battleship/jfx/fxml/battleshipHighscorefx.fxml")
    //creditScreen is not possible to load here because WebView requires a javaFx-Thread and not a javaImpl-Thread (butchered implementation by Oracle, again)
  }

  def showScene(scene: Scene, stage: Stage): Unit = {
    if (stage == null) {
      println("Window currently not set – ABORTING!")
      System.exit(1) //If we end up here, either the developer was stupid and wanted to parseScene a scene without a window-frame … or some crazy shit was going on ;-)
    } else {
      //Set scene and show() it
      stage.setScene(scene)
      stage.show()
    }
  }


  def main(args: Array[String]): Unit = {
    LauncherImpl.launchApplication(classOf[BattleShipFxApp], classOf[BattleShipFxSlashScreen], args)
  }


}

// Application starts here. rootStage (represents a frame/window) gets initialized and main Scene (Content of FXML) is loaded
class BattleShipFxApp extends Application {

  override def start(stage: Stage): Unit = {

    //Set fundamental environment settings
    stage.setTitle("BattleshipGame by SC!G0 (SWENGB-IMA16)")
    stage.setResizable(false)
    stage.getIcons.add(new Image(getClass.getResource("/at/fhj/swengb/apps/battleship/jfx/pics/logo.jpg").toString))


    //Assign main root stage to allow setting of different scenes in this frame
    BattleShipFxApp.rootStage = stage

    //Show main scene
    BattleShipFxApp.showScene(BattleShipFxApp.getWelcomeScene, stage)

    //Play some smooth background music
    BattleShipFxApp.jukeBox.playBackgroundMusic()
  }

  override def init(): Unit = {

    //First load soundfiles and initialize jukebox.
    //This is necessary because mainScene requires an initialized jukebox to set the correct layout for the mute-button
    val backgroundMedia: Media = new Media(getClass.getResource("/at/fhj/swengb/apps/battleship/jfx/music/MainMusic.mp3").toExternalForm)
    val shipHitMedia: Media = new Media(getClass.getResource("/at/fhj/swengb/apps/battleship/jfx/music/ShipHit.mp3").toExternalForm)
    val waterHitMedia: Media = new Media(getClass.getResource("/at/fhj/swengb/apps/battleship/jfx/music/WaterHit.mp3").toExternalForm)

    BattleShipFxApp.jukeBox = BattleShipJukeBox(backgroundMedia, shipHitMedia, waterHitMedia)

    //Finally, load the FXML for main welcome screen GUI
    BattleShipFxApp.initializeScenes()
  }
}


class BattleShipFxSlashScreen extends Preloader {

  //External help from: https://docs.oracle.com/javafx/2/deployment/preloaders.htm#BABFABDG

  private var stage: Stage = _
  private var progressBar: ProgressBar = new ProgressBar

  override def start(primaryStage: Stage): Unit = {
    stage = primaryStage

    stage.initStyle(StageStyle.UNDECORATED)
    stage.getIcons.add(new Image(getClass.getResource("/at/fhj/swengb/apps/battleship/jfx/pics/logo.jpg").toString))

    stage.setWidth(600)
    stage.setHeight(240)

    stage.setScene(createScene)
    stage.show()
  }

  private def createScene: Scene = {
    val mainPane: BorderPane = new BorderPane()

    val image: ImageView = new ImageView(getClass.getResource("/at/fhj/swengb/apps/battleship/jfx/pics/SplashScreen.jpg").toString)
    image.setFitWidth(600)
    image.setFitHeight(240)

    progressBar.setPrefWidth(600)

    mainPane.setCenter(image)
    mainPane.setBottom(progressBar)

    new Scene(mainPane)
  }

  /**
    * Function called when progress has changed... Updates GUI
    *
    * @param pn Notification from preloader
    */
  override def handleProgressNotification(pn: Preloader.ProgressNotification): Unit = {
    //Show Splash-screen for a bit
    progressBar.setProgress(pn.getProgress)
  }

  /**
    * Function called, when stage has changed
    *
    * @param evt stage changed
    */
  override def handleStateChangeNotification(evt: Preloader.StateChangeNotification): Unit = {
    //Right before the application starts, hide the splash-screen!
    if (evt.getType.equals(StateChangeNotification.Type.BEFORE_START)) {
      stage.hide()
    }
  }
}