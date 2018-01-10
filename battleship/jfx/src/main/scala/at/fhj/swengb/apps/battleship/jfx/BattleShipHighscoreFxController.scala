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
    * Show and reply selected highscore game
    */
  @FXML def onReplayGame(): Unit = {
    val selectedEntry: HighScoreEntry = tbHighscore.getSelectionModel.getSelectedItem

    //Check if entry is selected
    if (!(selectedEntry == null)) {
      //Open dialog to show history game
      val selectedBattleShipPlayRound = selectedEntry.getBattleShipPlayRound()
      (new BattleShipFxDialogHandler).showHigschoreGameDialog(selectedBattleShipPlayRound)
    }
  }

  /**
    * Ask user again if Highscore should cleared and if confirmed clear it
    */
  @FXML def resetHighscore(): Unit = {

    val deleteHighscore: Boolean = (new BattleShipFxDialogHandler).askResetHighscoreDialog()
    if (deleteHighscore == true) {
      HighScore().clearHighscore()
      initData
    }

  }

  /**
    * Go back to main Scene
    */
  @FXML def returnToMain(): Unit = BattleShipFxApp.showScene(BattleShipFxApp.getWelcomeScene,BattleShipFxApp.getRootStage)


  override def initialize(location: URL, resources: ResourceBundle) = {
    //Init table columns
    initTableViewColumn[String](colDate,_.formatedDate)
    initTableViewColumn[String](colWinner,_.winnerName)
    initTableViewColumn[String](colGameName,_.playroundName)
    initTableViewColumn[Int](colClickAmount,_.clickAmount)

    //Set correct table (selection mode,CellSize, ...)
    tbHighscore.getSelectionModel.setSelectionMode(SelectionMode.SINGLE)
    tbHighscore.getSelectionModel.setCellSelectionEnabled(false)
    tbHighscore.setFixedCellSize(25)

    //Show data
    initData
  }

  private def initData(): Unit = {
    //Load highscore and display
    val tableData: ObservableList[HighScoreEntry] = convertToObservableList( HighScore().getSortedHighScore())
    tbHighscore.setItems(tableData)
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

  def initTableViewColumnCellValueFactory[HighScoreEntry, T](column: TableColumn[HighScoreEntry, T], function: HighScoreEntry => Any): Unit = {
    column.setCellValueFactory(createCellValueFactory(cell => function(cell.getValue).asInstanceOf[ObservableValue[T]]))
  }

  private def createCellValueFactory[HighScoreEntry, T](fn: TableColumn.CellDataFeatures[HighScoreEntry, T] => ObservableValue[T]):
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

  def getBattleShipPlayRound(): BattleShipGamePlayRound = playRound
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
