package at.fhj.swengb.apps.battleship.model

import java.util.Calendar

import org.scalacheck.Gen

/**
  * Implement in the same manner like MazeGen from the lab, adapt it to requirements of BattleShip
  */
object BattleshipGamePlayGroundGen {

  val maxWidth: Int = 10
  val maxHeight: Int = 10
  val fleetConfigSeq: Seq[FleetConfig] =
    Seq(FleetConfig.OneShip, FleetConfig.TwoShips, FleetConfig.Standard)

  val players: Seq[Player] = Seq(Player("PlayerA", "bg_playerA"), Player("PlayerB", "bg_playerB"))

  //Generate a random battlefield
  val battlefieldGen: Gen[BattleField] = for {
    width <- Gen.chooseNum[Int](1, maxWidth)
    height <- Gen.chooseNum[Int](1, maxHeight)
    x <- Gen.chooseNum[Int](0, fleetConfigSeq.size - 1)
  } yield BattleField(width, height, Fleet(fleetConfigSeq(x)))


  //Simulate a random set of already clicked battle positions
  val clickedPosGen: Gen[List[(BattlePos)]] = for {
    i <- Gen.chooseNum[Int](0, maxWidth * maxHeight)
    x <- Gen.chooseNum[Int](0, maxWidth - 1)
    y <- Gen.chooseNum[Int](0, maxHeight - 1)
    player <- Gen.chooseNum[Int](0, players.size - 1)
  } yield {
    List.fill(i)(BattlePos(x, y))
  }

  //Generate a random BattleShipGame
  val battleShipGameGen: Gen[BattleShipGame] = for {
    player <- Gen.chooseNum[Int](0, players.size - 1)
    battlefield <- battlefieldGen
    clickedPos <- clickedPosGen
  } yield {
    val game = BattleShipGame(players(player), battlefield,
      x => x.toDouble,
      x => x.toDouble,
      x => (),
      x => (),
      null)
    game.clickedPositions = clickedPos
    game
  }

  //Generates a random Game Play ground
  val battleShipGamePlayRound: Gen[BattleShipGamePlayRound] = for {
    games <- battleShipGameGen
    i <- Gen.chooseNum[Int](1, 2)
  } yield {
    BattleShipGamePlayRound("Unit-Test", List.fill(i)(games), Calendar.getInstance().getTime)
  }
}
