import akka.actor.Actor
import com.sedmelluq.discord.lavaplayer.player.{AudioLoadResultHandler, AudioPlayer, AudioPlayerManager}
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.{AudioPlaylist, AudioTrack}
import net.dv8tion.jda.api.entities.TextChannel

import java.io.File

class CountdownActor(
                    val textChannel: TextChannel,
                    val audioPlayerManager: AudioPlayerManager,
                    val audioPlayer: AudioPlayer,
                    val preAlermSoundFile: File,
                    val alermSoundFile: File
                    ) extends Actor {

  override def receive: Receive = {
    case "1分前" =>
      textChannel.sendMessage("オーガ1分前").queue()

      audioPlayerManager.loadItem(preAlermSoundFile.getPath, new AudioLoadResultHandler(){
        override def trackLoaded(track: AudioTrack): Unit = {
          audioPlayer.playTrack(track)
        }

        override def playlistLoaded(playlist: AudioPlaylist): Unit = {
          println("playlist loaded!!!")
        }

        override def noMatches(): Unit = {
          println("ノーマッチ")
        }

        override def loadFailed(exception: FriendlyException): Unit = {
          println("読込失敗")
          println(exception.getMessage)
        }
      })

    case "30秒前" =>
      textChannel.sendMessage("オーガ30秒前").queue()

    case "2秒前" =>
      audioPlayerManager.loadItem(alermSoundFile.getPath, new AudioLoadResultHandler(){
        override def trackLoaded(track: AudioTrack): Unit = {
          audioPlayer.playTrack(track)
        }

        override def playlistLoaded(playlist: AudioPlaylist): Unit = {
          println("playlist loaded!!!")
        }

        override def noMatches(): Unit = {
          println("ノーマッチ")
        }

        override def loadFailed(exception: FriendlyException): Unit = {
          println("読込失敗")
          println(exception.getMessage)
        }
      })
    case _ =>
  }
}
