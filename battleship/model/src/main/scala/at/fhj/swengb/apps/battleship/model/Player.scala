package at.fhj.swengb.apps.battleship.model

case class Player(name: String, cssStyleClass: String) {
  require(!name.isEmpty)
}
