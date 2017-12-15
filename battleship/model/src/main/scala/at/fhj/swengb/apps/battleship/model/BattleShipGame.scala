package at.fhj.swengb.apps.battleship.model

/**
  * Contains all information about a battleship game.
  */
case class BattleShipGame(battleField: BattleField,
                          getCellWidth: Int => Double,
                          getCellHeight: Int => Double,
                          log: String => Unit) {

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
  var clickedPositions: Seq[BattlePos] = Seq();

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
                 log,
                 battleField.fleet.findByPos(pos),
                 updateGameState,
                 updateClickedPositions)
  }

  def getCells(): Seq[BattleFxCell] = cells

  //Adds a new Position to clicked set
  def updateClickedPositions(pos: BattlePos): Unit = {
    //We keep already clicked positions awell!
    clickedPositions = pos +: clickedPositions
  }

  //Simulates click for all positions in list
  def simulateClicksOnClickedPositions(pos: Seq[BattlePos]): Unit = {

    //This are all affected Cells
    val relevantCells: Seq[BattleFxCell] =
      cells.filter(c => pos.contains(c.pos))

    //Simulate Mouseclick
    relevantCells.map(e => e.handleMouseClick())

  }

  def updateGameState(vessel: Vessel, pos: BattlePos): Unit = {
    log("Vessel " + vessel.name.value + " was hit at position " + pos)

    if (hits.contains(vessel)) {
      // this code is executed if vessel was already hit at least once

      // pos
      // vessel
      // map (hits)

      // we want to update the hits map
      // the map should be updated if
      // we hit a vessel which is already contained
      // in the 'hits' map, and it's values (
      // the set of BattlePos) should be added
      // the current pos
      val oldPos: Set[BattlePos] = hits(vessel)

      hits = hits.updated(vessel, oldPos + pos)

      hits(vessel).foreach(p => log(p.toString))

      if (oldPos.contains(pos)) {
        log("Position was triggered two times.")
      }

      if (vessel.occupiedPos == hits(vessel)) {
        log(s"Ship ${vessel.name.value} was destroyed.")
        sunkShips = sunkShips + vessel

        if (battleField.fleet.vessels == sunkShips) {
          log("G A M E   totally  O V E R")
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
