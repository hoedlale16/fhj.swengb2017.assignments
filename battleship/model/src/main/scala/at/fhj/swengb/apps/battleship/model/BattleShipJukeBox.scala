package at.fhj.swengb.apps.battleship.model

import javafx.scene.media.{Media, MediaPlayer}

case class BattleShipJukeBox(backgroundMusic: Media, shipHitMedia: Media, waterHitMedia: Media) {

  private var mute: Boolean = false;


  private val backgroundMusicPlayer: MediaPlayer = {
    val player: MediaPlayer = new MediaPlayer (backgroundMusic)
    player.setCycleCount (MediaPlayer.INDEFINITE)

    player
  }

  private def initMediaPlayer(media: Media): MediaPlayer = {
    val player: MediaPlayer = new MediaPlayer(media)
    player
  }

  def setMute(state: Boolean) = {
    mute = state
    //refresh background
    playBackgroundMusic()
  }

  def isMute = mute

  def hitShip(): Unit = {
    if(! mute) {
      backgroundMusicPlayer.stop()
      initMediaPlayer(shipHitMedia).play()
      backgroundMusicPlayer.play()
    }
  }
  def hitWater(): Unit = {
    if (! mute) {
      backgroundMusicPlayer.stop()
      initMediaPlayer(waterHitMedia).play()
      backgroundMusicPlayer.play()
    }
  }


  def playBackgroundMusic(): Unit = {
    if(! mute)
      backgroundMusicPlayer.play()
    else
      backgroundMusicPlayer.pause()
  }

}
