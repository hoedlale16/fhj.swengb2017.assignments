package at.fhj.swengb.apps.battleship.jfx

import javafx.application.Preloader.StateChangeNotification
import javafx.application.{Application, Preloader}
import javafx.fxml.{FXML, FXMLLoader}
import javafx.scene.control.ProgressBar
import javafx.scene.image.{Image, ImageView}
import javafx.scene.layout.BorderPane
import javafx.scene.{Parent, Scene}
import javafx.stage.{Stage, StageStyle}

import at.fhj.swengb.apps.battleship.jfx.BattleShipFxApp.getClass
import com.sun.javafx.application.LauncherImpl

import scala.util.{Failure, Success, Try}

object BattleShipFxApp {
  //Becomes initialized when GUI starts
  private var rootStage: Stage = _; //If this stays null, some crazy shit is going on...

  /**
    * Returns the RootStage of Application. This Stage get initialized once whenn the application starts
    * @return root-Stage of application
    */
  def getRootStage(): Stage = rootStage

  def main(args: Array[String]): Unit = {
    LauncherImpl.launchApplication(classOf[BattleShipFxApp], classOf[BattleShipFxSlashScreen], args)
  }

  def getMainScene: Scene = getScene("/at/fhj/swengb/apps/battleship/jfx/fxml/battleshipMainfx.fxml")
  def getGameScene: Scene = getScene("/at/fhj/swengb/apps/battleship/jfx/fxml/battleshipGamefx.fxml")
  def getHighscoreScene: Scene = getScene("/at/fhj/swengb/apps/battleship/jfx/fxml/battleshipHighscorefx.fxml")
  def getCreditsScene: Scene = getScene("/at/fhj/swengb/apps/battleship/jfx/fxml/battleshipCreditsfx.fxml")


  /**
    * Method to create a Scene from given FXML and returns is
    * Scene
    *
    * @param fxml - Path to fxml to load
    */
  def getScene(fxml: String): Scene = {
    val triedScene = Try(FXMLLoader.load[Parent](getClass.getResource(fxml)))
    triedScene match {
      case Success(root) =>
        val scene: Scene = new Scene(root)
        //Set CSS Style
        scene.getStylesheets.clear()
        scene.getStylesheets.add("/at/fhj/swengb/apps/battleship/jfx/battleshipfx.css")

        //Return scene
        scene
      case Failure(e) => {
        e.printStackTrace()
        null //If this happens, we are already dead...
      }
    }
  }

  def loadScene(scene: Scene, stage: Stage): Unit = {
    if (stage == null) {
      println("Window currently not set - Abort!")
      System.exit(1) //If we're here either the developer is stupid and want to
      // getScene a scene without window-frame or
      // some crazy shit was going on
    } else {
      //Set scene and show
      stage.setScene(scene)
      stage.show()
    }
  }
}

/*
Here starts the application. rootStage (represents frame/window) get initializied and main Scene(Content from FXML)
get loaded
 */
class BattleShipFxApp extends Application {

  private var mainScene: Scene = _

    override def start(stage: Stage): Unit = {

      //Set fundamental environment settings
      stage.setTitle("BattleshipGame by SC1G0 (SWENGB-IMA16)")
      stage.setResizable(false)
      stage.getIcons.add(new Image(getClass.getResource("/at/fhj/swengb/apps/battleship/jfx/pics/logo.jpg").toString))


      //Assign main root stage to allow to set differenct sceens in this frame
      BattleShipFxApp.rootStage = stage

      //Show main scene
      BattleShipFxApp.loadScene(mainScene,stage)

    //TODO: Play some background music
  }

  override def init(): Unit = {
    //Load FXML for main GUI
    mainScene = BattleShipFxApp.getMainScene

    //TODO: Load music which starts when application starts
  }
}


class BattleShipFxSlashScreen extends Preloader{

  //Help from: https://docs.oracle.com/javafx/2/deployment/preloaders.htm#BABFABDG

  private var stage: Stage = _
  private var progressBar: ProgressBar = new ProgressBar

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

  override def start(primaryStage: Stage) = {
    stage = primaryStage

    stage.initStyle(StageStyle.UNDECORATED)
    stage.getIcons.add(new Image(getClass.getResource("/at/fhj/swengb/apps/battleship/jfx/pics/logo.jpg").toString))

    stage.setWidth(600)
    stage.setHeight(240)

    stage.setScene(createScene)
    stage.show()
  }

  /**
    * Function called when progress get changed... Updage GUI
    * @param pn Notification from preloader
    */
  override def handleProgressNotification(pn: Preloader.ProgressNotification): Unit = {
    //Show Splash-screen a bit
    progressBar.setProgress(pn.getProgress)
  }

  /**
    * Function called, wenn stage changed
    * @param evt stage changed
    */
  override def handleStateChangeNotification(evt: Preloader.StateChangeNotification): Unit = {
    //Just before application starts, hide splash-screen!
    if (evt.getType.equals(StateChangeNotification.Type.BEFORE_START)) {
      stage.hide()
    }
  }
}