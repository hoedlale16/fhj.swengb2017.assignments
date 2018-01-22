package at.fhj.swengb.apps.battleship.model

import java.io.{File, InputStream}
import java.nio.file.{Files, Paths}
import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

import at.fhj.swengb.apps.battleship.{BattleShipProtobuf, BattleShipProtocol}

import scala.util.Random

object BattleShipGamePlayRound {

  /**
    * Create A BattleShipPlayRound for Singleplayer-Mode
    *
    * @param player               - Player to create play round for
    * @param getCellWidth         - function which defines width of cell
    * @param getCellHeight        - function which defines height of cell
    * @param log                  - function to write log
    * @param updateGUIAfterAction - function which get called after click on a cell
    * @param jukeBox              - Instance which plays music during game
    * @param fleetConfig          - Used Fleetconfig for game
    * @return a singleplayerMode Play round
    */
  def apply(player: Player,
            getCellWidth: Int => Double,
            getCellHeight: Int => Double,
            log: String => Unit,
            updateGUIAfterAction: BattleShipGame => Unit,
            jukeBox: BattleShipJukeBox,
            fleetConfig: FleetConfig): BattleShipGamePlayRound = {
    BattleShipGamePlayRound(
      createRandomPlayRoundName(),
      Seq(createBattleShipGame(player, getCellWidth, getCellHeight, log, updateGUIAfterAction, jukeBox, fleetConfig)),
      Calendar.getInstance.getTime)
  }

  /**
    * * Creates a new BattleShip game for given user
    *
    * @param player               - Create game for given player
    * @param getCellWidth         - Function which defines the width of each cell
    * @param getCellHeight        - Function which defines the height of each cell
    * @param log                  - Function to write log
    * @param updateGUIAfterAction - Function which get called after click
    * @return a new BattleShipGame
    */
  private def createBattleShipGame(player: Player,
                                   getCellWidth: Int => Double,
                                   getCellHeight: Int => Double,
                                   log: String => Unit,
                                   updateGUIAfterAction: BattleShipGame => Unit,
                                   jukeBox: BattleShipJukeBox,
                                   fleetConfig: FleetConfig): BattleShipGame = {

    val battlefield: BattleField =
      BattleField(10, 10, Fleet(fleetConfig))

    BattleShipGame(player,
      BattleField.placeRandomly(battlefield),
      getCellWidth,
      getCellHeight,
      log,
      updateGUIAfterAction,
      jukeBox)
  }

  /**
    * Create A BattleShipPlayRound for Multiplayer-Mode
    *
    * @param playerA              - First player to create play round for
    * @param playerB              - Second player to create play round for
    * @param battlefieldPlayerA   - Battlefield of player A - Field which plays player B
    * @param battlefieldPlayerB   - Battlefield of player B - Field which plays player A
    * @param getCellWidth         - function which defines width of cell
    * @param getCellHeight        - function which defines height of cell
    * @param log                  - function to write log
    * @param updateGUIAfterAction - function which get called after click on a cell
    * @param jukeBox              - Instance which plays music during game
    * @return a multiplayermode Play round
    */
  def apply(playerA: Player,
            battlefieldPlayerA: BattleField,
            playerB: Player,
            battlefieldPlayerB: BattleField,
            getCellWidth: Int => Double,
            getCellHeight: Int => Double,
            log: String => Unit,
            updateGUIAfterAction: BattleShipGame => Unit,
            jukeBox: BattleShipJukeBox): BattleShipGamePlayRound = {

    //Create Games for each player. Battlefield is field of enemy
    val gamePlayerA: BattleShipGame = createBattleShipGame(playerA, battlefieldPlayerB, getCellWidth, getCellHeight, log, updateGUIAfterAction, jukeBox)
    val gamePlayerB: BattleShipGame = createBattleShipGame(playerB, battlefieldPlayerA, getCellWidth, getCellHeight, log, updateGUIAfterAction, jukeBox)

    BattleShipGamePlayRound(
      createRandomPlayRoundName(),
      Seq(gamePlayerA, gamePlayerB),
      Calendar.getInstance.getTime)
  }

