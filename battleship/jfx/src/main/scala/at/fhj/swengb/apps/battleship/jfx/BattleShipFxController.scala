package at.fhj.swengb.apps.battleship.jfx

import java.io.File
import java.net.URL
import java.nio.file.{Files, Paths}
import java.util.{Optional, ResourceBundle}
import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.{ChoiceDialog, _}
import javafx.scene.layout.GridPane
import javafx.stage.FileChooser
import javafx.stage.FileChooser.ExtensionFilter

import at.fhj.swengb.apps.battleship.BattleShipProtocol
import at.fhj.swengb.apps.battleship.model._

class BattleShipFxController extends Initializable {

  //Initialized in init
  private var game: BattleShipGame = _

  @FXML private var battleGroundGridPane: GridPane = _
  @FXML private var clickHistorySlider: Slider = _
  @FXML private var lbHeader: Label = _
  @FXML private var btSaveGame: Button = _

  /**
    * A text area box to place the history of the game
    */
  @FXML private var log: TextArea = _

  @FXML def newGame(): Unit = {
    //Show Dialog to ask user for Single or Multiplayer game
    val dialog: ChoiceDialog[String] =
      new ChoiceDialog("Singleplayer", "Singleplayer", "Multiplayer")
    dialog.setTitle("Select Game setting")
    dialog.setHeaderText("Start a new Game:")
    dialog.setContentText("Select your enemy")

    val result: Optional[String] = dialog.showAndWait()
    if (result.isPresent) {
      appendLog("Started new <" + result.get() + "> Game")

      //Start game according selection
      result.get() match {
        case "Singleplayer" => {
          val field = BattleField(10, 10, Fleet(FleetConfig.Standard))
          val battleField: BattleField = BattleField.placeRandomly(field)
          val game = BattleShipGame(battleField,
                                    null,
                                    getCellWidth,
                                    getCellHeight,
                                    appendLog,
                                    updateSlider)

          init(game, List())
        }
        case "Multiplayer" => {
          appendLog("Sorry, not implemented yet!")
          val playerAField = BattleField(10, 10, Fleet(FleetConfig.Standard))
          val playerABattleField: BattleField =
            BattleField.placeRandomly(playerAField)

          val playerBField = BattleField(10, 10, Fleet(FleetConfig.Standard))
          val playerBBattleField: BattleField =
            BattleField.placeRandomly(playerAField)

          val game = BattleShipGame(playerABattleField,
                                    playerBBattleField,
                                    getCellWidth,
                                    getCellHeight,
                                    appendLog,
                                    updateSlider)

          init(game, List())

        }
      }

    }
  }

  @FXML def saveGame(): Unit = {
    try {
      val chooser = new FileChooser();
      chooser.setTitle("Select path to store")

      //Set Extention filter
      val extensionFilter: ExtensionFilter =
        new ExtensionFilter("Protobuf files", "*.bin")
      chooser.getExtensionFilters.add(extensionFilter)

      //Handle selected file
      var selectedFile: File = chooser.showSaveDialog(BattleShipFxApp.rootStage)

      if (selectedFile != null) {
        //Save game state
        val protoBattleShipGame = BattleShipProtocol.convert(game)
        protoBattleShipGame.writeTo(
          Files.newOutputStream(Paths.get(selectedFile.getAbsolutePath)))
        appendLog("Saved Game-state: [" + selectedFile.getAbsolutePath + "]")

      }
    } catch {
      case e: Exception => appendLog("ERROR - Saveing failed: " + e.getMessage)
    }
  }

  @FXML def loadGame(): Unit = {
    try {
      val chooser = new FileChooser();
      chooser.setTitle("Select path to load BattleshipGame")

      //Set Extention filter
      val extensionFilter: FileChooser.ExtensionFilter =
        new ExtensionFilter("Protobuf files", "*.bin")
      chooser.getExtensionFilters.add(extensionFilter)

      //Handle selected file
      var selectedFile: File = chooser.showOpenDialog(BattleShipFxApp.rootStage)
      if (selectedFile != null) {
        //Load game state
        val (battleShipGame, clickedPos) = loadGame(
          selectedFile.getAbsolutePath)
        //Reset text area and init game
        log.setText("Load Game-state: [" + selectedFile.getAbsolutePath + "]")
        init(battleShipGame, clickedPos)
      }
    } catch {
      case e: Exception => appendLog("ERROR - Loading failed: " + e.getMessage)
    }
  }

