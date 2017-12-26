package at.fhj.swengb.apps.battleship.model

/**
  * Contains all information about a battleship game.
  * @param battlefields  => List of Tuples represents a Player and his Battlefield
  * @param getCellWidth  => Size of Cell
  * @param getCellHeight => Size of Cell
  * @param log           => Function where to write the log
  * @param updateGUI     => Function to update GUI after Click action (e.g. update Slider for history)
  */
case class BattleShipGame(gameName: String,
                          battlefields: Map[Player,BattleField],
                          getCellWidth: Int => Double,
                          getCellHeight: Int => Double,
                          log: String => Unit,
                          updateGUI: BattleShipGame => Unit) {

  //BattleShipGame requires a name
  require( gameName.nonEmpty)

  //Allow only Singleplayer or Multiplayer(2 Player) games
  require (battlefields.nonEmpty && battlefields.size <= 2)

  /**
    * remembers which vessel was hit at which position
    * starts with the empty map, meaning that no vessel was hit yet.
    *
    **/
  var hits: Map[Vessel, Set[BattlePos]] = Map()

  /**
    * contains all vessels which are destroyed
    */
  var sunkShips: Set[Vessel] = Set()

  /**
    * Contains all already clicked positions.
    * Array keeps sorting...
    */
  var clickedPositions: List[(Player,BattlePos)] = List()

  //All available players
  var players: List[Player] = battlefields.keys.toList

  //Initial: Start with player A
  var currentPlayer: Player = players.head

  //Returns current battlefield
  var currentBattleField: BattleField = {
    battlefields.get(currentPlayer) match {
      case Some(field) => field
      case None => {
        log("FATAL ERROR - Player not found!")
        null; //If this happens, some crazy shit is going on...
      }
    }
  }


  var isGameOver: Boolean = false

  /**
    * We don't ever change cells, they should be initialized only once.
    */
  private val cells: Seq[BattleFxCell] = for {
    x <- 0 until currentBattleField.width
    y <- 0 until currentBattleField.height
    pos = BattlePos(x, y)
  } yield {
    BattleFxCell(BattlePos(x, y),
                 getCellWidth(x),
                 getCellHeight(y),
                 log,
                 currentBattleField.fleet.findByPos(pos),
                 updateGameState,
                 updateClickedPositions)
  }

  def getCells: Seq[BattleFxCell] = cells

  //Adds a new Position to clicked set
  def updateClickedPositions(pos: BattlePos): Unit = {
    //We keep already clicked positions awell!
    clickedPositions = (currentPlayer,pos) :: clickedPositions

    //Switch player on Multiplayermode when game not finished yet!
    if (battlefields.size > 1 && ! isGameOver) {
      currentPlayer = battlefields.keys.filter(e => !e.eq(currentPlayer)).head
      //TODO: Change Battlefield aswell
    }

    //Update GUI after click as well
    updateGUI(this)
  }

  //Simulates click for all positions in list
  def simulateClicksOnClickedPositions(pos: List[(Player,BattlePos)]): Unit = {

    /*
    We have to iterate to get the correct sequence.
    We are not allowed to do this:
        val relevantCells: Seq[BattleFxCell] = cells.filter(c => pos.contains(c.pos))
        relevantCells.map(e => e.handleMouseClick())
    because filter is unsorted and would destroy the sequence
     */
    for ((player,clickedPos) <- pos) {
      //Set current player
      currentPlayer = player

      //All Cells in 'cells' are unique. So we know that there is just one and need to exception handling
      val fxCell: BattleFxCell = cells.filter(e => e.pos.equals(clickedPos)).head
      fxCell.handleMouseClick
    }
  }

  def updateGameState(vessel: Vessel, pos: BattlePos): Unit = {
    log("Vessel " + vessel.name.value + " was hit at position " + pos)

    if (hits.contains(vessel)) {
      val oldPos: Set[BattlePos] = hits(vessel)
      hits = hits.updated(vessel, oldPos + pos)

      if (oldPos.contains(pos)) {
        log("Position was triggered two times.")
      }

      if (vessel.occupiedPos == hits(vessel)) {
        log(s"Ship ${vessel.name.value} was destroyed.")
        sunkShips = sunkShips + vessel

        if (currentBattleField.fleet.vessels == sunkShips) {
          log("G A M E   totally  O V E R")
          isGameOver = true
          updateGUI(this)
        }
      }

    } else {
      // vessel is not part of the map
      // but vessel was hit!
      // it was hit the first time ever!
      hits = hits.updated(vessel, Set(pos))
    }

  }

}
