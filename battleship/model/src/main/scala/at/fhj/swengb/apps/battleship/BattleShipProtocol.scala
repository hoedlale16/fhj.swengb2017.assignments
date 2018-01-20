package at.fhj.swengb.apps.battleship

import java.text.SimpleDateFormat
import java.util.Date

import at.fhj.swengb.apps.battleship.BattleShipProtobuf.BattleShipPlayRound.{Position, Vessel, VesselOrientation}
import at.fhj.swengb.apps.battleship.BattleShipProtobuf.HighScore
import at.fhj.swengb.apps.battleship.model._

import scala.collection.JavaConverters._

object BattleShipProtocol {

  def convert(protoHighScore: HighScore): Seq[BattleShipGamePlayRound] = {
    val highScore: Seq[BattleShipGamePlayRound] = protoHighScore.getPlayedPlayRoundsList.asScala.map(e => convert(e))
    highScore
  }

  def convert(protoPlayGround: BattleShipProtobuf.BattleShipPlayRound): BattleShipGamePlayRound = {

    //Create all Battlefields stored (at least one)
    val battleFieldGames: Seq[BattleShipGame] = protoPlayGround.getBattlefieldsGamesList.asScala.map(e => convert(e)).toList

    //Read BattleShipGameName from Protobuf!

    val gameName: String = protoPlayGround.getGameName

    //If change on format is required change reverse-convert function as well!!
    val startDate: Date = new SimpleDateFormat("yyyy/MM/dd").parse(protoPlayGround.getStartdate)

    //Create BattleshipGame and set aready clicked positions
    val playRound = BattleShipGamePlayRound(gameName,
      battleFieldGames, startDate)

    //Set Winner of round if set (Highscore data)
    if (protoPlayGround.hasWinner) {
      playRound.setWinner(convert(protoPlayGround.getWinner))
    }

    //return game
    playRound
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
      x => (),
      null)

    game.clickedPositions = protoGame.getClickedPositionsList.asScala.map(e => convert(e)).toList

    //Return game
    game
  }

  /**
    * Convert a Protobuf Vessel to a BattleShipGame Vessel
    *
    * @param vessel Vessel to convert to protobuf vessel
    * @return
    */
  //Previously convertProtobufVesseltoVessel
  def convert(vessel: Vessel): at.fhj.swengb.apps.battleship.model.Vessel = {

    val name: NonEmptyString = NonEmptyString(vessel.getName)
    val startPos: BattlePos =
      BattlePos(vessel.getStartPos.getX, vessel.getStartPos.getY)
    val size = vessel.getSize
    val direction: Direction = vessel.getOrientation match {
      case VesselOrientation.Horizontal => Horizontal
      case VesselOrientation.Vertical => Vertical
      case _ => ??? //When this happens, some crazy shit is going on…
    }

    //Create Battleship Vessel and return it.
    at.fhj.swengb.apps.battleship.model.Vessel(name, startPos, direction, size)
  }

  /** Converts a ProtobufPosition to a BattleShipGame BattlePos
    *
    * @param position protobuf position to convert to BattlePos
    * @return
    */
  //previously convertProtoBufPositionToBattlePos
  private def convert(position: Position): BattlePos = {
    BattlePos(position.getX, position.getY)
  }

  private def convert(player: BattleShipProtobuf.BattleShipPlayRound.Player): Player = {
    Player(player.getName, player.getCssStyleClass)
  }

  def convert(highScore: Seq[BattleShipGamePlayRound]): BattleShipProtobuf.HighScore = {

    //Convert all highscore-entries
    val protoHighScoreEntries: Seq[BattleShipProtobuf.BattleShipPlayRound] = highScore.map(e => convert(e))

    //Add Entries to protHighScore
    val protoHighScore: HighScore.Builder = protoHighScoreEntries.foldLeft(HighScore.newBuilder())((acc, e) => acc.addPlayedPlayRounds(e))
    protoHighScore.build()
  }

  def convert(g: BattleShipGamePlayRound): BattleShipProtobuf.BattleShipPlayRound = {
    //Create Protobuf-Battlefield
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

    //Set winner if set. (HighScore)
    if (g.getWinner.isDefined) {
      protoPlayRound.setWinner(convert(g.getWinner.get))
    }

    //Build battlefield and write to file
    protoPlayRound.build()
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

  /**
    * Convertes a given Battleship-Game Vessel to a Protobuf-Vessel for storage
    *
    * @param vessel Vessel from BattleShipGame which become converted
    * @return
    */
  // Previous convertVesseltoProtobufVessel
  def convert(vessel: at.fhj.swengb.apps.battleship.model.Vessel): Vessel = {

    val vesselOrientation = {
      vessel.direction match {
        case Horizontal => VesselOrientation.Horizontal;
        case Vertical => VesselOrientation.Vertical;
        case _ => ??? /*When this happens, some crazy shit is going on… */
      }
    }

    //Create new protobuf-Vessel
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
    * Generates a Protobuf-Position from a given BattlePos-position
    *
    * @param battlePos battlepos convert to protobuf position
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

  private def convert(player: Player): BattleShipProtobuf.BattleShipPlayRound.Player = {
    BattleShipProtobuf.BattleShipPlayRound.Player.newBuilder()
      .setName(player.name)
      .setCssStyleClass(player.cssStyleClass)
      .build()
  }
}
