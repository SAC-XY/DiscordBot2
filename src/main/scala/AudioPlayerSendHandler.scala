import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame
import net.dv8tion.jda.api.audio.AudioSendHandler

import java.nio.ByteBuffer

class AudioPlayerSendHandler(val audioPlayer: AudioPlayer) extends AudioSendHandler {
  var lastFrame: Option[AudioFrame] = None

  override def canProvide: Boolean = {
    val audioFrame = audioPlayer.provide()

    // オーディオフレームをインスタンス変数に更新
    if (audioFrame != null) {
      lastFrame = Some(audioFrame)
    } else {
      lastFrame = None
    }

    lastFrame.isDefined
  }

  override def provide20MsAudio(): ByteBuffer = ByteBuffer.wrap(lastFrame.get.getData)
  override def isOpus: Boolean = true
}