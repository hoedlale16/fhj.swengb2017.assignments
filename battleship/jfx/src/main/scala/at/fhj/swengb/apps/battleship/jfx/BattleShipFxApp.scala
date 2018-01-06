package at.fhj.swengb.apps.battleship.jfx

import javafx.application.Preloader.StateChangeNotification
import javafx.application.{Application, Preloader}
import javafx.fxml.{FXML, FXMLLoader}
import javafx.scene.control.ProgressBar
import javafx.scene.image.{Image, ImageView}
import javafx.scene.layout.BorderPane
import javafx.scene.{Parent, Scene}
import javafx.stage.{Stage, StageStyle}

import com.sun.javafx.application.LauncherImpl

import scala.util.{Failure, Success, Try}

object BattleShipFxApp {
  //Becomes initialized when GUI starts
  private var rootStage: Stage = _; //If this stays null, some crazy shit is going on...

  def main(args: Array[String]): Unit = {
    LauncherImpl.launchApplication(classOf[BattleShipFxApp], classOf[BattleShipFxSlashScreen], args)
  }

  def loadMainScene: Unit = loadScene("/at/fhj/swengb/apps/battleship/jfx/fxml/battleshipMainfx.fxml")
  def loadGameScene: Unit = loadScene("/at/fhj/swengb/apps/battleship/jfx/fxml/battleshipGamefx.fxml")
  def loadHighscoreScene: Unit = loadScene("/at/fhj/swengb/apps/battleship/jfx/fxml/battleshipHighscorefx.fxml")
  def loadCreditsScene: Unit = loadScene("/at/fhj/swengb/apps/battleship/jfx/fxml/battleshipCreditsfx.fxml")


  /**
    * Internal method to loadScene scene from given fxml
    * Scene
    *
    * @param fxml - Path to fxml to load
    */
  private def loadScene(fxml: String): Unit = {
    val stage: Stage = BattleShipFxApp.rootStage

    if (stage == null) {
      println("Window currently not set - Abort!")
      System.exit(1) //If we're here either the developer is stupid and want to
      // loadScene a scene without window-frame or
      // some crazy shit was going on
    } else {
      val triedScene = Try(FXMLLoader.load[Parent](getClass.getResource(fxml)))
      triedScene match {
        case Success(root) =>
          stage.setTitle("BattleshipGame by SC1G0 (SWENGB-IMA16)")
          stage.setScene(new Scene(root))
          stage.setResizable(false)

          //Set CSS Style
          stage.getScene.getStylesheets.clear()
          val css = "/at/fhj/swengb/apps/battleship/jfx/battleshipfx.css"
          stage.getScene.getStylesheets.add(css)

          //Set Icon and Display stage...
          stage.getIcons.add(new Image(getClass.getResource("/at/fhj/swengb/apps/battleship/jfx/pics/logo.jpg").toString))
          stage.show()
        case Failure(e) => e.printStackTrace()
      }
    }
  }
}

/*
Here starts the application. rootStage (represents frame/window) get initializied and main Scene(Content from FXML)
get loaded
 */
class BattleShipFxApp extends Application {
    override def start(stage: Stage): Unit = {
    //Assign main root stage to allow to set differenct sceens in this frame
    BattleShipFxApp.rootStage = stage

    //Load MAIN scene for start
      BattleShipFxApp.loadMainScene

    //TODO: Play some background music
  }

  override def init(): Unit = {
    //TODO: Wait a bit to show SplashScreen/Preloader
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