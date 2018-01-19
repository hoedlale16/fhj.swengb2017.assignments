package at.fhj.swengb.apps.battleship.model

import javafx.scene.media.{Media, MediaPlayer}

case class BattleShipJukeBox(backgroundMusic: Media, shipHitMedia: Media, waterHitMedia: Media) {

  private val backgroundMusicPlayer: MediaPlayer = {
    val player: MediaPlayer = new MediaPlayer(backgroundMusic)
    player.setCycleCount(MediaPlayer.INDEFINITE)

    player
  }
  private var muteBackground: Boolean = false
  private var muteSoundEffect: Boolean = false

  def playMusic(background: Boolean, effect: Boolean): Unit = {
    muteBackground = !background
    muteSoundEffect = !effect

    //refresh background music
    playBackgroundMusic()
  }

  def isTotalMute: Boolean = muteBackground && muteBackground

  def setTotalMute(state: Boolean): Unit = {
    muteBackground = state
    muteSoundEffect = state

    //refresh background music
    playBackgroundMusic()
  }

  def isBackGroundMusicMute: Boolean = muteBackground

  def isSoundEffectsMute: Boolean = muteSoundEffect

  def hitShip(): Unit = {
    if (!muteSoundEffect) {
      backgroundMusicPlayer.stop()
      initMediaPlayer(shipHitMedia).play()
      playBackgroundMusic()
    }
  }

  def playBackgroundMusic(): Unit = {
    if (!muteBackground)
      backgroundMusicPlayer.play()
    else
      backgroundMusicPlayer.pause()
  }

  private def initMediaPlayer(media: Media): MediaPlayer = {
    val player: MediaPlayer = new MediaPlayer(media)
    player
  }

  def hitWater(): Unit = {
    if (!muteSoundEffect) {
      backgroundMusicPlayer.stop()
      initMediaPlayer(waterHitMedia).play()
      playBackgroundMusic()
    }
  }

}
