package at.fhj.swengb.apps.battleship

import java.text.{DateFormat, SimpleDateFormat}
import java.util.{Calendar, Date}

import at.fhj.swengb.apps.battleship.BattleShipProtobuf.BattleShipPlayRound.{Position, Vessel, VesselOrientation}
import at.fhj.swengb.apps.battleship.model._

import scala.collection.JavaConverters._

object BattleShipProtocol {

  def convert(g: BattleShipGamePlayRound): BattleShipProtobuf.BattleShipPlayRound = {
    //Create Protobuf Battlefield
    val protoPlayRound = BattleShipProtobuf.BattleShipPlayRound
      .newBuilder()
      .setGameName(g.name)

     //Add all Instances of BattleshipGames
      //Singleplayer => max 1
      //Multiplayer => max 2
     val protoGames = g.games.map(e => convert(e))
     protoGames.foreach(e => protoPlayRound.addBattlefieldsGames(e))

    //If change on format is required change reverse-convert function as well!!
    protoPlayRound.setStartdate(new SimpleDateFormat("yyyy/MM/dd").format(g.startDate))

    //Build battlefield and write to file
    protoPlayRound.build()
  }

  def convert(protoPlayGround: BattleShipProtobuf.BattleShipPlayRound): BattleShipGamePlayRound = {

    //Create all Battlefields stored (at least one)
    val battleFieldGames: Seq[BattleShipGame] = protoPlayGround.getBattlefieldsGamesList.asScala.map(e => convert(e)).toList

    //Read BattleShipGameName from Protobuf!

    val gameName: String = protoPlayGround.getGameName
    println("Read name: " + gameName)

    //If change on format is required change reverse-convert function as well!!
    val startDate: Date = new SimpleDateFormat("yyyy/MM/dd").parse(protoPlayGround.getStartdate)

    //Create BattleshipGame and set aready clicked positions
    val playRound = BattleShipGamePlayRound(gameName,
                              battleFieldGames,startDate)


    //return game
    playRound
  }
  
  
  def convert(game: BattleShipGame): BattleShipProtobuf.BattleShipPlayRound.BattleFieldGame = {
    val protoGame = BattleShipProtobuf.BattleShipPlayRound.BattleFieldGame.newBuilder()
    
    protoGame.setPlayer(convert(game.player))
    protoGame.setFieldWidth(game.battleField.width)
    protoGame.setFieldHeight(game.battleField.height)

    //Add Fleet of Game instance
    val protoFleet: Set[Vessel] = game.battleField.fleet.vessels.map(e => convert(e))
    protoFleet.foreach(e => protoGame.addVessels(e))

    //Add all clicked positions of game instance
    val protoClickedPos: List[BattleShipProtobuf.BattleShipPlayRound.Position] = game.clickedPositions.map(e => convert(e))
    protoClickedPos.foreach(e => protoGame.addClickedPositions(e))

    protoGame.build()
  }

  def convert(protoGame: BattleShipProtobuf.BattleShipPlayRound.BattleFieldGame): BattleShipGame = {

    val fleet = Fleet(protoGame.getVesselsList.asScala.map(e => convert(e)).toSet)

    val field: BattleField = BattleField(protoGame.getFieldWidth,
                                         protoGame.getFieldHeight, fleet)


    val game: BattleShipGame = BattleShipGame(convert(protoGame.getPlayer),
                                              field,
                                              x => x.toDouble,
                                              x => x.toDouble,
                                              x => (),
                                              x => ())

    game.clickedPositions = protoGame.getClickedPositionsList.asScala.map(e => convert(e)).toList

    //Return game
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
    * @param vessel Vessel to convert to protobuf vessel
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
    * @param battlePos battlepos convert to protobuf positon
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


  /** Converts a Protobuf Position to a BattleShipGame BattlePos
    *
    * @param position protobuf position to convert to BattlePos
    * @return
    */
  //previous convertProtoBufPositionToBattlePos
  private def convert(position: Position): BattlePos = {
    BattlePos(position.getX, position.getY)
  }

  private def convert(player: Player): BattleShipProtobuf.BattleShipPlayRound.Player = {
    BattleShipProtobuf.BattleShipPlayRound.Player.newBuilder()
      .setName(player.name)
      .setCssStyleClass(player.cssStyleClass)
      .build()
  }

  private def convert(player: BattleShipProtobuf.BattleShipPlayRound.Player): Player = {
    Player(player.getName,player.getCssStyleClass)
  }
}
