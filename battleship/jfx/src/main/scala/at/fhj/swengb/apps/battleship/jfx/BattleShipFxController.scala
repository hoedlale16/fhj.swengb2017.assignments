package at.fhj.swengb.apps.battleship.jfx

import java.io.FileOutputStream
import java.net.URL
import java.nio.file.{Files, Paths}
import java.util.ResourceBundle
import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.TextArea
import javafx.scene.layout.GridPane
import javax.swing.JFileChooser

import at.fhj.swengb.apps.battleship.{BattleShipProtobuf, BattleShipProtocol}
import at.fhj.swengb.apps.battleship.model._

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
    try {
      var chooser = new JFileChooser();
      chooser.setDialogTitle("Select path to store")
      chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY)
      chooser.setAcceptAllFileFilterUsed(false)

      if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
        val filePath = chooser.getSelectedFile.getAbsolutePath + "\\battleship.bin"

        val protoBattleShipGame = BattleShipProtocol.convert(game);
        protoBattleShipGame.writeTo(Files.newOutputStream(Paths.get(filePath)))

        appendLog("Saved Game-state: [" + filePath + "]")
      }
    } catch {
      case e: Exception => {
        appendLog("ERROR - Saveing failed: " + e.getMessage)
      }
    }
  }

  @FXML def loadGame(): Unit = {
    try {
      var chooser = new JFileChooser();
      chooser.setDialogTitle("Select path to load gamestate")
      chooser.setFileSelectionMode(JFileChooser.FILES_ONLY)
      chooser.setAcceptAllFileFilterUsed(false)

      if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
        val filePath = chooser.getSelectedFile.getAbsolutePath

        //Read Protobuf-Object
        val bsgIn =
          at.fhj.swengb.apps.battleship.BattleShipProtobuf.BattleShipGame
            .parseFrom(Files.newInputStream(Paths.get(filePath)))

        //Convert Protobuf-Object to a BattleShipGame Instance
        val loadedBattleShipGame: BattleShipGame =
          BattleShipProtocol.convert(bsgIn)

        //Create new game-Event based on loaded Data
        game = BattleShipGame(loadedBattleShipGame.battleField,
                              getCellWidth,
                              getCellHeight,
                              appendLog)
        init(game)

        //Set already clicked positions and update GUI!
        game.clickedPositions = loadedBattleShipGame.clickedPositions
        game.simulateClicksOnClickedPositions()

        appendLog("Load Game-state: [" + filePath + "]")
      }
    } catch {
      case e: Exception => appendLog("ERROR - Loading failed: " + e.getMessage)
    }
  }

  override def initialize(url: URL, rb: ResourceBundle): Unit = initGame()

  private def getCellHeight(y: Int): Double =
    battleGroundGridPane.getRowConstraints.get(y).getPrefHeight

  private def getCellWidth(x: Int): Double =
    battleGroundGridPane.getColumnConstraints.get(x).getPrefWidth

  def appendLog(message: String): Unit = log.appendText(message + "\n")

  /**
    * Create a new game.
    *
    * This means
    *
    * - resetting all cells to 'empty' state
    * - placing your ships at random on the battleground
    *
    */
  def init(game: BattleShipGame): Unit = {
    battleGroundGridPane.getChildren.clear()
    for (c <- game.getCells) {
      battleGroundGridPane.add(c, c.pos.x, c.pos.y)
    }
    game.getCells().foreach(c => c.init)
  }

  private def initGame(): Unit = {
    game = createNewGame()
    init(game)
    appendLog("New game started.")
  }

  private def createNewGame(): BattleShipGame = {
    val field = BattleField(10, 10, Fleet(FleetConfig.Standard))

    val battleField: BattleField = BattleField.placeRandomly(field)

    BattleShipGame(battleField, getCellWidth, getCellHeight, appendLog)
  }
}
