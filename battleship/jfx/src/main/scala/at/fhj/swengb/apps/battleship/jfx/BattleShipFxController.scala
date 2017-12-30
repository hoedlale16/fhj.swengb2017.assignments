package at.fhj.swengb.apps.battleship.jfx

import java.io.File
import java.net.URL
import java.nio.file.{Files, Paths}
import java.util.{Calendar, Optional, ResourceBundle}
import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.Alert.AlertType
import javafx.scene.control._
import javafx.scene.layout.{BorderPane, GridPane, VBox}
import javafx.scene.paint.Color
import javafx.stage.FileChooser
import javafx.stage.FileChooser.ExtensionFilter

import at.fhj.swengb.apps.battleship.{BattleShipProtobuf, BattleShipProtocol}
import at.fhj.swengb.apps.battleship.model._

class BattleShipFxController extends Initializable {

  //Current play round: Initialized in method 'init'
  private var gamePlayround: BattleShipGamePlayRound = _

  //Instance to handle dialogas
  private val dialogHandler: BattleShipFxDialogHandler = new BattleShipFxDialogHandler

  @FXML private var gameBackground: BorderPane = _
  @FXML private var battleGroundGridPane: GridPane = _
  @FXML private var clickHistorySlider: Slider = _
  @FXML private var lbHeader: Label = _
  @FXML private var btSaveGame: Button = _
  @FXML private var lbPlayerName: Label = _
  @FXML private var shipStatisticBox: VBox = _
  @FXML private var log: TextArea = _

  @FXML def newGame(): Unit = {
    //Show Dialog to ask user for Single or Multiplayer gamePlayround
    val result: Optional[String] = dialogHandler.askGameMode()
    if (result.isPresent) {
      log.setText("Started new <" + result.get() + "> Game")

      /**
        * Internal function to create a new Player
        * @param name Name for player. if null replaced with 'Unkown'
        * @param cssStyle Used css style for player-gui
        * @return new created player
        */
      def createPlayer(name: String, cssStyle: String): Player = {
        if (name.isEmpty)
          Player("Unkown", cssStyle)
        else
          Player(name, cssStyle)
      }

      //Start gamePlayround according selection
      result.get() match {
        case "Singleplayer" => {
          var playerNameA: Optional[String] =
            dialogHandler.askSinglePlayerName()
          if (playerNameA.isPresent) {
            //Create Singleplayer game
            val playerA: Player = createPlayer(playerNameA.get(), "bg_playerA")
            val playGround: BattleShipGamePlayRound = BattleShipGamePlayRound(
              playerA,
              getCellWidth,
              getCellHeight,
              appendLog,
              updateGUIAfterAction)
            init(playGround)
          }
        }
        case "Multiplayer" => {
          val result: Optional[(String, String)] = dialogHandler.askMultiplayerPlayerName()
          if (result.isPresent) {

            //Create player for multiplayer game
            val (playerNameA, playerNameB) = result.get()
            val playerA: Player = createPlayer(playerNameA, "bg_playerA")
            val playerB: Player = createPlayer(playerNameB, "bg_playerB")

            /*TODO: Create a new Window(Dialog) Where user can edit the fleet
             * After confirming the ship positions. Player B can do that.
             * When both players confirmed the field. build gamePlayround and start gamePlayround
             */
            val battlefieldPlayerA: BattleField = BattleField(10, 10, Fleet(FleetConfig.Standard))
            val battlefieldPlayerB: BattleField = BattleField(10, 10, Fleet(FleetConfig.Standard))

            //Start gamePlayround
            val playGround = BattleShipGamePlayRound(playerA,
                                                     battlefieldPlayerA,
                                                     playerB,
                                                     battlefieldPlayerB,
                                                     getCellWidth,
                                                     getCellHeight,
                                                     appendLog,
                                                     updateGUIAfterAction)
            init(playGround)
          }
        }
      }

    }
  }

