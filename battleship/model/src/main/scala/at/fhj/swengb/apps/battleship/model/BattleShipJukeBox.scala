package at.fhj.swengb.apps.battleship.model

import javafx.scene.media.{Media, MediaPlayer}

case class BattleShipJukeBox(backgroundMusic: Media, shipHitMedia: Media, waterHitMedia: Media) {

  private var muteBackground: Boolean = false
  private var muteSoundEffect: Boolean = false


  private val backgroundMusicPlayer: MediaPlayer = {
    val player: MediaPlayer = new MediaPlayer (backgroundMusic)
    player.setCycleCount (MediaPlayer.INDEFINITE)

    player
  }

  private def initMediaPlayer(media: Media): MediaPlayer = {
    val player: MediaPlayer = new MediaPlayer(media)
    player
  }

  def setTotalMute(state: Boolean) = {
    muteBackground = state
    muteSoundEffect = state

    //refresh background music
    playBackgroundMusic()
  }

  def playMusic(background: Boolean, effect: Boolean) = {
    muteBackground = ! background
    muteSoundEffect = ! effect

    //refresh background music
    playBackgroundMusic()
  }

  def isTotalMute = muteBackground && muteBackground

  def isBackGroundMusicMute= muteBackground
  def isSoundEffectsMute = muteSoundEffect

  def hitShip(): Unit = {
    if(! muteSoundEffect) {
      backgroundMusicPlayer.stop()
      initMediaPlayer(shipHitMedia).play()
      backgroundMusicPlayer.play()
    }
  }
  def hitWater(): Unit = {
    if (! muteSoundEffect) {
      backgroundMusicPlayer.stop()
      initMediaPlayer(waterHitMedia).play()
      backgroundMusicPlayer.play()
    }
  }


  def playBackgroundMusic(): Unit = {
    if(! muteBackground)
      backgroundMusicPlayer.play()
    else
      backgroundMusicPlayer.pause()
  }

}
