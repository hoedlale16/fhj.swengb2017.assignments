package at.fhj.swengb.apps.battleship.jfx

import java.util.Optional
import javafx.fxml.FXMLLoader
import javafx.scene.control.Alert.AlertType
import javafx.scene.control._
import javafx.scene.image.Image
import javafx.scene.layout.GridPane
import javafx.scene.{Parent, Scene}
import javafx.stage.{Stage, StageStyle}

import at.fhj.swengb.apps.battleship.model._

import scala.util.{Failure, Success, Try}

case class BattleShipFxDialogHandler() {

  def askGameMode(): Optional[String] = {
    val dialog: ChoiceDialog[String] =
      new ChoiceDialog("Singleplayer", "Singleplayer", "Multiplayer")
    dialog.setTitle("Select Game Mode")
    dialog.setHeaderText("Start a new Game:")
    dialog.setContentText("Choose your enemy")

    dialog.showAndWait()
  }

  def askSinglePlayerName(): Optional[String] = {
    //Ask for the username
    val dialog: TextInputDialog = new TextInputDialog
    dialog.setTitle("Enter your name")
    dialog.setHeaderText("Captain, please enter your name!")
    dialog.setContentText("Please enter your name:")

    dialog.showAndWait()
  }

  def initMultiPlayer(playerNr: Int): (Player, BattleField) = {
    val fxml: String = "/at/fhj/swengb/apps/battleship/jfx/fxml/battleshipMutiplayerEditDialog.fxml"
    val fxmlLoader: FXMLLoader = new FXMLLoader()

    //Set playround to controller and controller for dialog
    val controller: BattleShipMutliplayerEditFxController = new BattleShipMutliplayerEditFxController()
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

        //Set Icon and Display stage…
        dialog.getIcons.add(new Image(getClass.getResource("/at/fhj/swengb/apps/battleship/jfx/pics/logo.jpg").toString))
        dialog.showAndWait()

        //return result
        controller.getResult match {
          case Some(result) =>
            if (controller.closedRegularly)
              result
            else
              (null, null)
          case None => (null, null) //Abort - return empty
        }

      case Failure(e) =>
        e.printStackTrace()
        (null, null) //If we end up here, some crazy shit was going on!
    }
  }

  def askResetHighscoreDialog(): Boolean = {
    val alert = new Alert(AlertType.CONFIRMATION)
    alert.setTitle("Clear Highscores")
    alert.setHeaderText("Are you sure you want to delete all Highscores?")
    alert.setContentText("All Entries will be deleted!")

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


  def showHigschoreGameDialog(playRound: BattleShipGamePlayRound): Unit = {

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
        dialog.getIcons.add(new Image(getClass.getResource("/at/fhj/swengb/apps/battleship/jfx/pics/logo.jpg").toString))
        dialog.show()
      case Failure(e) => e.printStackTrace()
    }
  }

  /**
    * Game is over - Show winner and store game for highscore!
    *
    * @param gamePlayround - Current played round
    */
  def showGameOverDialog(gamePlayround: BattleShipGamePlayRound): Optional[ButtonType] = {
    //Deactivate gamePlayround field
    gamePlayround.currentBattleShipGame.getCells.foreach(e => e.setDisable(true))

    //Show dialog accordingly
    gamePlayround.getWinner match {
      case None => Optional.empty() //When this happens, game ended without winner!
      case Some(winner) =>
        val alert = new Alert(AlertType.INFORMATION)
        alert.setTitle("GAME OVER!")
        alert.setHeaderText("Game over!")
        alert.setContentText("Player " + gamePlayround.getWinnerName + " won! Congratulations!")
        alert.showAndWait()
    }
  }

  /**
    * Dialog shown when player in edit-mode places a vessel on an illegal position
    *
    * @param vessel - Vessel which is placed on an illegal position
    */
  def showIllegalShipPlacementDialog(vessel: Vessel, illegalPos: Set[BattlePos]): Optional[ButtonType] = {
    val alert = new Alert(AlertType.ERROR)
    alert.setTitle("CAPTAIN, ARE YOU DRUNK)")
    alert.setHeaderText("Illegal position for this ship!")

    def context: String = {
      val builder = new StringBuilder("Ship ").append(vessel.name.value).append(" was almost set on an illegal position:\n")

      //Append illegal Positions to Dialog
      builder.append("  ")
      val posString: String = illegalPos.foldLeft("")((acc, e) => "(" + e.x + "/" + e.y + ") " + acc)
      builder.append(posString.trim).append("\n")

      builder.append("\nThis ship either collides with another ship, or it is not inside the game ground!")
      builder.toString()
    }

    alert.setContentText(context)
    alert.showAndWait()
  }


  def showApplicationSettingDialog(): Optional[(Boolean, Boolean, FleetConfig)] = {

    val jukeBox = BattleShipFxApp.getBattleShipJukeBox
    val dialog = new Dialog[(Boolean, Boolean, FleetConfig)]

    dialog.setTitle("Sound Settings:")
    dialog.getDialogPane.getButtonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)

    // Create labels for settings Dialog
    val grid: GridPane = new GridPane
    grid.setHgap(10)
    grid.setVgap(10)

    val backgroundCheckBox: CheckBox = new CheckBox()
    backgroundCheckBox.setSelected(!jukeBox.isBackGroundMusicMute)

    val effectCheckBox: CheckBox = new CheckBox()
    effectCheckBox.setSelected(!jukeBox.isSoundEffectsMute)

    val fleetListView: ListView[String] = new ListView[String]()
    fleetListView.getItems.add("Standard")
    fleetListView.getItems.add("TwoShips")
    fleetListView.getItems.add("OneShip")
    fleetListView.setPrefHeight(75)

    fleetListView.getSelectionModel.setSelectionMode(SelectionMode.SINGLE)
    fleetListView.getSelectionModel.selectFirst()


    grid.add(new Label("Play Background-Music:"), 0, 0)
    grid.add(backgroundCheckBox, 1, 0)

    grid.add(new Label("Play Sound_Effects:"), 0, 1)
    grid.add(effectCheckBox, 1, 1)

    grid.add(new Label("Battle fleet size:"), 0, 2)
    grid.add(fleetListView, 1, 2)

    dialog.getDialogPane.setContent(grid)

    dialog.setResultConverter {
      case ButtonType.OK => {

        val fleetConfig: FleetConfig = fleetListView.getSelectionModel.getSelectedItem match {
          case "Standard" => FleetConfig.Standard
          case "TwoShips" => FleetConfig.TwoShips
          case "OneShip" => FleetConfig.OneShip
        }

        (backgroundCheckBox.isSelected, effectCheckBox.isSelected, fleetConfig)
      }
      case ButtonType.CANCEL => null
    }

    dialog.showAndWait()
  }

}