  @FXML def saveGame(): Unit = {
    try {
      val chooser = new FileChooser
      chooser.setTitle("Select path to store")

      //Set Extention filter
      val extensionFilter: ExtensionFilter =
        new ExtensionFilter("Protobuf files", "*.bin")
      chooser.getExtensionFilters.add(extensionFilter)

      //Handle selected file
      var selectedFile: File = chooser.showSaveDialog(BattleShipFxApp.rootStage)

      if (selectedFile != null) {
        //Save gamePlayround state
        val protoBattleShipGame = BattleShipProtocol.convert(gamePlayround)
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
      val chooser = new FileChooser
      chooser.setTitle("Select path to load BattleshipGame")

      //Set Extension filter
      val extensionFilter: FileChooser.ExtensionFilter =
        new ExtensionFilter("Protobuf files", "*.bin")
      chooser.getExtensionFilters.add(extensionFilter)

      //Handle selected file
      val selectedFile: File = chooser.showOpenDialog(BattleShipFxApp.rootStage)
      if (selectedFile != null) { //Reset text area and init gamePlayround
        log.setText("Load Game-state: [" + selectedFile.getAbsolutePath + "]")
        val playRound: BattleShipGamePlayRound = BattleShipGamePlayRound(
          selectedFile,
          BattleShipProtobuf.BattleShipPlayRound.parseFrom,
          getCellWidth,
          getCellHeight,
          appendLog,
          updateGUIAfterAction)


        init(playRound)
      }
    } catch {
      case e: Exception => appendLog("ERROR - Loading failed: " + e.getMessage)
    }
  }

  @FXML def returnToMain(): Unit = BattleShipFxApp.loadMainScene()

  @FXML def onSliderChanged(): Unit = {
    val currVal = clickHistorySlider.getValue.toInt
    var simModeActive: Boolean = true

    //TODO: Add handling to switch between players on multiplayer!

    //Current List of clicks to simulate
    //Reverse clickedPos List.. Take required list and reverse it again!
    val simClickPos: List[BattlePos] =
      gamePlayround.currentBattleShipGame.clickedPositions
        .takeRight(currVal)
        .reverse

    //Print some fancy output to user
    if (currVal == clickHistorySlider.getMax.toInt) {
      appendLog("HISTORY VIEW DEACTIVATED")
      lbHeader.setText(gamePlayround.name)
      simModeActive = false
      /*We are in the present now again, which means that the buttons get active again
        In this case we add the clicks to the list, that means we would have them tice.
        Remove them here now - they get inserted with simulateClicks anyway...
       */
      gamePlayround.currentBattleShipGame.clickedPositions = List()
    } else {
      appendLog("HISTORY VIEW ACTIVATED (" + simClickPos.size + ")")
      lbHeader.setText(gamePlayround.name + "(History)")
      simModeActive = true
    }

    //If we are simulation mode, deactivate buttons
    battleGroundGridPane.getChildren.clear()
    for (c <- gamePlayround.currentBattleShipGame.getCells) {
      battleGroundGridPane.add(c, c.pos.x, c.pos.y)
      c.init()
      //Deactivate Buttons if we're in the history data!
      //History is not changeable, we just can learn from history!
      c.setDisable(simModeActive)
    }

    //Now simulate all already clicked positions

    gamePlayround.currentBattleShipGame.simulateClicksOnClickedPositions(
      simClickPos)

    updateShipStatistic(gamePlayround.currentBattleShipGame)
  }

  override def initialize(url: URL, rb: ResourceBundle): Unit = {
    /*When scene get loaded save button and gamegrid is disabled!
    These components become active when user press New Game or load Game
     */
    btSaveGame.setDisable(true)
    battleGroundGridPane.setVisible(false)
    clickHistorySlider.setVisible(false)
  }

  private def getCellHeight(y: Int): Double = battleGroundGridPane.getRowConstraints.get(y).getPrefHeight
  private def getCellWidth(x: Int): Double = battleGroundGridPane.getColumnConstraints.get(x).getPrefWidth
  private def appendLog(message: String): Unit = log.appendText(message + "\n")

  /**
    * Create a new gamePlayround.
    * This means
    *   - resetting all cells to 'empty' state
    *   - placing your ships at random on the battleground
    *   - enable gaming buttons
    *   - init game field for player A (Single and Multiplayer)
    * @param newPlayRound New playround to initialize
    */
  def init(newPlayRound: BattleShipGamePlayRound): Unit = {
    //Initialize BattleShipGame
    //Required to save state!
    gamePlayround = newPlayRound
    lbHeader.setText(gamePlayround.name)

    //Enable gaming Buttons
    btSaveGame.setDisable(false)
    battleGroundGridPane.setVisible(true)



    //Set gridGame according last played button click
    //The game with the longer "clickedPositions" list was the last one, which means the other game is next
    val gameToInitialize: BattleShipGame =
      gamePlayround.getBattleShipGameWithShorterClicks

    //Initialize game: GridField, Slider and statistic
    switchGameGridField(gameToInitialize)
    updateShipStatistic(gameToInitialize)

    //Just show slider in SinglePlayer Mode
    if (gamePlayround.games.size == 1) {
      updateSliderSize(gameToInitialize.clickedPositions.size)
      clickHistorySlider.setVisible(true)
    } else {
      clickHistorySlider.setVisible(false)
    }

    //Reset Background for gamePlayround which handles multiplayer/singleplyer mode
    gameBackground.getStyleClass.remove("bg_playerA")
    gameBackground.getStyleClass.remove("bg_playerB")
    gameBackground.getStyleClass.add("bg_game")
  }

  /**
    * Iniitialize Gamefield with given Game
    * Used to switch between players in mutliplayer game
    * @param gameToLoad - Game to load
    */
  private def switchGameGridField(gameToLoad: BattleShipGame): Unit = {
    lbPlayerName.setText(gameToLoad.player.name)
    battleGroundGridPane.getChildren.clear()
    for (c <- gameToLoad.getCells) {
      battleGroundGridPane.add(c, c.pos.x, c.pos.y)
    }
    gameToLoad.getCells.foreach(c => c.init(gameToLoad.clickedPositions))

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
    * Get executed after user pressed a field
    * @param game - current game to display
    */
  def updateGUIAfterAction(game: BattleShipGame): Unit = {

    //Resize Slider for history
    updateSliderSize(game.clickedPositions.size)

    //Update Statistics
    updateShipStatistic(game)

    //Check if gamePlayround is over!
    if (game.isGameOver)
      showGameOverDialog(game)
    else
    //Switch game in multiplayer mode
    if (gamePlayround.games.size > 1) {
      //Show Infodialog that other user is ready to play
      val otherGame: BattleShipGame = gamePlayround.getOtherBattleShipGame
      val result = dialogHandler.showPlayerChangeDialog(otherGame.player)
      if (result.isPresent)
        switchGameGridField(otherGame)
    }
  }

  /**
    * Increase slider positons after each click
    * @param maxClicks - maximum amount of already happend clicks
    */
  private def updateSliderSize(maxClicks: Int): Unit = {
    clickHistorySlider.setMax(maxClicks)
    clickHistorySlider.setValue(maxClicks)
  }

  /**
    * Updates Ship statistic according given game
    * @param game - game to read ship statistic from
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

  /**
    * Game is over - Show winner and store game for highscore!
    * @param game - game which has won
    */
  private def showGameOverDialog(game: BattleShipGame): Unit = {

    val alert = new Alert(AlertType.INFORMATION)
    alert.setTitle("G A M E - O V E R")
    alert.setHeaderText("Game over!")
    alert.setContentText("Player <" + game.player.name + "> has won!")
    val result = alert.showAndWait()

    //Deactivate gamePlayround field
    game.getCells.foreach(e => e.setDisable(true))

    //Deactiveate store button
    btSaveGame.setDisable(true)

    //TODO: Store game internally for highscore
  }
}
