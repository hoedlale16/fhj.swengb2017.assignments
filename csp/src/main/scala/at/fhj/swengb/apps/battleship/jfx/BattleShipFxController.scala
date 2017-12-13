package at.fhj.swengb.apps.battleship.jfx

import java.net.URL
import java.util.ResourceBundle
import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.TextArea
import javafx.scene.layout.GridPane
import javax.swing.JFileChooser

import at.fhj.swengb.apps.battleship.{BattleField, Fleet, FleetConfig}
import at.fhj.swengb.protobuf.ProtobufBattlefield


class BattleShipFxController extends Initializable {

  var game: BattleShipGame = null;

  @FXML private var battleGroundGridPane: GridPane = _

  /**
    * A text area box to place the history of the game
    */
  @FXML private var log: TextArea = _

  @FXML
  def newGame(): Unit = initGame()

  @FXML def saveGame(): Unit = {
    var chooser = new JFileChooser();
    chooser.setDialogTitle("Select path to store")
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY)
    chooser.setAcceptAllFileFilterUsed(false)

    if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
      val filePath = chooser.getSelectedFile.getAbsolutePath + "\\battleship.bin"

      ProtobufBattlefield.saveBattleShipGame(game,filePath)
      appendLog("Saved Game-state: [" + filePath + "]")
    }
  }

  @FXML def loadGame(): Unit = {
    var chooser = new JFileChooser();
    chooser.setDialogTitle("Select path to load gamestate")
    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY)
    chooser.setAcceptAllFileFilterUsed(false)

    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
      val filePath = chooser.getSelectedFile.getAbsolutePath
      val (battleField,clickedPos) = ProtobufBattlefield.loadBattleField(filePath)

      game = BattleShipGame(battleField, getCellWidth, getCellHeight, appendLog)
      game.init(battleGroundGridPane)

      //Update GUI for clicked positions aswell!
      game.clickedPositions = clickedPos
      game.simulateClicksOnClickedPositions()


      appendLog("Load Game-state: [" + filePath + "]")
    }
  }

  override def initialize(url: URL, rb: ResourceBundle): Unit = initGame()

  private def getCellHeight(y: Int): Double = battleGroundGridPane.getRowConstraints.get(y).getPrefHeight

  private def getCellWidth(x: Int): Double = battleGroundGridPane.getColumnConstraints.get(x).getPrefWidth

  def appendLog(message: String): Unit = log.appendText(message + "\n")


  private def initGame(): Unit = {
    val game: BattleShipGame = createGame()
    game.init(battleGroundGridPane)
    appendLog("New game started.")
  }

  private def createGame(): BattleShipGame = {
    val field = BattleField(10, 10, Fleet(FleetConfig.Standard))

    val battleField: BattleField = BattleField.placeRandomly(field)

    game = BattleShipGame(battleField, getCellWidth, getCellHeight, appendLog)
    game
  }

}