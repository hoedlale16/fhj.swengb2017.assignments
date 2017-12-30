package at.fhj.swengb.apps.battleship.jfx

import java.util.Optional
import javafx.scene.control.Alert.AlertType
import javafx.scene.control._
import javafx.scene.layout.GridPane

import at.fhj.swengb.apps.battleship.model.{BattleShipGame, BattleShipGamePlayRound, Player}

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

  def showPlayerChangeDialog(newPlayer: Player): Optional[ButtonType] = {
    val alert = new Alert(AlertType.INFORMATION)
    alert.setTitle("Multiplayer-Mode")
    alert.setHeaderText("Next player: " + newPlayer.name)
    alert.showAndWait()
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
        alert.setContentText("Player <" + gamePlayround.getWinner + "> has won!")
        alert.showAndWait()
      }
    }
  }

}
