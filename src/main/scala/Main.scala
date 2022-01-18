import net.dv8tion.jda.api.JDABuilder
import org.slf4j.LoggerFactory

import java.io.{BufferedInputStream, BufferedOutputStream, BufferedReader, BufferedWriter, DataInputStream, DataOutputStream, File, FileInputStream, FileOutputStream, FileWriter, InputStreamReader, OutputStream, OutputStreamWriter}

class UndefinedDiscordToken(message: String) extends RuntimeException(message)

object Main extends App {
  val logger = LoggerFactory.getLogger(this.getClass)

  // fatjarにしてresources配下に入ったファイルに対しアクセスしたいが直接はアクセスできないので
  // 起動時に一度テンポラリファイルとしてresourcesの中からファイルを書き出して使用する
  def resourcesToTempFile(prefix: String, suffix: String) = {
    val inputStream = getClass.getClassLoader.getResourceAsStream(prefix + suffix)
    val dataInputStream = new DataInputStream(new BufferedInputStream(inputStream))

    val tempFile = File.createTempFile(prefix, suffix)
    val dataOutputStream = new FileOutputStream(tempFile)

    var b = new Array[Byte](4096)
    var readByte: Int = 0
    var totalByte: Int = 0
    readByte = dataInputStream.read(b)

    while (readByte != -1) {
      dataOutputStream.write(b, 0, readByte)
      totalByte += readByte
      readByte = dataInputStream.read(b)
      logger.debug("Read: " + readByte + " Total: " + totalByte)
    }
    dataInputStream.close()
    dataOutputStream.close()

    tempFile
  }

  val maybeDiscordToken: Option[String] = sys.env.get("DISCORD_TOKEN")

  val tmp1 = resourcesToTempFile("pitaaa", ".mp4")
  val tmp2 = resourcesToTempFile("pitagora", ".mp4")

  maybeDiscordToken.fold(throw new UndefinedDiscordToken("ディスコードのトークンが環境変数にみつかりませんでした")) { discordToken =>
    val jda = JDABuilder
      .createDefault(discordToken)
      .build

    jda.addEventListener(new CountdownTimerListener(tmp1,tmp2))
  }

}







