package at.fhj.swengb.apps.battleship.jfx

import java.net.URL
import java.text.SimpleDateFormat
import java.util.{Date, Observable, ResourceBundle}
import javafx.beans.property.{SimpleIntegerProperty, SimpleStringProperty}
import javafx.collections.{FXCollections, ObservableList}
import javafx.fxml.{FXML, Initializable}
import javafx.scene.control._
import javafx.scene.layout.GridPane
import javafx.stage.Stage

import at.fhj.swengb.apps.battleship.model.{BattleShip, _}

class BattleShipMutliplayerEditFxController extends Initializable {

  @FXML private var lbHeader: Label = _
  @FXML private var tfPlayerName: TextField = _
  @FXML private var lvFleet: ListView[VesselListViewEntry] = _
  @FXML private var cbOrientation: ComboBox[Direction] = _
  @FXML private var battleGroundGridPane: GridPane = _
  @FXML private var btInitialize: Button = _

  //TODO: For Multiplayergame-Tests set here to OneShip is required
  private val usedFleetConfig: FleetConfig = FleetConfig.Standard
  private var gameField: BattleField = BattleField.placeRandomly(BattleField(10, 10, Fleet.Empty))

  var initPlayerNr: Int = 1
  var closedRegularly: Boolean = false

  private var player: Player = _
  private def getCssStyle(playerNr: Int ): String = playerNr match {
    case 1 => "bg_playerA"
    case 2 => "bg_playerB"
    case _ => "bg_game"
  }

  def getResult: Option[(Player,BattleField)] = Option(player,gameField)


  @FXML def onInitialize(): Unit = {
    closedRegularly = true
    val stage = btInitialize.getScene.getWindow.asInstanceOf[Stage]
    stage.close()
  }

  @FXML def onSetRandomFleet(): Unit = {
    gameField = BattleField.placeRandomly(BattleField(10, 10, Fleet(usedFleetConfig)))
    refreshGameField(gameField)
    refreshFleetList(gameField.fleet.vessels)
    checkEnableInitButton()
  }

  @FXML def onClearFleet(): Unit = {
    //Reset gameField
    gameField = BattleField(10,10,Fleet(Set[Vessel]()))

    //Update UI elements
    refreshGameField(gameField)
    refreshFleetList(gameField.fleet.vessels)

    checkEnableInitButton()
  }

  @FXML def onPlayerNameEntered(): Unit = {
    if(tfPlayerName.getText().nonEmpty) {
      player = Player(tfPlayerName.getText(), getCssStyle(initPlayerNr))
    } else {
      player = null
    }
    checkEnableInitButton()
  }

  @FXML def onShipSelected(): Unit = {
    if(lvFleet.getSelectionModel.getSelectedItem != null) {

      val ship: Vessel = lvFleet.getSelectionModel.getSelectedItem.getVessel()
      val direction: Direction = cbOrientation.getSelectionModel.getSelectedItem

      //Enable posibitlty to cklick
      if (ship != null && direction != null) {
        //TODO: Just enable buttons where ship is allowed to be positoned
        disableButtons(false)
      }
    }
  }


  override def initialize(location: URL, resources: ResourceBundle) = {
    lbHeader.setText("Initialize Player " + initPlayerNr)

    //Just allow to select one element from list!
    lvFleet.getSelectionModel.setSelectionMode(SelectionMode.SINGLE)

    //Show gameField and initi UI
    refreshGameField(gameField)
    refreshFleetList(gameField.fleet.vessels)
    initDirectionComboBox

    //Disable init button. No fleet set yet!
    checkEnableInitButton()
  }

  private def getCellHeight(y: Int): Double = battleGroundGridPane.getRowConstraints.get(y).getPrefHeight
  private def getCellWidth(x: Int): Double = battleGroundGridPane.getColumnConstraints.get(x).getPrefWidth

  private def initDirectionComboBox(): Unit = {
    //Now set rest of vessels as "to set"
    val cbData: ObservableList[Direction] = FXCollections.observableArrayList()
    cbData.add(Horizontal)
    cbData.add(Vertical)

    cbOrientation.setItems(cbData)
    cbOrientation.getSelectionModel.selectFirst()
  }