  @FXML def returnToMain(): Unit = {
    //Abort and switch back to main
    BattleShipFxApp.loadMainScene
  }

  @FXML def onSliderChanged(): Unit = {
    val currVal = clickHistorySlider.getValue.toInt
    var simModeActive: Boolean = true

    //Current List of clicks to simulate
    //Reverse clickedPos List.. Take required list and reverse it again!
    val simClickPos: List[BattlePos] =
      game.clickedPositions.reverse.take(currVal).reverse

    //Print some fancy output to user
    if (currVal == clickHistorySlider.getMax.toInt) {
      appendLog("HISTORY VIEW DEACTIVATED")
      lbHeader.setText("Battleship")
      simModeActive = false
      /*We are in the present now again, which means that the buttons get active again
        In this case we add the clicks to the list, that means we would have them tice.
        Remove them here now - they get inserted with simulateClicks anyway...
       */
      game.clickedPositions = List()
    } else {
      appendLog("HISTORY VIEW ACTIVATED (" + simClickPos.size + ")")
      lbHeader.setText("Battleship (History)")
      simModeActive = true
    }

    //If we are simulation mode, deactivate buttons
    battleGroundGridPane.getChildren.clear()
    for (c <- game.getCells()) {
      battleGroundGridPane.add(c, c.pos.x, c.pos.y)
      c.init()
      //Deactivate Buttons if we're in the history data!
      //History is not changeable, we just can learn from history!
      c.setDisable(simModeActive)
    }

    //Now simulate all already clicked positions
    game.simulateClicksOnClickedPositions(simClickPos)
  }

  override def initialize(url: URL, rb: ResourceBundle): Unit = {
    /*When scene get loaded save button and gamegrid is disabled!
    These components become active when user press New Game or load Game
     */
    btSaveGame.setDisable(true)
    battleGroundGridPane.setVisible(false)
    clickHistorySlider.setVisible(false)
  }

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
  def init(g: BattleShipGame, simulateClicks: List[BattlePos]): Unit = {
    //Initialize BattleShipGame
    //Required to save state!
    game = g

    battleGroundGridPane.getChildren.clear()
    for (c <- g.getCells()) {
      battleGroundGridPane.add(c, c.pos.x, c.pos.y)
    }
    g.getCells().foreach(c => c.init())

    //Reset all previous clicked positions
    //simulate all already clicked positions and update GUI-Slider!
    g.clickedPositions = List()
    g.simulateClicksOnClickedPositions(simulateClicks)
    updateSlider(simulateClicks.size)

    //Enable gaming Buttons
    btSaveGame.setDisable(false)
    battleGroundGridPane.setVisible(true)
    clickHistorySlider.setVisible(true)
  }

  private def loadGame(filePath: String): (BattleShipGame, List[BattlePos]) = {
    //Read Protobuf-Object
    val bsgIn =
      at.fhj.swengb.apps.battleship.BattleShipProtobuf.BattleShipGame
        .parseFrom(Files.newInputStream(Paths.get(filePath)))

    //Convert Protobuf-Object to a BattleShipGame Instance
    val loadedBattleShipGame: BattleShipGame =
      BattleShipProtocol.convert(bsgIn)

    //Create new game-Event based on loaded Data
    //TODO Handle to load single/multiplayer games!
    val battleShipGame = BattleShipGame(loadedBattleShipGame.battleFieldA,null,
                                        getCellWidth,
                                        getCellHeight,
                                        appendLog,
                                        updateSlider)

    battleShipGame.clickedPositions = List()

    (battleShipGame, loadedBattleShipGame.clickedPositions)
  }

  def updateSlider(maxClicks: Int): Unit = {
    clickHistorySlider.setMax(maxClicks)
    clickHistorySlider.setValue(maxClicks)
  }

}
