package at.fhj.swengb.apps.battleship.model

import org.scalacheck.Gen

import scala.collection.JavaConverters._

/**
  * Implement in the same manner like MazeGen from the lab, adapt it to requirements of BattleShip
  */
object BattleshipGameGen {

  val maxWidth: Int = 10
  val maxHeight: Int = 10
  val fleetConfigSeq: Seq[FleetConfig] =
    Seq(FleetConfig.OneShip, FleetConfig.TwoShips, FleetConfig.Standard)

  val players: Seq[Player] = Seq(Player("PlayerA","bg_playerA"),Player("PlayerB","bg_playerB"))

  //Generate a random battlefield
  val battlefieldGen: Gen[(Player,BattleField)] = for {
    width <- Gen.chooseNum[Int](1, maxWidth)
    height <- Gen.chooseNum[Int](1, maxHeight)
    x <- Gen.chooseNum[Int](0, fleetConfigSeq.size - 1)
    player <- Gen.chooseNum[Int](0, players.size -1)
  } yield (players(player),BattleField(width, height, Fleet(fleetConfigSeq(x))))


  //Simulate a random set of already clicked battle positions
  val clickedPosGen: Gen[List[(Player,BattlePos)]] = for {
    i <- Gen.chooseNum[Int](0, maxWidth * maxHeight)
    x <- Gen.chooseNum[Int](0, maxWidth - 1)
    y <- Gen.chooseNum[Int](0, maxHeight - 1)
    player <- Gen.chooseNum[Int](0, players.size -1)
  } yield {
    List.fill(i)( (players(player),BattlePos(x, y)) )
  }

  //Generate a random BattleShipGame
  val battleShipGameGen: Gen[BattleShipGame] = for {
    battlefields <- battlefieldGen
    clickedPos <- clickedPosGen
  } yield {
    val game = BattleShipGame("Unit-Test game",
                              Map(battlefields),
                              (x => x.toDouble),
                              (x => x.toDouble),
                              (x => ()),
                              (x => ()))
    game.clickedPositions = clickedPos
    game
  }
}
