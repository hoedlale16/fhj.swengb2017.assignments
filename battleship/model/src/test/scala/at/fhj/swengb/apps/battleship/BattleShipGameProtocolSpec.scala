package at.fhj.swengb.apps.battleship

import java.text.SimpleDateFormat

import at.fhj.swengb.apps.battleship.model._
import org.scalacheck.Prop
import org.scalatest.WordSpecLike
import org.scalatest.prop.Checkers

class BattleShipGameProtocolSpec extends WordSpecLike {

  import at.fhj.swengb.apps.battleship.model.BattleshipGamePlayGroundGen._

  "BattleShipProtocol" should {
    "be deserializable" in {
      Checkers.check(Prop.forAll(battleShipGamePlayRound) {
        expected: BattleShipGamePlayRound => {
          val actual =
            BattleShipProtocol.convert(BattleShipProtocol.convert(expected))
          //It makes no sense to declare global functions just to test whole BattleShipGame object.
          actual.name == expected.name
          actual.games == expected.games
          (new SimpleDateFormat("yyyy/MM/dd")).format(actual.startDate) == (new SimpleDateFormat("yyyy/MM/dd")).format(expected.startDate)
        }
      })
    }
  }
}