  /**
    * Function called after click happend in EditorFxCell
    * @param pos - Clicked position - Set vessel there!
    */
  private def updateEditorAfterClick(pos: BattlePos): Unit = {

    var selVessel: Vessel = lvFleet.getSelectionModel.getSelectedItem.getVessel()
    val selDirection: Direction = cbOrientation.getSelectionModel.getSelectedItem

    val shipName: String = selVessel.name.value
    //Enable posibitlty to cklick
    if (selVessel != null && selDirection != null) {
      val newVessel: Vessel = selVessel.size match {
        case 2 => new Submarine(shipName, pos, selDirection)
        case 3 => new Destroyer(shipName, pos, selDirection)
        case 4 => new Cruiser(shipName, pos, selDirection)
        case 5 => new BattleShip(shipName, pos, selDirection)
        case _ => ??? /*Can't happen */
      }

      val newShips: Seq[Vessel] = newVessel +: gameField.fleet.vessels.toSeq
      gameField = BattleField(10,10,new Fleet(newShips.toSet))
     }

    //Refresh UI and disable Buttons
    refreshGameField(gameField)
    refreshFleetList(gameField.fleet.vessels)
    disableButtons(true)
    checkEnableInitButton
  }


  /**
    * Set given game field in Editor
    * @param field
    */
  private def refreshGameField(field: BattleField): Unit =  {
    battleGroundGridPane.getChildren.clear()

    val cells: Seq[EditorFxCell] = for {
      x <- 0 until field.width
      y <- 0 until field.height
      pos = BattlePos(x, y)
    } yield {
      EditorFxCell(BattlePos(x, y),
        getCellWidth(x),
        getCellHeight(y),
        field.fleet.findByPos(pos),
        updateEditorAfterClick)
    }
    for (c <- cells) {
      battleGroundGridPane.add(c, c.pos.x, c.pos.y)
      c.init()
    }

    disableButtons(true)
  }

  /**
    * Disable or enable gamefield
    * @param disable
    */
  private def disableButtons(disable: Boolean): Unit = {
    battleGroundGridPane.getChildren().forEach(e => e.setDisable(disable))
  }

  /**
    * Refreshes listview model which contains all not set vessels
    * @param alreadySet
    */
  private def refreshFleetList(alreadySet: Set[Vessel]): Unit = {
    var aFullStandardFleet: Set[Vessel] = Fleet(usedFleetConfig).vessels

    //Remove all vessels from fullFleet list which are in given List
    //Name is unique, positions maight different. Just check againts names
    val alreadySetShips: Set[NonEmptyString] = alreadySet.map(e => e.name)
    aFullStandardFleet = aFullStandardFleet.filter(e => ! alreadySetShips.contains(e.name))

    //Now set rest of vessels as "to set"
    val lvData: ObservableList[VesselListViewEntry] = FXCollections.observableArrayList()
    aFullStandardFleet.toList.foreach(e => lvData.add(VesselListViewEntry(e)) )
    lvFleet.setItems(lvData)
  }

  /**
    * Checks if game button is allowed to be enabled
    */
  private def checkEnableInitButton(): Unit = {
    //Check if playername set and all vessels are set
    if(player != null && gameField != null &&
       tfPlayerName.getText().nonEmpty && lvFleet.getItems.size() == 0) {
      btInitialize.setDisable(false)
    } else
      btInitialize.setDisable(true)
  }


  /**
    * Internal class - required to display a readable name of vessels
    */
  private class VesselListViewEntry {
    val name: SimpleStringProperty = new SimpleStringProperty()
    private var ship: Vessel = _

    def setName(name: String) = this.name.set(name)

    def setVessel(vessel: Vessel) = {
      ship = vessel
    }

    def getVessel(): Vessel= ship

    override def toString: String = name.get
  }

  private object VesselListViewEntry {
    def apply(vessel: Vessel): VesselListViewEntry = {
      val entry = new VesselListViewEntry
      entry.setName(vessel.name.value)
      entry.setVessel(vessel)

      //Return object
      entry
    }
  }
}