  /**
    * Creates a random Name for a new created battleship gamePlayround according feature "Naming of battles"
    * Name is build from 4 lists where words get randomly choosen.
    *
    * @return Random generated Name for a new battleshipName
    */
  private def createRandomPlayRoundName(): String = {
    val w1: Seq[String] = Seq("The", "Holy", "Deadly", "Epic", "Peaceful")
    val w2: Seq[String] = Seq("battle", "fight", "encounter", "conflict", "war", "crusade", "board examination")
    val w3: Seq[String] = Seq("of", "from", "since", "for", "at", "in", "against")
    val w4: Seq[String] = Seq("Graz", "Eggenberg", "1908", "FH Joanneum", "Kapfenberg", "London", "1997", "1492")

    val rGen: Random = new Random()

    val name: String = w1(rGen.nextInt(w1.size - 1)) + " " +
      w2(rGen.nextInt(w2.size - 1)) + " " +
      w3(rGen.nextInt(w3.size - 1)) + " " +
      w4(rGen.nextInt(w4.size - 1))

    name
  }

  /**
    * * Creates a new BattleShip game for given user
    *
    * @param player               - Create game for given player
    * @param battlefield          - Battlefield for this game.
    * @param getCellWidth         - Function which defines the width of each cell
    * @param getCellHeight        - Function which defines the height of each cell
    * @param log                  - Function to write log
    * @param updateGUIAfterAction - Function which get called after click
    * @return a new BattleShipGame
    */
  private def createBattleShipGame(player: Player,
                                   battlefield: BattleField,
                                   getCellWidth: Int => Double,
                                   getCellHeight: Int => Double,
                                   log: String => Unit,
                                   updateGUIAfterAction: BattleShipGame => Unit,
                                   jukeBox: BattleShipJukeBox): BattleShipGame = {
    BattleShipGame(player,
      battlefield,
      getCellWidth,
      getCellHeight,
      log,
      updateGUIAfterAction,
      jukeBox)
  }

  /**
    * Create BattleShipGamePlayRound from given protobuf file (load functionality)
    *
    * @param file                 -File to read play round from
    * @param parse                - Function used to parse given file
    * @param getCellWidth         - function which defines width of cell
    * @param getCellHeight        - function which defines height of cell
    * @param log                  - function to write log
    * @param updateGUIAfterAction - function which get called after click on a cell
    * @return a loaded Play round
    */
  def apply(file: File,
            parse: InputStream => BattleShipProtobuf.BattleShipPlayRound,
            getCellWidth: Int => Double,
            getCellHeight: Int => Double,
            log: String => Unit,
            updateGUIAfterAction: BattleShipGame => Unit,
            jukeBox: BattleShipJukeBox,
            unused: Int => Int): BattleShipGamePlayRound = {

    //Read Protobuf-Object and convert it to a BattleShipGamePlayRound-Instance
    val protoBattleShipGamePlayRound: BattleShipProtobuf.BattleShipPlayRound = parse(Files.newInputStream(Paths.get(file.getAbsolutePath)))
    val loadedBattleShipGamePlayRound: BattleShipGamePlayRound = BattleShipProtocol.convert(protoBattleShipGamePlayRound)

    //Initialize all games (Single/Multiplayer mode)
    val games: Seq[BattleShipGame] =
      loadedBattleShipGamePlayRound.games.map(e => createBattleShipGame(e, getCellWidth, getCellHeight, log, updateGUIAfterAction, jukeBox))

    //Create new gamePlayround-Event based on loaded Data
    BattleShipGamePlayRound(loadedBattleShipGamePlayRound.name, games, loadedBattleShipGamePlayRound.startDate)
  }

  def apply(highScorePlayround: BattleShipGamePlayRound,
            getCellWidth: Int => Double,
            getCellHeight: Int => Double,
            log: String => Unit,
            updateGUIAfterAction: BattleShipGame => Unit,
            jukeBox: BattleShipJukeBox): BattleShipGamePlayRound = {


    val games: Seq[BattleShipGame] = highScorePlayround.games.map(g => createBattleShipGame(g, getCellWidth, getCellHeight, log, updateGUIAfterAction, jukeBox))

    val newPR = BattleShipGamePlayRound(highScorePlayround.name, games, highScorePlayround.startDate)
    newPR.winner = highScorePlayround.winner
    newPR
  }

