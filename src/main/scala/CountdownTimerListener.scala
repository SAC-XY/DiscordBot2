import akka.actor.{ActorSystem, Cancellable, Props}
import com.sedmelluq.discord.lavaplayer.player.{AudioLoadResultHandler, DefaultAudioPlayerManager}
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.{AudioPlaylist, AudioTrack}
import net.dv8tion.jda.api.audio.SpeakingMode
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.LoggerFactory

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scala.concurrent.duration._
import scala.collection.mutable

class CountdownTimerListener(
                              val preAlermFile: File,
                              val alermFile: File
                            ) extends ListenerAdapter {
  val logger = LoggerFactory.getLogger(this.getClass)

  val actorSystem = ActorSystem("mySystem")
  import actorSystem.dispatcher

  val TIMER = 10.minutes

  val アラームセットコマンド = "あああ"
  val 退室コマンド = "ばいばい"

  var アラームスケジュール: Seq[Cancellable] = Seq.empty[Cancellable]

  override def onGuildMessageReceived(event: GuildMessageReceivedEvent): Unit = {
    logger.debug(event.getAuthor.getName)

    if(event.getMessage.getContentRaw == 退室コマンド) {
      // スケジュールされているActorタスクを全部キャンセルする
      アラームスケジュール.foreach { a =>
        a.cancel()
      }

      val audioManager = event.getGuild.getAudioManager
      audioManager.closeAudioConnection()
    }

    // 該当メッセージ以外を無視する
    if(event.getMessage.getContentRaw == アラームセットコマンド) {
      val textChannel = event.getMessage.getTextChannel

      // コマンドを実行した人と同じボイスチャンネルに入りたいので、
      // 発言者が現在どのボイスチャンネルに居るのかを調べる。
      // 発言者がどのボイスチャンネルにも属していない場合、nullが返ってくる事があるのでOptionで包んで扱う
      val maybeVoiceChannel = Option(event.getMember.getVoiceState.getChannel)

      maybeVoiceChannel.fold(
        // ボイスチャンネルに接続していないメンバーがコマンドを実行した
        textChannel.sendMessage("VCに接続してから呼んでね！").queue()

      ){ voiceChannel =>
        // ローカルソースもリモートソースも読み込むlavaplayerのPlayerManagerを準備
        val audioPlayerManager: DefaultAudioPlayerManager = new DefaultAudioPlayerManager()
        AudioSourceManagers.registerLocalSource(audioPlayerManager)
        AudioSourceManagers.registerRemoteSources(audioPlayerManager)
        val audioPlayer = audioPlayerManager.createPlayer()

        // VCに接続するため、オーディオマネージャーを取得し、接続したいvoiceChannelを指定して接続する
        val audioManager = event.getGuild.getAudioManager
        audioManager.setSendingHandler(new AudioPlayerSendHandler(audioPlayer))
        audioManager.setSpeakingMode(SpeakingMode.SOUNDSHARE)
        audioManager.openAudioConnection(voiceChannel)

        val countdownActor = actorSystem.actorOf(Props(classOf[CountdownActor], textChannel, audioPlayerManager, audioPlayer, preAlermFile, alermFile))

        // 現在時刻
        val ldt = LocalDateTime.now()

        // タイマー時刻
        val ldt_respawn_time = ldt.plusMinutes(TIMER.toMinutes)

        // 1分前
        val ldt_pre_alarm = ldt_respawn_time.minusMinutes(1)

        // 30秒前
        val ldt_main_alarm = ldt_respawn_time.minusSeconds(30)

        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val 予約した時間 = formatter.format(ldt)
        val アラーム1分前 = formatter.format(ldt_pre_alarm)
        val アラーム30秒前 = formatter.format(ldt_main_alarm)
        val オーガ出現時刻 = formatter.format(ldt_respawn_time)

        logger.debug(
          Seq(
            f"予約した時刻: ${予約した時間}",
            f"アラーム1分前: ${アラーム1分前}",
            f"アラーム30秒前: ${アラーム30秒前}",
            f"オーガ出現時刻: ${オーガ出現時刻}",
          ).mkString("\n")
        )
        textChannel.sendMessage("ま゛っ").queue()

        val cancellable1 = actorSystem.scheduler.scheduleOnce((TIMER - 1.minutes), countdownActor, "1分前")
        val cancellable2 = actorSystem.scheduler.scheduleOnce((TIMER - 30.seconds), countdownActor, "30秒前")
        val cancellable3 = actorSystem.scheduler.scheduleOnce((TIMER - 2.seconds), countdownActor, "2秒前")

        アラームスケジュール = Seq(cancellable1, cancellable2, cancellable3)

        logger.debug(アラームスケジュール.toString())
        logger.debug(event.getMember.getVoiceState.getChannel.getName)
        logger.debug(event.getChannel.getId)
      }
    }
  }
}