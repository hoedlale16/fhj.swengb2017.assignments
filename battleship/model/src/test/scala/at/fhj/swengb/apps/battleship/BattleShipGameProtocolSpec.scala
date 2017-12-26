package at.fhj.swengb.apps.battleship

import at.fhj.swengb.apps.battleship.model._
import org.scalacheck.Prop
import org.scalatest.WordSpecLike
import org.scalatest.prop.Checkers

class BattleShipProtocolSpec extends WordSpecLike {

  import at.fhj.swengb.apps.battleship.model.BattleshipGameGen._

  "BattleShipProtocol" should {
    "be deserializable" in {
      Checkers.check(Prop.forAll(battleShipGameGen) {
        expected: BattleShipGame =>
          {
            val actual =
              BattleShipProtocol.convert(BattleShipProtocol.convert(expected))
            //Make no sense to declare global functions just to test whole BattleShipGame object.
            actual.gameName == expected.gameName
            actual.battleFieldA == expected.battleFieldA
            actual.battleFieldB == expected.battleFieldB
            actual.clickedPositions == expected.clickedPositions
          }
      })
    }
  }
}
