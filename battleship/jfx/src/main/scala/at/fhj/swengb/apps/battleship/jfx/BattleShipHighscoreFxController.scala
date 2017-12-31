package at.fhj.swengb.apps.battleship.jfx

import java.net.URL
import java.text.SimpleDateFormat
import java.util.{Date, ResourceBundle}
import javafx.beans.property.{SimpleIntegerProperty, SimpleStringProperty}
import javafx.beans.value.ObservableValue
import javafx.collections.{FXCollections, ObservableList}
import javafx.fxml.{FXML, Initializable}
import javafx.scene.control.{Button, SelectionMode, TableColumn, TableView}
import javafx.util.Callback

import at.fhj.swengb.apps.battleship.model.{BattleShipGamePlayRound, HighScore, Player}

class BattleShipHighscoreFxController extends Initializable {

  @FXML private var tbHighscore: TableView[HighScoreEntry] = _
  @FXML private var btReplay: Button = _

  @FXML private var colDate: TableColumn[HighScoreEntry, String] = _
  @FXML private var colWinner: TableColumn[HighScoreEntry, String] = _
  @FXML private var colGameName: TableColumn[HighScoreEntry, String] =_
  @FXML private var colClickAmount: TableColumn[HighScoreEntry, Int] = _


  /**
    * Go back to main Scene
    */
  @FXML def returnToMain(): Unit = {
    //Abort and switch back to main
    BattleShipFxApp.loadMainScene
  }


  @FXML def onReplayGame(): Unit = {
    //TODO: Replay for selected game
    val selectedEntry: HighScoreEntry = tbHighscore.getSelectionModel.getSelectedItem

    //Check if entry is selected
    if ( ! (selectedEntry == null)) {
      println("Replay selected game")
    }



  }

  override def initialize(location: URL, resources: ResourceBundle) = {
    //Load and prepare data
   val tableData: ObservableList[HighScoreEntry] = convertToObservableList( HighScore().getSortedHighScore())

    //Init table columns
    initTableViewColumn[String](colDate,_.formatedDate)
    initTableViewColumn[String](colWinner,_.winnerName)
    initTableViewColumn[String](colGameName,_.playroundName)
    initTableViewColumn[Int](colClickAmount,_.clickAmount)

    //Display Content
    tbHighscore.setItems(tableData)
    tbHighscore.setFixedCellSize(25)

    //Set correct selection mode
    tbHighscore.getSelectionModel.setSelectionMode(SelectionMode.SINGLE)
    tbHighscore.getSelectionModel.setCellSelectionEnabled(false)
  }

  private def convertToObservableList(highScore: Seq[BattleShipGamePlayRound]): ObservableList[HighScoreEntry] = {
    val tableData: ObservableList[HighScoreEntry] = FXCollections.observableArrayList()
    highScore.foreach(e => tableData.add(HighScoreEntry(e)))

      //Return observable list
    tableData
  }

  /**EXTERNAL HELP  FOR TABLE MODEL */
  /*Helper Source: https://github.com/rladstaetter/fx-scala-tableview/blob/master/src/main/scala/net/ladstatt/fx/tableview/TableViewApp.scala **/

  /**
    * provide a table column and a generator function for the value to put into
    * the column.
    *
    * @tparam T the type which is contained in the property
    * @return
    */
  def initTableViewColumn[T]: (TableColumn[HighScoreEntry, T], (HighScoreEntry) => Any) => Unit =
    initTableViewColumnCellValueFactory[HighScoreEntry, T]

  def initTableViewColumnCellValueFactory[HighScoreEntry, T](tc: TableColumn[HighScoreEntry, T], f: HighScoreEntry => Any): Unit = {
    tc.setCellValueFactory(mkCellValueFactory(cdf => f(cdf.getValue).asInstanceOf[ObservableValue[T]]))
  }

  private def mkCellValueFactory[HighScoreEntry, T](fn: TableColumn.CellDataFeatures[HighScoreEntry, T] => ObservableValue[T]):
       Callback[TableColumn.CellDataFeatures[HighScoreEntry, T], ObservableValue[T]] = {
    (cdf: TableColumn.CellDataFeatures[HighScoreEntry, T]) => fn(cdf)
  }
  /** END EXTERNAL HELP **/

}

class HighScoreEntry {

  val formatedDate: SimpleStringProperty = new SimpleStringProperty()
  val winnerName: SimpleStringProperty = new SimpleStringProperty()
  val playroundName: SimpleStringProperty = new SimpleStringProperty()
  val clickAmount: SimpleIntegerProperty = new SimpleIntegerProperty()

  private var playRound: BattleShipGamePlayRound = _

  def setDate(date: Date) = formatedDate.set(new SimpleDateFormat("yyyy/MM/dd").format(date))

  def setWinner(name: String) = winnerName.set(name)

  def setPlayroundName(name: String) = playroundName.set(name)

  def setClickAmount(clicks: Int) = clickAmount.set(clicks)

  def setPlayRound(roundToReplay: BattleShipGamePlayRound) = {
    playRound = roundToReplay
  }

  def getBatleShipPlayRound(): BattleShipGamePlayRound = playRound
}

object HighScoreEntry {
  def apply(playRound: BattleShipGamePlayRound): HighScoreEntry = {
    val hsEntry = new HighScoreEntry
    hsEntry.setDate(playRound.startDate)
    hsEntry.setWinner(playRound.getWinnerName)
    hsEntry.setPlayroundName(playRound.name)
    hsEntry.setClickAmount(playRound.getTotalAmountOfMoves())
    hsEntry.setPlayRound(playRound)

    hsEntry
  }
}
