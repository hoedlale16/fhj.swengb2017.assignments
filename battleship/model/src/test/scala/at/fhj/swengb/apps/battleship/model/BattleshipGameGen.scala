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


  val battleShipGameGen: Gen[BattleShipGame] = for {
    battlefield <- battlefieldGen
  } yield {
    BattleShipGame(battlefield, ( x => x.toDouble), ( x => x.toDouble), (x => println(x)))
  }
}
