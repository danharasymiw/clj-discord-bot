# clj-discord-bot
Discord bot created for my personal discord.

## Commands
### !d20
Picks a random number from 1 - 20

### !img \<your_query_here\>
Finds a random image of <your_query_here>

### !summon \<game query here\>
Will ping everyone who has ever played the game before, if multiple games are found in the query it will
select the most popular game.

## Setup
[//]: # (switch to using https://github.com/LonoCloud/lein-voom/ for the library)
Get library
```bash
clone https://github.com/yotsov/clj-discord && cd clj-discord
lein install
```
Place discord token into `discord_token.txt` in root dir  
Run Bot
```bash
lein uberjar
java -jar target/clj-discord-bot-0.1.0-SNAPSHOT-standalone.jar
```
