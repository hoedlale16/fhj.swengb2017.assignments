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

}
