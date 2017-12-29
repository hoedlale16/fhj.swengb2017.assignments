package at.fhj.swengb.apps.battleship.model

import java.util.{Calendar, Date}

case class BattleShipGamePlayRound(name: String,
                              games: Seq[BattleShipGame],
                              startDate: Date
                             ) {
  //Requires a name
  require(name.nonEmpty)

  //Require min 1 (Singleplayer), max 2 (Multiplayer) game instances
  require(games.nonEmpty && games.size <= 2)


  //Current battleshipGame in multiplayer mode...
  //Default(Init) => Player1
  var currentBattleShipGame: BattleShipGame = games.head

  /**
    * Return BattleShipGame of other player
    * @return
    */
  def getOtherBattleShipGame: BattleShipGame = {
    /*
    There are only ONE or TWO games in the list.
    If There are 2 games in the list and current is head, return tail
    If there is just one game, return current
     */
    if ( games.size == 1)
      currentBattleShipGame
    else {
      games.filter(p => !p.equals(currentBattleShipGame)).head
    }
  }

  /**
    * Return Game which is next according the click list.
    *
    * @return game which has to play next.
    */
  def getBattleShipGameWithShorterClicks: BattleShipGame = {
    val currGameClicks: Int = currentBattleShipGame.clickedPositions.size
    val otherGameClicks: Int = getOtherBattleShipGame.clickedPositions.size

    /*If Size of A is less of equal to B return A
    * Example:
    *    A-Clicks  B-clicks
    *       0         0          => Nobody played yet => A is next(first)
    *       1         0  [A<B]   => A played last => B is next
    *       1         1  [A==B]   => B played last => A is next
    *       2         1  [A>B]    => A played last => B is next
    *       ...
     */
    if (currGameClicks <= otherGameClicks)
      currentBattleShipGame
    else
      getOtherBattleShipGame
  }

}
