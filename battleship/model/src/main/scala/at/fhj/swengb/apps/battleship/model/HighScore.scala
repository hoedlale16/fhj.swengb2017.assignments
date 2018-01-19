package at.fhj.swengb.apps.battleship.model

import java.io.File
import java.nio.file.{Files, Paths}

import at.fhj.swengb.apps.battleship.{BattleShipProtobuf, BattleShipProtocol}

case class HighScore() {

  private val highScoreFile: File = new File(System.getProperty("user.home") + "/BattleShipGame/highscore.bin")


  def addRoundToHighScore(playRound: BattleShipGamePlayRound): Unit = {
    //read alread stored information
    val oldHighScore: Seq[BattleShipGamePlayRound] = readHighScoreFromFile(highScoreFile)

    //add given round to new HighScore
    val newHighScore: Seq[BattleShipGamePlayRound] = playRound +: oldHighScore

    //store new HighScore
    writeHighScoreToFile(newHighScore, highScoreFile)

  }

  /**
    * Write given Highscore to given file in PROTOBUF format
    *
    * @param highScore Highscore to write
    * @param file      - File to write to
    */
  private def writeHighScoreToFile(highScore: Seq[BattleShipGamePlayRound], file: File): Unit = {
    try {
      val protoHighScore = BattleShipProtocol.convert(highScore)

      //Create Parent path directores if they do not exist yet.
      if (!file.exists()) {
        file.getParentFile.mkdirs()
      }

      //Write information
      protoHighScore.writeTo(Files.newOutputStream(Paths.get(file.getAbsolutePath)))
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }

  /**
    * read already stored highscore data from protobuf file
    *
    * @param file - Protobuf file which get parsed
    */
  private def readHighScoreFromFile(file: File): Seq[BattleShipGamePlayRound] = {

    try {
      if (file.exists()) {
        val protoHighScore: BattleShipProtobuf.HighScore = BattleShipProtobuf.HighScore.parseFrom(Files.newInputStream(Paths.get(file.getAbsolutePath)))
        BattleShipProtocol.convert(protoHighScore)
      } else
        throw new NoSuchElementException("ERROR: Highscore-File not found!")

    } catch {
      case e: Exception => Seq()
    }
  }

  def clearHighscore(): Unit = {
    //store new empty HighScore
    writeHighScoreToFile(Seq(), highScoreFile)
  }

  def getSortedHighScore: Seq[BattleShipGamePlayRound] = {
    //Read unsorted Highscore
    val unsortedHighScore: Seq[BattleShipGamePlayRound] = readHighScoreFromFile(highScoreFile)

    def mergeSort(list: List[BattleShipGamePlayRound]): List[BattleShipGamePlayRound] = {

      /**
        * Helper to sort
        *
        * @param left  left half to sort
        * @param right right half to sort
        * @return a sorted list combinend of left and right
        */
      def merge(left: List[BattleShipGamePlayRound], right: List[BattleShipGamePlayRound]): List[BattleShipGamePlayRound] =
        (left, right) match {
          case (_, Nil) => left
          case (Nil, _) => right
          case (leftHead :: leftTail, rightHead :: rightTail) =>
            //Sort according Moves
            if (leftHead.getTotalAmountOfMoves >= rightHead.getTotalAmountOfMoves)
              leftHead +: merge(leftTail, right)
            else
              rightHead +: merge(left, rightTail)
        }

      //Split given list and sort with merge-sort algorithm
      list.size match {
        case 0 => list //List with no elements
        case 1 => list //List with one element
        case _ =>
          val (left, right) = list.splitAt(list.size / 2)
          merge(mergeSort(left), mergeSort(right))
      }
    }

    //Return sorted list
    val sortedHighScore: Seq[BattleShipGamePlayRound] = mergeSort(unsortedHighScore.toList)
    sortedHighScore
  }
}



