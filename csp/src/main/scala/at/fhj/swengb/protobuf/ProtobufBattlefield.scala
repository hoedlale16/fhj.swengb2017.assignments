package at.fhj.swengb.protobuf

import java.nio.file.{Files, Paths}

import at.fhj.swengb.apps.battleship.jfx.BattleShipGame
import at.fhj.swengb.apps.battleship.{BattlePos, _}
import at.fhj.swengb.protobuf.BattlefieldOuterClass.Battlefield
import at.fhj.swengb.protobuf.BattlefieldOuterClass.Battlefield.{
  Position,
  Vessel,
  VesselOrientation
}

import scala.collection.JavaConverters._

object ProtobufBattlefield {

  def main(args: Array[String]): Unit = {}

  /**
    * Method to save game state of battleship game
    *
    * @param game
    * @param filePath
    */
  def saveBattleShipGame(game: BattleShipGame, filePath: String) = {

    //Create Protobuf Battlefield
    val protoBattleField = BattlefieldOuterClass.Battlefield
      .newBuilder()
      .setFieldWidth(game.battleField.width)
      .setFieldHeight(game.battleField.height)

    //Convert vessesls to protobuf-Vessels and add it
    val fleetProtobuf: Set[Vessel] =
      game.battleField.fleet.vessels.map(e => convertVesseltoProtobufVessel(e))
    fleetProtobuf.foreach(e => protoBattleField.addVessels(e))

    //Convert set of BattleBos to Protobuf clicked positions add add it
    val clickedPos =
      game.clickedPositions.map(e => convertBattlePosToProtobufPosition(e))
    clickedPos.foreach(e => protoBattleField.addClickedPositions(e))

    //Build battlefield and write to file
    protoBattleField.build().writeTo(Files.newOutputStream(Paths.get(filePath)))
  }

  /**
    * Load given protobuf file and creates a battlefield out of it.
    * Returns a Tuple with battleField and already clicked positions
    *
    * @param filePath
    * @return
    */
  def loadBattleField(filePath: String)
  : (at.fhj.swengb.apps.battleship.BattleField, Set[BattlePos]) = {
    val bfIn: Battlefield =
      Battlefield.parseFrom(Files.newInputStream(Paths.get(filePath)))

    //Create BattleField
    val fleet: Fleet = Fleet(
      bfIn.getVesselsList.asScala
        .map(e => convertProtobufVesseltoVessel(e))
        .toSet)
    val battleField =
      BattleField(bfIn.getFieldWidth, bfIn.getFieldHeight, fleet)

    //Create set of alread clicked positions
    val clickedPos: Set[BattlePos] =
      bfIn.getClickedPositionsList.asScala
        .map(e => convertProtoBufPositionToBattlePos(e))
        .toSet

    //Return Battlefield and already clicked positions
    (battleField, clickedPos)
  }

  /**
    * Convertes a given Battleship-Game Vessel to an Protobuf Vessel to store
    *
    * @param vessel
    * @return
    */
  private def convertVesseltoProtobufVessel(
                                             vessel: at.fhj.swengb.apps.battleship.Vessel): Vessel = {

    val vesselOrientation = {
      vessel.direction match {
        case Horizontal => VesselOrientation.Horizontal;
        case Vertical => VesselOrientation.Vertical;
        case _ => ??? /*When this happens, some crazy shit is going on... */
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
  private def convertProtobufVesseltoVessel(
                                             vessel: Vessel): at.fhj.swengb.apps.battleship.Vessel = {

    val name: NonEmptyString = NonEmptyString(vessel.getName)
    val startPos: BattlePos =
      BattlePos(vessel.getStartPos.getX, vessel.getStartPos.getY)
    val size = vessel.getSize
    val direction: Direction = vessel.getOrientation match {
      case VesselOrientation.Horizontal => Horizontal
      case VesselOrientation.Vertical => Vertical
      case _ => ???
    }

    //Create Battleship Vessel and return it.
    at.fhj.swengb.apps.battleship.Vessel(name, startPos, direction, size)
  }

  /**
    * Generates a Protobuf Position from given BattlePos possiton
    *
    * @param battlePos
    * @return
    */
  private def convertBattlePosToProtobufPosition(
                                                  battlePos: BattlePos): Position = {
    Position
      .newBuilder()
      .setX(battlePos.x)
      .setY(battlePos.y)
      .build()
  }

  /**
    * Converts a Protobuf Position to a BattleShipGame BattlePos
    *
    * @param position
    * @return
    */
  private def convertProtoBufPositionToBattlePos(
                                                  position: Position): BattlePos = {
    BattlePos(position.getX, position.getY)
  }
}
