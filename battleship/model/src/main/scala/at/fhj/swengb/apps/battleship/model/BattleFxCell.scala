package at.fhj.swengb.apps.battleship.model

import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

/**
  * Represents one part of a vessel or one part of the ocean.
  */
case class BattleFxCell(pos: BattlePos,
                        width: Double,
                        height: Double,
                        game: BattleShipGame,
                        log: String => Unit,
                        someVessel: Option[Vessel] = None,
                        upGameState: (Vessel, BattlePos) => Unit,
                        upClickedPos: BattlePos => Unit,
                        jukeBox: BattleShipJukeBox)
  extends Rectangle(width, height) {

  /**
    * Initialize BattleFxCell. If cell is in given list cell gets colorized immediately (already clicked)
    * If simulation mode is active, no further action is triggered
    *
    * @param clickedPos - List of all already clicked positions. Colorize this cell if given list contains it
    */
  def init(clickedPos: Seq[BattlePos]): Unit = {

    //When given list contains position of cell assume that cell was already clicked
    if (clickedPos.contains(pos)) {
      colorizedAfterClick()
    } else {
      init()
    }
  }

  def init(): Unit = {

    setFill(Color.DARKBLUE)

    //TODO: Testing: Just enable for internal test mode. Shows ships during game
    /*if (someVessel.isDefined) {
      setFill(Color.YELLOWGREEN)
    } else {
      setFill(Color.DARKBLUE)
    }*/

  }

  setOnMouseClicked(e => {
    //Color clicked field
    colorizedAfterClick()

    //Just play music on a physical click!
    playMusicAfterClick()

    //Add click to clicked list
    upClickedPos(pos)
  })

  def colorizedAfterClick(): Unit = {
    someVessel match {
      case None =>
        log(game.player.name + ": Missed. Just hit water.")
        setFill(Color.MEDIUMAQUAMARINE)
      case Some(v) =>
        // log(s"Hit an enemy vessel!")
        setFill(Color.RED)
        upGameState(v, pos)

    }
  }

  def playMusicAfterClick(): Unit = {
    someVessel match {
      case None => jukeBox.hitWater()
      case Some(v) => jukeBox.hitShip()
    }
  }

}
