package at.fhj.swengb.apps.battleship.model

import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle;

/**
  * Represents one part of a vessel or one part of the ocean.
  */
case class BattleFxCell(pos: BattlePos,
                        width: Double,
                        height: Double,
                        log: String => Unit,
                        someVessel: Option[Vessel] = None,
                        fn: (Vessel, BattlePos) => Unit,
                        upClickedPos: BattlePos => Unit)
    extends Rectangle(width, height) {

  def init(): Unit = {

    setFill(Color.DARKBLUE)

    /* Deactivate test mode
    if (someVessel.isDefined) {
      setFill(Color.YELLOWGREEN)
    } else {
      setFill(Color.DARKBLUE)
    }
    */
  }

  setOnMouseClicked(e => {
    handleMouseClick
  })

  def handleMouseClick() = {
    /*IF Button is disabled, we are in simulation mode
    in this case we're not allowed to add position to clickedPos-List
    because click is already there...

    Otherwhise, add click to position Lits
     */
    if(!isDisable)
      upClickedPos(pos)


    someVessel match {
      case None =>
        log(s"Missed. Just hit water.")
        setFill(Color.MEDIUMAQUAMARINE)
      case Some(v) =>
        // log(s"Hit an enemy vessel!")
        fn(v, pos)
        setFill(Color.RED)
    }
  }

}
