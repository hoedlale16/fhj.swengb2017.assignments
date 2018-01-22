package at.fhj.swengb.apps.battleship.jfx

import java.io.File
import java.net.URL
import java.nio.file.{Files, Paths}
import java.util.{Optional, ResourceBundle}
import javafx.fxml.{FXML, Initializable}
import javafx.scene.control._
import javafx.scene.layout.{BorderPane, GridPane, VBox}
import javafx.scene.paint.Color
import javafx.stage.FileChooser
import javafx.stage.FileChooser.ExtensionFilter

import at.fhj.swengb.apps.battleship.model._
import at.fhj.swengb.apps.battleship.{BattleShipProtobuf, BattleShipProtocol}

class BattleShipFxController extends Initializable {

  //Current play round: Initialized in method 'init'
  private var gamePlayround: BattleShipGamePlayRound = _

  @FXML private var gameBackground: BorderPane = _
  @FXML private var battleGroundGridPane: GridPane = _
  @FXML private var clickHistorySlider: Slider = _
  @FXML private var lbHeader: Label = _
  @FXML private var btSaveGame: Button = _
  @FXML private var lbPlayerName: Label = _
  @FXML private var lbStatisticHeader: Label = _
  @FXML private var lbPlayerHeader: Label = _
  @FXML private var shipStatisticBox: VBox = _
  @FXML private var log: TextArea = _

  @FXML def newGame(): Unit = {
    //Display dialog asking user for Single- or Multiplayer gamePlayround
    val result: Optional[String] = BattleShipFxDialogHandler().askGameMode()
    if (result.isPresent) {
      log.setText("Started new " + result.get() + "-Game")

      /**
        * Internal function to create a new Player
        *
        * @param name     Name for player. If null (not supplied by the user), replaced with 'Unknown'
        * @param cssStyle Used css style for player-gui
        * @return newly created player
        */
      def createPlayer(name: String, cssStyle: String): Player = {
        if (name.isEmpty)
          Player("Unknown", cssStyle)
        else
          Player(name, cssStyle)
      }

      //Start gamePlayround according to the user's selection
      result.get() match {
        case "Singleplayer" =>
          var playerNameA: Optional[String] =
            BattleShipFxDialogHandler().askSinglePlayerName()
          if (playerNameA.isPresent) {
            //Create a Singleplayer game
            val playerA: Player = createPlayer(playerNameA.get(), "bg_playerA")
            val playGround: BattleShipGamePlayRound = BattleShipGamePlayRound(
              playerA,
              getCellWidth,
              getCellHeight,
              appendLog,
              updateGUIAfterAction,
              BattleShipFxApp.getBattleShipJukeBox,
              BattleShipFxApp.getUsedFleetConfig)
            init(playGround)
          }
        case "Multiplayer" =>

          //Initialize playerA
          BattleShipFxDialogHandler().initMultiPlayer(1) match {
            case (null, null) => //userA aborted
            case (playerA, bfPlayerA) =>
              //Initialize playerB
              BattleShipFxDialogHandler().initMultiPlayer(2) match {
                case (null, null) => //userB aborted
                case (playerB, bfPlayerB) =>
                  //Start gamePlayround if both users are initialized
                  val playGround = BattleShipGamePlayRound(playerA,
                    bfPlayerA,
                    playerB,
                    bfPlayerB,
                    getCellWidth,
                    getCellHeight,
                    appendLog,
                    updateGUIAfterAction,
                    BattleShipFxApp.getBattleShipJukeBox)
                  init(playGround)
              }
          }
      }
    }
  }

  @FXML def saveGame(): Unit = {
    try {
      val chooser = new FileChooser
      chooser.setTitle("Select path to save your BattleShipGame")

      //Set Extension filter
      val extensionFilter: ExtensionFilter =
        new ExtensionFilter("Protobuf binary files", "*.bin")
      chooser.getExtensionFilters.add(extensionFilter)

      //Handle selected file
      var selectedFile: File = chooser.showSaveDialog(null)

      if (selectedFile != null) {
        //Save gamePlayround state
        val protoBattleShipGame = BattleShipProtocol.convert(gamePlayround)
        protoBattleShipGame.writeTo(
          Files.newOutputStream(Paths.get(selectedFile.getAbsolutePath)))
        appendLog("Saved current Gamestate in " + selectedFile.getAbsolutePath)

      }
    } catch {
      case e: Exception => appendLog("ERROR - Saving failed! " + e.getMessage)
    }
  }

