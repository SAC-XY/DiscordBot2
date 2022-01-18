# DiscordBot2

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