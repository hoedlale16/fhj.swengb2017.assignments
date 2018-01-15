package at.fhj.swengb.apps.battleship.jfx

import java.util.Optional
import javafx.fxml.FXMLLoader
import javafx.scene.{Parent, Scene}
import javafx.scene.control.Alert.AlertType
import javafx.scene.control._
import javafx.scene.image.Image
import javafx.scene.layout.GridPane
import javafx.stage.{Stage, StageStyle}

import at.fhj.swengb.apps.battleship.model._

import scala.util.{Failure, Success, Try}

case class BattleShipFxDialogHandler() {

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

  /**
    * Dialog shown when player in edit-mode place a vessel on a illegal positon
    * @param vessel - Vessel which is placed on a illegal position
    */
  def showIllegalShipPlacementDialog(vessel: Vessel, illegalPos: Set[BattlePos]): Optional[ButtonType] = {
    val alert = new Alert(AlertType.ERROR)
    alert.setTitle("CAPTAIN, YOU'RE DRUNK!")
    alert.setHeaderText("Illegal Position for ship!")

    def context: String = {
      val builder = new StringBuilder("Ship <").append(vessel.name.value).append("> set on an illegal positions:\n")

      //Append illegal Positions to Dialog
      builder.append("  ")
      val posString:String =  illegalPos.foldLeft("")((acc,e) => "("+e.x+"/"+e.y+") " + acc )
      builder.append(posString.trim).append("\n")

      builder.append("\nEither ship collidates with another or it is not in the game ground!")
      builder.toString()
    }

    alert.setContentText(context)
    alert.showAndWait()
  }



  def showApplicationSettingDialog(): Optional[(Boolean,Boolean,FleetConfig)] = {

    val jukeBox = BattleShipFxApp.getBattleShipJukeBox
    val dialog = new Dialog[(Boolean,Boolean,FleetConfig)]

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



    grid.add(new Label("Play Backgroundmusic:"),0,0)
    grid.add(backgroundCheckBox, 1, 0)

    grid.add(new Label("Play SoundEffects:"),0,1)
    grid.add(effectCheckBox, 1, 1)

    grid.add(new Label("Battle fleet size:"),0,2)
    grid.add(fleetListView,1,2)

    dialog.getDialogPane.setContent(grid)

    dialog.setResultConverter {
      case ButtonType.OK     => {

        val fleetConfig: FleetConfig = fleetListView.getSelectionModel.getSelectedItem match {
          case "Standard" => FleetConfig.Standard
          case "TwoShips" => FleetConfig.TwoShips
          case "OneShip"  => FleetConfig.OneShip
        }

        (backgroundCheckBox.isSelected,effectCheckBox.isSelected,fleetConfig)
      }
      case ButtonType.CANCEL => null
    }

    dialog.showAndWait()
  }

}