  @FXML def loadGame(): Unit = {
    try {
      val chooser = new FileChooser
      chooser.setTitle("Select path to load your BattleShipGame")

      //Set Extension filter
      val extensionFilter: FileChooser.ExtensionFilter =
        new ExtensionFilter("Protobuf binary files", "*.bin")
      chooser.getExtensionFilters.add(extensionFilter)

      //Handle selected file
      val selectedFile: File = chooser.showOpenDialog(null)
      if (selectedFile != null) { //Reset text area and init gamePlayround
        log.setText("Loaded Gamestate from " + selectedFile.getAbsolutePath)
        val playRound: BattleShipGamePlayRound = BattleShipGamePlayRound(
          selectedFile,
          BattleShipProtobuf.BattleShipPlayRound.parseFrom,
          getCellWidth,
          getCellHeight,
          appendLog,
          updateGUIAfterAction,
          BattleShipFxApp.getBattleShipJukeBox,
          x => x)


        init(playRound)
      }
    } catch {
      case e: Exception => appendLog("ERROR - Loading failed! " + e.getMessage)
    }
  }

  @FXML def returnToMain(): Unit = BattleShipFxApp.showScene(BattleShipFxApp.getWelcomeScene, BattleShipFxApp.getRootStage)


  @FXML def onSliderChanged(): Unit = {
    val currVal = clickHistorySlider.getValue.toInt
    var simModeActive: Boolean = true

    //Current List of clicks to simulate
    //Reverse clickedPos List.. Take required amount of list-entries and reverse it back!
    val simClickPos: List[BattlePos] =
    gamePlayround.currentBattleShipGame.clickedPositions
      .takeRight(currVal)
      .reverse

    //Display some fancy output to the user
    if (currVal == clickHistorySlider.getMax.toInt) {
      appendLog("HISTORY VIEW DEACTIVATED")
      lbHeader.setText(gamePlayround.name)
      simModeActive = false
    } else {
      appendLog("HISTORY VIEW ACTIVATED (" + simClickPos.size + ")")
      lbHeader.setText(gamePlayround.name + "(History)")
      simModeActive = true
    }

    //If we are in simulation mode, deactivate buttons
    battleGroundGridPane.getChildren.clear()
    for (c <- gamePlayround.currentBattleShipGame.getCells) {
      battleGroundGridPane.add(c, c.pos.x, c.pos.y)
      c.init(simClickPos)
      //Deactivate Buttons if we're in the historic data!
      //History cannot be changed, we can just learn from history – or we are doomed to repeat it!
      c.setDisable(simModeActive)
    }

    updateShipStatistic(gamePlayround.currentBattleShipGame)
  }

  private def appendLog(message: String): Unit = log.appendText(message + "\n")

  /**
    * Updates Ship statistics for a given game
    *
    * @param game - game to read ship statistics from
    */
  private def updateShipStatistic(game: BattleShipGame): Unit = {
    shipStatisticBox.getChildren.clear()
    for (v <- game.battleField.fleet.vessels) {
      val label: Label = new Label(v.name.value)
      if (game.sunkShips.contains(v)) {
        label.setTextFill(Color.RED)
      } else {
        label.setTextFill(Color.GREEN)
      }

      shipStatisticBox.getChildren.add(label)
    }
  }

  override def initialize(url: URL, rb: ResourceBundle): Unit = {
    /*When a scene gets loaded, save button and gamegrid are disabled!
    These components become active, once the user clicks New Game or load Game
     */
    btSaveGame.setDisable(true)
    battleGroundGridPane.setVisible(false)
    clickHistorySlider.setVisible(false)
    lbStatisticHeader.setVisible(false)
    lbPlayerHeader.setVisible(false)
    log.setVisible(false)
  }

