package at.fhj.swengb.apps.battleship.model

import java.io.InputStream
import java.nio.file.{Files, Paths}
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.control.Button
import javafx.scene.image.{Image, ImageView}
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle;

/**
  * Represents one part of a vessel or one part of the ocean.
  */
case class BattleFxCellBUTTON(pos: BattlePos,
                              width: Double,
                              height: Double,
                              log: String => Unit,
                              someVessel: Option[Vessel] = None,
                              fn: (Vessel, BattlePos) => Unit,
                              upClickedPos: BattlePos => Unit)
  extends Button {

  def init(): Unit = {


    /**
      * README!!!
      *
      *
      * THIS IS AN EXPERIMENTAL CLASS:
      * Replaced Rect with a button to set pictures and images to get a nicer
      * look and feel...
      */

    //val view: ImageView  = new ImageView()
    //var is: InputStream = Files.newInputStream(Paths.get("battleship/jfx/src/main/resources/at/fhj/swengb/apps/battleship/jfx/meerButton.jpg"))

    setOnAction(buttonEventHandler)

    this.setPrefSize(100,100)
    this.setMaxSize(100,100)
    this.getStyleClass.add("btGame")
    //view.setImage(new Image(is))
    //setGraphic(view)



    /* Deactivate test mode
    if (someVessel.isDefined) {
      setFill(Color.YELLOWGREEN)
    } else {
      setFill(Color.DARKBLUE)
    }
    */
  }

  val buttonEventHandler = new EventHandler[ActionEvent] {
    override def handle(event: ActionEvent): Unit = {
      handleMouseClick
    }
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

    //val view: ImageView  = new ImageView()
    //var is: InputStream = Files.newInputStream(Paths.get("battleship/jfx/src/main/resources/at/fhj/swengb/apps/battleship/jfx/shipExplosion.jpg"))

    someVessel match {
      case None =>
        log(s"Missed. Just hit water.")
        //is = Files.newInputStream(Paths.get("battleship/jfx/src/main/resources/at/fhj/swengb/apps/battleship/jfx/waterExplosion.jpg"))
        //view.setImage(new Image(is))
        //setGraphic(view)
        this.getStyleClass.add("btGameExpWater")
      case Some(v) => {
        // log(s"Hit an enemy vessel!")
        //view.setImage(new Image(is))
        //setGraphic(view)
        this.getStyleClass.add("btGameExpShip")
        fn(v, pos)
      }
    }
  }

}
