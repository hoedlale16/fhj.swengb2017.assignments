package at.fhj.swengb.apps.battleship.model

import java.io.File

case class BattleShipGameCredits() {

  private def appendHeaderText(builder: StringBuilder): StringBuilder =  {
    builder.append("<h1>Project Description</h1>")
           .append("This Battleship Game is part of a students' project.\n")
           .append("It was created for the subject \"Software Engineering Basics (SWENGB)\"\n." )
           .append("This subject is part of the bachelor program Information Management held by the UAS JOANNEUM in the third semester.")

    val imaLogo: File = new File(getClass.getResource("/at/fhj/swengb/apps/battleship/jfx/pics/FH_IMA_Logo.jpg").getFile)
    builder.append("<br><center><img src=\""+imaLogo.toURI+"\" width='300' /></center><br>")

  }

  private def appendTeamMembers(builder: StringBuilder): StringBuilder = {
    builder.append("<h1>Project Team</h1>")
      .append("<br>The project Team which is responsible for the creation of this application consists of the following persons:")
      .append("<ul>")
      .append("<li>FERNBACH Gregor</li>")
      .append("<li>HOEDL Alexander</li>")
      .append("<li>QERIMI Ardian</li>")
      .append("<li>WEILAND Sebastian</li>")
      .append("</ul>")
      .append("Moreover this project was guided and supported by lecturer Dipl.-Ing. Robert Ladstätter. <br>")
  }

  private def appendTeamRoles(builder: StringBuilder): StringBuilder = {
    builder.append("<h1>Project Roles</h1>")
      .append("The roles of the project team were as follows:")
      .append("<ul>")
      .append("<li>Head of documentation & project management: Fernbach Gregor</li>")
      .append("<li>Head of development: Hoedl Alexander</li>")
      .append("<li>Head of Design and Layout: Qerimi Ardian</li>")
      .append("<li>Head of Testing and quality management: Weiland Sebastian</li>")
      .append("</ul>")
  }


  private def appendExternalSources(builder: StringBuilder): StringBuilder = {
    builder.append("<h1>Resources</h1>")
      .append("Pictures included in this application were extracted from the following websites:<br>")
      .append("<ul>")
      .append("<li>href=https://pixabay.com/p-1283475/?no_redirect</li>")
      .append("<li>http://wallpaperpulse.com/wallpaper/4028691</li>")
      .append("<li>https://www.flickr.com/photos/verfain/6388934159/</li>")
      .append("<li>https://www.iconfinder.com/icons/42343/mute_sound_icon#size=32</li>")
      .append("<li>https://www.iconfinder.com/icons/42341/audio_editing_sound_speaker_volume_icon#size=32</li>")
      .append("</ul>")
      .append("Music & Sound effects included in this application were extracted from the following websites: <br>")
      .append("<ul>")
      .append("<li>http://freemusicarchive.org/music/Dee_Yan-Key/Killing_Mozart/06--Dee_Yan-Key-Italian_Nightmare</li>")
      .append("<li> http://soundbible.com/1986-Bomb-Exploding.html</li>")
      .append("<li>http://soundbible.com/1463-Water-Balloon.html</li>")
      .append("</ul>")
  }

  /**
    * Returns Credits as a string
    * @return
    */
  def getCreditText: String = {
    val credits: StringBuilder = new StringBuilder("<body bgcolor=\"#61380B\">")

    //Append text modules
    appendHeaderText(credits)
    appendTeamMembers(credits)
    appendTeamRoles(credits)
    appendExternalSources(credits)
    appendLicense(credits)

    credits.toString()
  }

  private def appendLicense(builder: StringBuilder): StringBuilder = {
    val cc: File = new File(getClass.getResource("/at/fhj/swengb/apps/battleship/jfx/pics/by-nc-sa.png").getFile)
    builder.append("<h1>License</h1>")
      .append("This program is licensed under CC BY-NC-SA 4.0")
      .append("<br><center><img src=\"" + cc.toURI + "\" width='300'></center><br>")
  }

}
