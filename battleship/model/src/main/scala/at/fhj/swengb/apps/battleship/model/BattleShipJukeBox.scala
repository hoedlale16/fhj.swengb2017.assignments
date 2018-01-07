package at.fhj.swengb.apps.battleship.model

import javafx.scene.media.{Media, MediaPlayer}

case class BattleShipJukeBox(shipHitMedia: Media, waterHitMedia: Media) {

  private def initMediaPlayer(media: Media): MediaPlayer = {
    val player: MediaPlayer = new MediaPlayer(media)
    player
  }

  def hitShip(): Unit = initMediaPlayer(shipHitMedia).play()
  def hitWater(): Unit = initMediaPlayer(waterHitMedia).play()

}
