package at.fhj.swengb.apps.battleship.jfx

import java.util.{Optional, ResourceBundle}
import javafx.beans.property.{ReadOnlyBooleanProperty, ReadOnlyDoubleProperty}
import javafx.fxml.FXMLLoader
import javafx.scene.{Node, Parent, Scene}
import javafx.scene.control.Alert.AlertType
import javafx.scene.control._
import javafx.scene.image.Image
import javafx.scene.layout.{BorderPane, GridPane}
import javafx.stage.{Modality, Stage, StageStyle, Window}

import at.fhj.swengb.apps.battleship.jfx.BattleShipFxApp.{getClass, loadScene}
import at.fhj.swengb.apps.battleship.model.{BattleField, BattleShipGame, BattleShipGamePlayRound, Player}

import scala.util.{Failure, Success, Try}

class BattleShipFxDialogHandler {

  def askGameMode(): Optional[String] = {
    val dialog: ChoiceDialog[String] =
      new ChoiceDialog("Singleplayer", "Singleplayer", "Multiplayer")
    dialog.setTitle("Select Game setting")
    dialog.setHeaderText("Start a new Game:")
    dialog.setContentText("Select your enemy")

    dialog.showAndWait()
  }

  def askSinglePlayerName(): Optional[String] = {
    //Ask for username
    val dialog: TextInputDialog = new TextInputDialog
    dialog.setTitle("Enter names:")
    dialog.setContentText("Please enter your name:")

    dialog.showAndWait()
  }

  def askMultiplayerPlayerName(): Optional[(String,String)] = {
    val dialog = new Dialog[(String, String)]
    dialog.setTitle("Enter names:")
    dialog.setHeaderText("Please enter your names:")
    dialog.getDialogPane.getButtonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)

    // Create the username and password labels and fields.
    val grid: GridPane = new GridPane
    grid.setHgap(10)
    grid.setVgap(10)

    val tfUserA: TextField = new TextField()
    tfUserA.setPromptText("PlayerA")
    grid.add(tfUserA, 0, 0)

    val tfUserB: TextField = new TextField()
    tfUserB.setPromptText("PlayerB")
    grid.add(tfUserB, 0, 1)

    dialog.getDialogPane.setContent(grid)

    dialog.setResultConverter {
      case ButtonType.OK     => (tfUserA.getText, tfUserB.getText)
      case ButtonType.CANCEL => null
    }

    dialog.showAndWait()
  }

  def initMultiPlayer(playerNr: Int): (Player,BattleField) = {
    val fxml: String = "/at/fhj/swengb/apps/battleship/jfx/fxml/battleshipMutiplayerEditDialog.fxml"
    val fxmlLoader: FXMLLoader = new FXMLLoader()

    //Set play round to controller and controller for dialog
    val controller: BattleShipMutliplayerEditFxController= new BattleShipMutliplayerEditFxController()
    controller.initPlayerNr = playerNr

    fxmlLoader.setController(controller)
    var triedScene = Try(fxmlLoader.load[Parent](getClass.getResource(fxml).openStream()))

    triedScene match {
      case Success(root) =>
        val dialog = new Stage
        dialog.initStyle(StageStyle.UTILITY)

        dialog.setTitle("Initialize Multiplayer")
        val scene: Scene = new Scene(root)
        dialog.setScene(scene)
        dialog.setResizable(false)

        //Set CSS Style
        dialog.getScene.getStylesheets.clear()
        val css = "/at/fhj/swengb/apps/battleship/jfx/battleshipfx.css"
        dialog.getScene.getStylesheets.add(css)

        //Set Icon and Display stage...
        dialog.getIcons().add(new Image(getClass.getResource("/at/fhj/swengb/apps/battleship/jfx/pics/logo.jpg").toString))
        dialog.showAndWait()

        //return result
        controller.getResult match {
          case Some(result) => {
            if (controller.closedRegularly)
              result
            else
              (null,null)
          }
          case None => (null,null) //Abort - return empty
        }

      case Failure(e) => {
        e.printStackTrace()
        (null,null) //If we are here, some crazy shit was going on!
      }
    }
  }

  def askResetHighscoreDialog(): Boolean = {
    val alert = new Alert(AlertType.CONFIRMATION)
    alert.setTitle("Clear Highscore")
    alert.setHeaderText("Are you shure to delete this Highscore?")
    alert.setContentText("All Entries get deleted.?")

    val result = alert.showAndWait
    result.get() match {
      case ButtonType.OK => true
      case _ => false
    }
  }

  def showPlayerChangeDialog(newPlayer: Player): Optional[ButtonType] = {
    val alert = new Alert(AlertType.INFORMATION)
    alert.setTitle("Multiplayer-Mode")
    alert.setHeaderText("Next player: " + newPlayer.name)
    alert.showAndWait()
  }


  def showHigschoreGameDialog(playRound: BattleShipGamePlayRound) = {

    val fxml: String = "/at/fhj/swengb/apps/battleship/jfx/fxml/battleshipHighscoreGameReplayDialogfx.fxml"
    val fxmlLoader: FXMLLoader = new FXMLLoader()

    //Set play round to controller and controller for dialog
    val controller: BattleShipGameReplayFxController = new BattleShipGameReplayFxController()
    controller.selectedPlayRound = playRound

    fxmlLoader.setController(controller)
    var triedScene = Try(fxmlLoader.load[Parent](getClass.getResource(fxml).openStream()))

    triedScene match {
      case Success(root) =>
        val dialog = new Stage
        dialog.initStyle(StageStyle.UTILITY)

        dialog.setTitle(playRound.name)
        val scene: Scene = new Scene(root)

        dialog.setScene(scene)
        dialog.setResizable(false)

        //Set CSS Style
        dialog.getScene.getStylesheets.clear()
        val css = "/at/fhj/swengb/apps/battleship/jfx/battleshipfx.css"
        dialog.getScene.getStylesheets.add(css)

        //Set Icon and Display stage...
        dialog.getIcons().add(new Image(getClass.getResource("/at/fhj/swengb/apps/battleship/jfx/pics/logo.jpg").toString))
        dialog.show()
      case Failure(e) => e.printStackTrace()
    }
  }

  /**
    * Game is over - Show winner and store game for highscore!
    * @param gamePlayround - Current played round
    */
  def showGameOverDialog(gamePlayround: BattleShipGamePlayRound): Optional[ButtonType] = {
    //Deactivate gamePlayround field
    gamePlayround.currentBattleShipGame.getCells.foreach(e => e.setDisable(true))

    //Show dialog according
    gamePlayround.getWinner match {
      case None => Optional.empty() //When this happens, game ended without winner!
      case Some(winner) => {
        val alert = new Alert(AlertType.INFORMATION)
        alert.setTitle("G A M E - O V E R")
        alert.setHeaderText("Game over!")
        alert.setContentText("Player <" + gamePlayround.getWinnerName + "> has won!")
        alert.showAndWait()
      }
    }
  }


}
