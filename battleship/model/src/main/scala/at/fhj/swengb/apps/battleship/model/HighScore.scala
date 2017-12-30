package at.fhj.swengb.apps.battleship.model

import java.io.File
import java.nio.file.{Files, Paths}

import at.fhj.swengb.apps.battleship.{BattleShipProtobuf, BattleShipProtocol}

case class HighScore() {

  private val highScoreFile: File = new File(System.getProperty("user.home")+ "/BattleShipGame/highscore.bin")


  def addRoundToHighScore(playRound: BattleShipGamePlayRound): Unit = {
    //read alread stored information
    val oldHighScore: Seq[BattleShipGamePlayRound] = readHighScoreFromFile(highScoreFile)

    //add given round to new HighScore
    val newHighScore: Seq[BattleShipGamePlayRound] = playRound +: oldHighScore

    //store new HighScore
    writeHighScoreToFile(newHighScore,highScoreFile)

  }

  def getSortedHighScore(): Seq[BattleShipGamePlayRound] = {
    //Read unsorted Highscore
    val unsortedHighScore: Seq[BattleShipGamePlayRound] = readHighScoreFromFile(highScoreFile)

    //TODO: Sort Highscore according number of moves
    unsortedHighScore
  }

  /**
    * read already stored highscore data from protobuf file
    * @param file - Protobuf file which get parsed
    */
  private def readHighScoreFromFile(file: File): Seq[BattleShipGamePlayRound] = {

    try {
      if (file.exists()) {
        val protoHighScore: BattleShipProtobuf.HighScore = BattleShipProtobuf.HighScore.parseFrom(Files.newInputStream(Paths.get(file.getAbsolutePath)))
        BattleShipProtocol.convert(protoHighScore)
      } else
        throw new NoSuchElementException("Highscore-File not found!")

    } catch {
      case e: Exception => Seq()
    }
  }

  /**
    * Write given Highscore to given file in PROTOBUF format
    * @param highScore  Highscore to write
    * @param file - File to write to
    */
  private def writeHighScoreToFile(highScore: Seq[BattleShipGamePlayRound],file: File): Unit = {
    try {
      val protoHighScore = BattleShipProtocol.convert(highScore)

      //Create Parent path directores if they do not exist yet.
      if( ! file.exists()) {
        file.getParentFile.mkdirs()
      }

      //Write information
      protoHighScore.writeTo(Files.newOutputStream(Paths.get(file.getAbsolutePath)))
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }
}
