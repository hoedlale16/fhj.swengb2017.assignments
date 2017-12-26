package at.fhj.swengb.apps.battleship

import at.fhj.swengb.apps.battleship.BattleShipProtobuf.BattleShipGame.{ClickPos, Position, Vessel, VesselOrientation}
import at.fhj.swengb.apps.battleship.model._

import scala.collection.JavaConverters._

object BattleShipProtocol {

  def convert(g: BattleShipGame): BattleShipProtobuf.BattleShipGame = {
    //Create Protobuf Battlefield
    val protoBattleField = BattleShipProtobuf.BattleShipGame
      .newBuilder()
      .setGameName(g.gameName)
      .setFieldWidth(g.battleFieldA.width) //A & B have the same size...
      .setFieldHeight(g.battleFieldA.height)

    //Convert vessesls to protobuf-Vessels and add it
    val fleetProtobuf: Set[Vessel] =
      g.battleFieldA.fleet.vessels.map(e => convert(e))
    fleetProtobuf.foreach(e => protoBattleField.addVesselsPlayerA(e))

    //Convert set of BattleBos to Protobuf clicked positions add add it
    val clickedPos: List[ClickPos] = g.clickedPositions.map(e => convert(e._1,e._2))
    clickedPos.foreach(e => protoBattleField.addClickedPositions(e))

    //Build battlefield and write to file
    protoBattleField.build()
  }

  def convert(g: BattleShipProtobuf.BattleShipGame): BattleShipGame = {
    //Create data for BattleField
    val fleet: Fleet = Fleet(
      g.getVesselsPlayerAList.asScala.map(e => convert(e)).toSet)

    val battleFieldA = BattleField(g.getFieldWidth, g.getFieldHeight, fleet)
    val battleFieldB = null //TODO: Read Battlefield for player 2 or null if singleplayer

    //Create set of alread clicked positions
    val clickedPos: List[(Int,BattlePos)] =
      g.getClickedPositionsList.asScala.map(e => convert(e)).toList

    //Read BattleShipGameName from Protobuf!
    val gameName: String = g.getGameName

    //Create BattleshipGame and set aready clicked positions
    val game = BattleShipGame(gameName,
                              battleFieldA,
                              battleFieldB,
                              (e => e.toDouble),
                              (e => e.toDouble),
                              (e => ()),
                              (e => ()))
    game.clickedPositions = clickedPos

    //return game
    game
  }

  /**
    * Convertes a given Battleship-Game Vessel to an Protobuf Vessel to store
    *
    * @param vessel
    * @return
    */
  // Previous convertVesseltoProtobufVessel
  def convert(vessel: at.fhj.swengb.apps.battleship.model.Vessel): Vessel = {

    val vesselOrientation = {
      vessel.direction match {
        case Horizontal => VesselOrientation.Horizontal;
        case Vertical   => VesselOrientation.Vertical;
        case _          => ??? /*When this happens, some crazy shit is going on... */
      }
    }

    //Create new protobuf Vessel
    Vessel
      .newBuilder()
      .setName(vessel.name.value)
      .setSize(vessel.size)
      .setOrientation(vesselOrientation)
      .setStartPos(
        Position
          .newBuilder()
          .setX(vessel.startPos.x)
          .setY(vessel.startPos.y)
          .build())
      .build()
  }

  /**
    * Convert a Protobuf Vessel to a BattleShipGame Vessel
    *
    * @param vessel
    * @return
    */
  //Previous convertProtobufVesseltoVessel
  def convert(vessel: Vessel): at.fhj.swengb.apps.battleship.model.Vessel = {

    val name: NonEmptyString = NonEmptyString(vessel.getName)
    val startPos: BattlePos =
      BattlePos(vessel.getStartPos.getX, vessel.getStartPos.getY)
    val size = vessel.getSize
    val direction: Direction = vessel.getOrientation match {
      case VesselOrientation.Horizontal => Horizontal
      case VesselOrientation.Vertical   => Vertical
      case _                            => ???
    }

    //Create Battleship Vessel and return it.
    at.fhj.swengb.apps.battleship.model.Vessel(name, startPos, direction, size)
  }

  /**
    * Generates a Protobuf Position from given BattlePos possiton
    *
    * @param battlePos
    * @return
    */
  //Previous convertBattlePosToProtobufPosition
  private def convert(battlePos: BattlePos): Position = {
    Position
      .newBuilder()
      .setX(battlePos.x)
      .setY(battlePos.y)
      .build()
  }

  /**
    * Generates a clicked position to store in protobuf
    * @param playerNr -> Player which clicked. starts with 1
    * @param pos -> Clicked position
    * @return
    */
  private def convert(playerNr: Int,pos: BattlePos): ClickPos = {
    ClickPos
      .newBuilder()
      .setClickedPos(convert(pos))
      .setPlayerNumber(playerNr)
      .build()
  }

  /**
    * Converts a ClickedPos from protobuf and returns a tuple of playernr and Position
    * @param clickPos
    * @return
    */
  private def convert(clickPos: ClickPos): (Int,BattlePos) = {
    val battlePos: BattlePos = convert(clickPos.getClickedPos)
    val playerNr: Int = clickPos.getPlayerNumber

    (playerNr,battlePos)
  }

  /** Converts a Protobuf Position to a BattleShipGame BattlePos
    *
    * @param position
    * @return
    */
  //previous convertProtoBufPositionToBattlePos
  private def convert(position: Position): BattlePos = {
    BattlePos(position.getX, position.getY)
  }

}