  /**
    * Creates a new BattleShipGame but take player and field information from given game.(loading functionality).
    *
    * @param loadedGame           - game where battlefield and player information is taken from
    * @param getCellWidth         - Function which defines the width of each cell
    * @param getCellHeight        - Function which defines the height of each cell
    * @param log                  - Function to write log
    * @param updateGUIAfterAction - Function which get called after click
    * @return new initialized BattleShipGame
    */
  private def createBattleShipGame(loadedGame: BattleShipGame,
                                   getCellWidth: Int => Double,
                                   getCellHeight: Int => Double,
                                   log: String => Unit,
                                   updateGUIAfterAction: BattleShipGame => Unit,
                                   jukeBox: BattleShipJukeBox): BattleShipGame = {
    val newGame: BattleShipGame = BattleShipGame(loadedGame.player,
      loadedGame.battleField,
      getCellWidth,
      getCellHeight,
      log,
      updateGUIAfterAction,
      jukeBox)

    newGame.clickedPositions = loadedGame.clickedPositions
    newGame
  }
}

case class BattleShipGamePlayRound(name: String,
                                   games: Seq[BattleShipGame],
                                   startDate: Date
                                  ) {
  //Requires a name
  require(name.nonEmpty)

  //Require min 1 (Singleplayer), max 2 (Multiplayer) game instances
  require(games.nonEmpty && games.lengthCompare(2) <= 0)


  //Current battleshipGame in multiplayer mode...
  //Default(Init) => Player1
  var currentBattleShipGame: BattleShipGame = games.head
  //Holds winner of play round
  private var winner: Player = _

  //Return total amount of moves in play round
  def getTotalAmountOfMoves: Int = games.foldLeft(0)((acc, game) => acc + game.clickedPositions.size)

  def getMergedClickedPositions: Seq[BattlePos] = {
    if (games.lengthCompare(1) == 0)
      games.head.clickedPositions
    else {
      var clicksGame1: Seq[BattlePos] = games.head.clickedPositions
      var clicksGame2: Seq[BattlePos] = games.last.clickedPositions

      var mergedList: Seq[BattlePos] = Seq()
      var takeFromFirst: Boolean = true

      def takeHeadAndAddtoMergeList(list: Seq[BattlePos]): Seq[BattlePos] =
        list match {
          case Nil => Nil
          case head :: Nil =>
            mergedList = head +: mergedList
            Nil
          case head :: tail =>
            mergedList = head +: mergedList
            tail
        }

      while (clicksGame1.isEmpty && clicksGame2.isEmpty) {
        if (takeFromFirst) {
          clicksGame1 = takeHeadAndAddtoMergeList(clicksGame1)
          takeFromFirst = false
        } else {
          clicksGame2 = takeHeadAndAddtoMergeList(clicksGame2)
          takeFromFirst = true
        }
      }

      //Return lists
      mergedList
    }
  }

  def getWinnerName: String = {
    getWinner match {
      case None => "???"
      case Some(w) => w.name
    }
  }

  def getWinner: Option[Player] = Option(winner)

  /**
    * Set given player as winner of the game
    *
    * @param player
    */
  def setWinner(player: Player): Unit = winner = player

  def getFormatedStartDate: String = {
    new SimpleDateFormat("yyyy/MM/dd").format(startDate)
  }


  def getPlayRoundName: String = name

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

  /**
    * Return BattleShipGame of other player
    *
    * @return
    */
  def getOtherBattleShipGame: BattleShipGame = {
    /*
    There are only ONE or TWO games in the list.
    If There are 2 games in the list and current is head, return tail
    If there is just one game, return current
     */
    if (games.lengthCompare(1) == 0)
      currentBattleShipGame
    else {
      games.filter(p => !p.equals(currentBattleShipGame)).head
    }
  }

}
