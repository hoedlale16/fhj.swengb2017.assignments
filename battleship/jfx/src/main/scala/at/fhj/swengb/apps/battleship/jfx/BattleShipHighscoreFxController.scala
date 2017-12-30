package at.fhj.swengb.apps.battleship.jfx

import java.net.URL
import java.text.SimpleDateFormat
import java.util.ResourceBundle
import javafx.fxml.{FXML, FXMLLoader, Initializable}
import javafx.scene.Parent
import javafx.scene.control.TableView
import javafx.stage.{Modality, Stage}

import at.fhj.swengb.apps.battleship.model.{BattleShipGamePlayRound, HighScore}

class BattleShipHighscoreFxController extends Initializable {

  @FXML private var tbHighscore: TableView[BattleShipGamePlayRound] = _

  override def initialize(location: URL, resources: ResourceBundle) = {
    //TODO Display Highscore
    val highscore: Seq[BattleShipGamePlayRound] = HighScore().getSortedHighScore()

    val outHighScore: Seq[String] = highscore.map(e => formatOutput(e))

    println("HighScore")
    outHighScore.foreach(e => println(e))

  }

  private def formatOutput(playRound: BattleShipGamePlayRound): String = {

    val startDate: String = new SimpleDateFormat("yyyy/MM/dd").format(playRound.startDate)
    val winner: String = playRound.getWinner match {
      case None => "???"
      case Some(w) => w.name
    }

    startDate + "\t\t" + winner + "\t\t\t" + playRound.name + "\t\t" + playRound.getTotalAmountOfMoves()
  }

  /**
    * Go back to main Scene
    */
  @FXML def returnToMain(): Unit = {
    //Abort and switch back to main
    BattleShipFxApp.loadMainScene
  }

}
