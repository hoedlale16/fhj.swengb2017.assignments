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
                        upClickedPos: BattlePos => Unit)
  extends Rectangle(width, height) {

  /**
    * Initialize BattleFxCell. If cell is in given list cell get colorized immediately(already clicked)
    * If simulation mode is active, no further action is triggered
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

    //Just required for internal test mode
    if (someVessel.isDefined) {
      setFill(Color.YELLOWGREEN)
    } else {
      setFill(Color.DARKBLUE)
    }

  }

  setOnMouseClicked(e => {
    handleMouseClick()
  })

  def handleMouseClick(): Unit= {
    //Color clicked field
    colorizedAfterClick()

    /*IF Button is disabled, we are in simulation mode
    in this case we're not allowed to add position to clickedPos-List
    because click is already there...

    Otherwhise, add click to position Lits
     */
    if(!isDisable)
      upClickedPos(pos)

  }

  def colorizedAfterClick(): Unit = {
    someVessel match {
      case None =>
        log(game.player.name + ": Missed. Just hit water.")
        setFill(Color.MEDIUMAQUAMARINE)
      case Some(v) =>
        // log(s"Hit an enemy vessel!")
        setFill (Color.RED)
        upGameState(v, pos)

    }
  }

}
