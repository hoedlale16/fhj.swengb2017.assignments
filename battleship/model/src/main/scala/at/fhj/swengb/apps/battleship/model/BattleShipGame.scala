package at.fhj.swengb.apps.battleship.model

/**
  * Contains all information about a battleship game.
  * @param player       => Player which plays this game
  * @param battleField  => Represebts battlefiled of this gmae
  * @param getCellWidth  => Size of Cell
  * @param getCellHeight => Size of Cell
  * @param log           => Function where to write the log
  * @param updateGUI     => Function to update GUI after Click action (e.g. update Slider for history)
  */
case class BattleShipGame(player: Player,
                          battleField: BattleField,
                          getCellWidth: Int => Double,
                          getCellHeight: Int => Double,
                          log: String => Unit,
                          updateGUI: BattleShipGame => Unit) {

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
  var clickedPositions: List[BattlePos] = List()


  var isGameOver: Boolean = false

  /**
    * We don't ever change cells, they should be initialized only once.
    */
  private val cells: Seq[BattleFxCell] = for {
    x <- 0 until battleField.width
    y <- 0 until battleField.height
    pos = BattlePos(x, y)
  } yield {
    BattleFxCell(BattlePos(x, y),
                 getCellWidth(x),
                 getCellHeight(y),
                 this,
                 log,
                 battleField.fleet.findByPos(pos),
                 updateGameState,
                 updateClickedPositions)
  }

  def getCells: Seq[BattleFxCell] = cells

  //Adds a new Position to clicked set
  def updateClickedPositions(pos: BattlePos): Unit = {
    //We keep already clicked positions awell!
    clickedPositions = pos :: clickedPositions

    //Update GUI after click. On Multiplayermode switch player/games!
    if (! isGameOver) {
      updateGUI(this)
    }
  }

  //Simulates click for all positions in list
  def simulateClicksOnClickedPositions(pos: List[BattlePos]): Unit = {

    /*
    We have to iterate to get the correct sequence.
    We are not allowed to do this:
        val relevantCells: Seq[BattleFxCell] = cells.filter(c => pos.contains(c.pos))
        relevantCells.map(e => e.handleMouseClick())
    because filter is unsorted and would destroy the sequence
     */
    for ((clickedPos) <- pos) {
      //All Cells in 'cells' are unique. So we know that there is just one and need to exception handling
      val fxCell: BattleFxCell = cells.filter(e => e.pos.equals(clickedPos)).head
      fxCell.handleMouseClick
    }
  }

  def updateGameState(vessel: Vessel, pos: BattlePos): Unit = {
    log(player.name + ": Vessel " + vessel.name.value + " was hit at position " + pos)

    if (hits.contains(vessel)) {
      val oldPos: Set[BattlePos] = hits(vessel)
      hits = hits.updated(vessel, oldPos + pos)

      if (oldPos.contains(pos)) {
        log(player.name + ": Position was triggered two times.")
      }

      if (vessel.occupiedPos == hits(vessel)) {
        log(s"Ship ${vessel.name.value} was destroyed.")
        sunkShips = sunkShips + vessel

        if (battleField.fleet.vessels == sunkShips) {
          log(player.name + ": G A M E   totally  O V E R")
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
