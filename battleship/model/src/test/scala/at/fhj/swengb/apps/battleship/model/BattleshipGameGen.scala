package at.fhj.swengb.apps.battleship.model

import org.scalacheck.Gen


/**
  * Implement in the same manner like MazeGen from the lab, adapt it to requirements of BattleShip
  */
object BattleshipGameGen {

  val maxWidth: Int = 10
  val maxHeight: Int = 10
  val fleetConfigSeq: Seq[FleetConfig] = Seq(FleetConfig.OneShip, FleetConfig.TwoShips, FleetConfig.Standard)


  //Generate a random fleet for Battleship
  val fleetGen: Gen[Fleet] = for {
    x <- Gen.chooseNum[Int](0, fleetConfigSeq.size - 1)
  } yield Fleet(fleetConfigSeq(x))

  //Generate a random battlefield
  val battlefieldGen: Gen[BattleField] = for {
    width <- Gen.chooseNum[Int](1, maxWidth)
    height <- Gen.chooseNum[Int](1, maxHeight)
    fleet <- fleetGen
  } yield BattleField(width, height, fleet)

  //Generate a random BattlePositon
  val battlePosGen: Gen[BattlePos] = for {
    x <- Gen.chooseNum[Int](0, maxWidth)
    y <- Gen.chooseNum[Int](0, maxHeight)
  } yield BattlePos(x,y)

  //Simulate a random set of already clicked battle positions
  val clickedPosGen: Gen[Set[BattlePos]] = for {
    i <- Gen.chooseNum[Int](0,maxWidth*maxHeight)
    pos <- battlePosGen
  } yield {
    Seq.fill(i)(pos).toSet
  }

  //Generate a random BattleShipGame
  val battleShipGameGen: Gen[BattleShipGame] = for {
    battlefield <- battlefieldGen
    clickedPos <- clickedPosGen
  } yield {
    val game = BattleShipGame(battlefield, ( x => x.toDouble), ( x => x.toDouble), (x => println(x)))
    game.clickedPositions = clickedPos
    game
  }
}
