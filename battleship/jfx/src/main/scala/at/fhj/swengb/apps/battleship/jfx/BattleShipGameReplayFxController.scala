package at.fhj.swengb.apps.battleship.jfx

import java.net.URL
import java.util.ResourceBundle
import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.{Label, Slider, SplitPane}
import javafx.scene.layout.{BorderPane, GridPane}

import at.fhj.swengb.apps.battleship.model.{BattleFxCell, BattlePos, BattleShipGame, BattleShipGamePlayRound}

class BattleShipGameReplayFxController extends Initializable {

  var selectedPlayRound: BattleShipGamePlayRound = _
  private var game1: BattleShipGame = _
  private var game2: BattleShipGame = _

  @FXML private var lbHeader: Label = _
  @FXML private var lbPlayer1: Label = _
  @FXML private var lbPlayer2: Label = _

  @FXML private var splitPane: SplitPane =_
  @FXML private var borderGameField2: BorderPane = _

  @FXML private var battleGroundGridPane1: GridPane = _
  @FXML private var battleGroundGridPane2: GridPane = _

  @FXML private var clickHistorySlider: Slider = _

  @FXML def onSliderChanged(): Unit = {

    //List starts with 0, not with 1
    val newSliderPosition: Int = clickHistorySlider.getValue.toInt -1

    selectedPlayRound.games.size match {
      case 1 => {
        val takeClickedElements: Int = newSliderPosition + 1
        initGameField(game1,game1.clickedPositions.takeRight(takeClickedElements),battleGroundGridPane1)
      }
      case 2 => {

        //We are at start again. reset all
        if (newSliderPosition == -1) {
          initGameField(game1, Seq(), battleGroundGridPane1)
          initGameField(game2,Seq(),battleGroundGridPane2)
        } else {

          //We have to simulate always the half (player1, player2, player1, ...)
          val takeClickedElements: Int = (newSliderPosition / 2) + 1

          Math.floorMod(newSliderPosition, 2) match {
            //even clicks => This was player 1
            case 0 => {
              initGameField(game1, game1.clickedPositions.takeRight(takeClickedElements), battleGroundGridPane1)
              initGameField(game2, game2.clickedPositions.takeRight(takeClickedElements - 1), battleGroundGridPane2)
            }
            //odd clicks => This was player2
            case _ => {
              initGameField(game1, game1.clickedPositions.takeRight(takeClickedElements), battleGroundGridPane1)
              initGameField(game2, game2.clickedPositions.takeRight(takeClickedElements), battleGroundGridPane2)
            }
          }
        }
      }
    }

  }


  //Same size for gamefield1 & 2
  private def getCellHeight(y: Int): Double = battleGroundGridPane1.getRowConstraints.get(y).getPrefHeight
  private def getCellWidth(x: Int): Double = battleGroundGridPane1.getColumnConstraints.get(x).getPrefWidth

  override def initialize(location: URL, resources: ResourceBundle) = {

    //Init Game
    if ( ! (selectedPlayRound == null) ) {

      selectedPlayRound = initPlayRound(selectedPlayRound)

      lbHeader.setText(selectedPlayRound.name)

      //Initialize gamefield1 - This field is required in any case!
      game1 = selectedPlayRound.games.head
      lbPlayer1.setText(game1.player.name)
      initGameField(game1,Seq(),battleGroundGridPane1)

      selectedPlayRound.games.size match {
        case 1 => {
          //Remove second pane
          splitPane.getItems.remove(borderGameField2)
        }
        case 2 => {
          game2 = selectedPlayRound.games.last
          lbPlayer2.setText(game2.player.name)
          initGameField(game2,Seq(),battleGroundGridPane2)
        }
      }

      //Initialize Slider
      clickHistorySlider.setMax(selectedPlayRound.getTotalAmountOfMoves())
    }
  }

  private def initPlayRound(givenPlayRound: BattleShipGamePlayRound): BattleShipGamePlayRound = {
    def log(x: String) = ()
    BattleShipGamePlayRound(givenPlayRound,getCellWidth,getCellHeight,log,updateGUIAfterAction,BattleShipFxApp.getBattleShipJukeBox,x=>x,x=>x)
  }

  private def updateGUIAfterAction(battleShipGame: BattleShipGame): Unit = {
    //Here can added a special handling but nothing required at the moment!
  }

  private def initGameField(game: BattleShipGame,showClicks: Seq[BattlePos], battleGroundGridPane: GridPane): Unit = {
    battleGroundGridPane.getChildren.clear()
    for (c <- game.getCells) {
      battleGroundGridPane.add(c, c.pos.x, c.pos.y)
      c.setDisable(true)
      c.init(showClicks)
    }
  }
}
