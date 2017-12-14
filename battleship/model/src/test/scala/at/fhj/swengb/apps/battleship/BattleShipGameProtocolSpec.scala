package at.fhj.swengb.apps.battleship

import at.fhj.swengb.apps.battleship.model.BattleShipGame
import org.scalacheck.{Gen, Prop}
import org.scalatest.WordSpecLike
import org.scalatest.prop.Checkers


class BattleShipGameProtocolSpec extends WordSpecLike {


  val battleShipGameGen: Gen[BattleShipGame] = for {
    battleField <- battleFieldGen
  } yield BattleShipGame(battleField, i => i.toDouble, i => i.toDouble, println)

  "BattleShipGameProtocol" should {
    "be deserializable" in {
      Checkers.check(Prop.forAll(battleShipGameGen) {
        expected: BattleShipGame => {
          val actual = BattleShipProtocol.convert(BattleShipProtocol.convert(expected))
          actual == expected
        }
      })
    }
  }
}
