package at.fhj.swengb.apps.battleship

import at.fhj.swengb.apps.battleship.BattleShipProtobuf.BattleShipGame.{ClickPos, Position, Vessel, VesselOrientation}
import at.fhj.swengb.apps.battleship.model._

import scala.collection.JavaConverters._

object BattleShipProtocol {

  def convert(g: BattleShipGame): BattleShipProtobuf.BattleShipGame = {
    //Create Protobuf Battlefield
    val protoGame = BattleShipProtobuf.BattleShipGame
      .newBuilder()
      .setGameName(g.gameName)
      .setFieldHeight(g.battlefields.head._2.height) //Doesnt matter because all fields have the same size!
      .setFieldWidth(g.battlefields.head._2.width)

    //Convert all stored Battlefields (Player and Field itself) to a Protobuf Obj and add it
    val protoFields: List[BattleShipProtobuf.BattleShipGame.BattleField] = g.battlefields.toList.map(e => convert(e._1,e._2))
    protoFields.foreach(e => protoGame.addBattlefields(e))


    //Convert set of BattleBos to Protobuf clicked positions add add it
    val clickedPos: List[ClickPos] = g.clickedPositions.map(e => convert(e._1,e._2))
    clickedPos.foreach(e => protoGame.addClickedPositions(e))

    //Build battlefield and write to file
    protoGame.build()
  }

  def convert(g: BattleShipProtobuf.BattleShipGame): BattleShipGame = {

    //Create all Battlefields stored (at least one)
    val battlefields: Map[Player,BattleField] = g.getBattlefieldsList.asScala.map(e => convert(e,g.getFieldWidth,g.getFieldHeight)).toMap[Player,BattleField]

    //Create set of alread clicked positions
    val clickedPos: List[(Player,BattlePos)] = g.getClickedPositionsList.asScala.map(e => convert(e)).toList

    //Read BattleShipGameName from Protobuf!
    val gameName: String = g.getGameName

    //Create BattleshipGame and set aready clicked positions
    val game = BattleShipGame(gameName,
                              battlefields,
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
    * @param vessel Vessel from BattleShipGame which become converted
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
      case _                            => ??? //When this happens, some crazy shit is going on...
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
    * @param player -> Player which clicked position
    * @param pos -> Clicked position
    * @return
    */
  private def convert(player: Player,pos: BattlePos): ClickPos = {
    ClickPos
      .newBuilder()
      .setClickedPos(convert(pos))
      .setPlayer(convert(player))
      .build()
  }

  /**
    * Converts a ClickedPos from protobuf and returns a tuple of playernr and Position
    * @param clickPos
    * @return
    */
  private def convert(clickPos: ClickPos): (Player,BattlePos) = {
    val battlePos: BattlePos = convert(clickPos.getClickedPos)
    val player: Player = convert(clickPos.getPlayer)

    (player,battlePos)
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

  private def convert(player: Player): BattleShipProtobuf.BattleShipGame.Player = {
    BattleShipProtobuf.BattleShipGame.Player.newBuilder()
      .setName(player.name)
      .setCssStyleClass(player.cssStyleClass)
      .build()
  }

  private def convert(player: BattleShipProtobuf.BattleShipGame.Player): Player = {
    Player(player.getName,player.getCssStyleClass)
  }

  private def convert(player: Player,battlefield: BattleField): BattleShipProtobuf.BattleShipGame.BattleField = {
    val protoBattleField = BattleShipProtobuf.BattleShipGame.BattleField
      .newBuilder()
      .setPlayer(convert(player))

    val fleetProtobuf: Set[Vessel] = battlefield.fleet.vessels.map(e => convert(e))
    fleetProtobuf.foreach(e => protoBattleField.addVessels(e))

    protoBattleField.build()
  }

  private def convert(protoBattleField: BattleShipProtobuf.BattleShipGame.BattleField, width: Int, height: Int): (Player,BattleField) = {
    val player: Player = convert(protoBattleField.getPlayer)
    val fleet: Fleet = Fleet(protoBattleField.getVesselsList.asScala.map(e => convert(e)).toSet)
    val battlefield: BattleField = BattleField(width,height,fleet)

    (player,battlefield)
  }
}
