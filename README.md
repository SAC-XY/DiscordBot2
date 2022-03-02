# DiscordBot2

## Usage
VCに接続してから「あああ」と発言するとVCに接続しにきて「ま゛っ」と言い残し10分のカウントを開始します  

「あああ」と発言してから5分後に「でゅーーぅん」という馴染み深いアイキャッチ音でバフ切れをお知らせしてくれます  
1分前に「オーガ1分前」とテキストと「ﾋﾟﾀｧ…」という不気味な音声でお知らせしてくれます  
30秒前に「オーガ30秒前」とテキストのみでお知らせしてくれます  
2秒前に某N○Kの人気番組のアイキャッチ音声でお知らせしてくれます  

カウントダウン中にもう一度「あああ」と発言するとカウントダウンがリセットされ、そのタイミングから10分後のカウントダウンになります  

「ばいばい」という発言を見るとVCから切断します  


## Quickstart

```
$ sbt docker:publishLocal
$ cd target/docker/stage
$ heroku login
$ heroku container:login
$ heroku container:push bot -a uchikoma
$ heroku container:release bot -a uchikoma
```

## Require Environment
| key           | value                                                       |
|---------------|-------------------------------------------------------------|
| DISCORD_TOKEN | https://discord.com/developers/applications のbotから取得したtoken |
| TZ            | Asia/Tokyo                                                  |