  /**
    * Creates a new gamePlayround.
    * This means
    *   - resetting all cells to the 'empty' state
    *   - placing your ships at random on the battleground
    *   - enabling gaming buttons
    *   - init-ing game field for player A (Single and Multiplayer)
    *
    * @param newPlayRound New playround to initialize
    */
  def init(newPlayRound: BattleShipGamePlayRound): Unit = {
    //Initialize BattleShipGame
    //Required to save state!
    gamePlayround = newPlayRound
    lbHeader.setText(gamePlayround.name)

    //Enable gaming Buttons and labels
    btSaveGame.setDisable(false)
    battleGroundGridPane.setVisible(true)
    lbStatisticHeader.setVisible(true)
    lbPlayerHeader.setVisible(true)
    log.setVisible(true)

    //Reset Background for gamePlayround which handles multiplayer/singleplayer mode
    gameBackground.getStyleClass.remove("bg_playerA")
    gameBackground.getStyleClass.remove("bg_playerB")
    gameBackground.getStyleClass.add("bg_game")

    //Set gridGame according to last played button click
    //The game with the longer "clickedPositions" list was the last one, which means the other game is next
    val gameToInitialize: BattleShipGame =
    gamePlayround.getBattleShipGameWithShorterClicks

    //Initialize game: GridField, Slider and statistics
    switchGameGridField(gameToInitialize)

    //Just show the slider in SinglePlayer Mode
    if (gamePlayround.games.lengthCompare(1) == 0) {
      updateSliderSize(gameToInitialize.clickedPositions.size)
      clickHistorySlider.setVisible(true)
    } else {
      clickHistorySlider.setVisible(false)
    }
  }

  /**
    * Gets executed after user clicked on a field
    *
    * @param game - current game to display
    */
  def updateGUIAfterAction(game: BattleShipGame): Unit = {

    //Resize Slider for history
    updateSliderSize(game.clickedPositions.size)

    //Update Statistics
    updateShipStatistic(game)

    //Check if gamePlayround is over!
    if (game.isGameOver) {
      //Set winner
      gamePlayround.setWinner(game.player)

      //Show Game over dialog
      val result = BattleShipFxDialogHandler().showGameOverDialog(gamePlayround)
      if (result.isPresent) {
        //Deactivate save button
        btSaveGame.setDisable(true)

        //Store game internally for the highscore-table
        HighScore().addRoundToHighScore(gamePlayround)
      } else {
        appendLog("ERROR: Unexpected end of game!")
      }
    } else {
      //Switch game to multiplayer mode
      //Autorefactoring: lengthCompare equals method size (-1 shorter, 0 equal, 1 longer)
      if (gamePlayround.games.lengthCompare(1) > 0) {
        //Display Info that other user is ready to play
        val otherGame: BattleShipGame = gamePlayround.getOtherBattleShipGame
        val result = BattleShipFxDialogHandler().showPlayerChangeDialog(otherGame.player)
        if (result.isPresent)
          switchGameGridField(otherGame)
      }
    }
  }

  private def getCellHeight(y: Int): Double = battleGroundGridPane.getRowConstraints.get(y).getPrefHeight

  private def getCellWidth(x: Int): Double = battleGroundGridPane.getColumnConstraints.get(x).getPrefWidth

  /**
    * Initialize Gamefield with given Game
    * Used to switch between players in mutliplayer game
    *
    * @param gameToLoad - Game to load
    */
  private def switchGameGridField(gameToLoad: BattleShipGame): Unit = {
    lbPlayerName.setText(gameToLoad.player.name)
    battleGroundGridPane.getChildren.clear()
    for (c <- gameToLoad.getCells) {
      battleGroundGridPane.add(c, c.pos.x, c.pos.y)
      c.init(gameToLoad.clickedPositions)
    }

    //Change Background according to style from user
    gameBackground.getStyleClass.remove("bg_playerA")
    gameBackground.getStyleClass.remove("bg_playerB")
    gameBackground.getStyleClass.add(gameToLoad.player.cssStyleClass)

    //Set given game as current game
    gamePlayround.currentBattleShipGame = gameToLoad

    //Update Statistics for current user
    updateShipStatistic(gameToLoad)
  }

  /**
    * Increase slider positions after each click
    *
    * @param maxClicks - maximum amount of historic clicks
    */
  private def updateSliderSize(maxClicks: Int): Unit = {
    clickHistorySlider.setMax(maxClicks)
    clickHistorySlider.setValue(maxClicks)
  }
}
