## PintoPlaytime
A simple spigot plugin to track playtime for players

### Features
* Stores playtimes in a SQLite DataBase (playtimes.db)
* Manually set playtime for a player
* View playtime for player
* Give permissions and announce when a player gets a playtime milestone

### Commands
* `/playtime <player> # Get's specified player's playtime (works offline if player has joined previously)`
* `/playtime <player> <time> # Set's specified player's playtime to specified value (format as X[unit] - e.g. 5h)`

### Permissions
* `pintoplaytime.playtime.get.self # Allows player to view their own playtime`
* `pintoplaytime.playtime.get.others # Allows player to view other player's playtime`
* `pintoplaytime.playtime.set # Allows player to set any player's playtime`

### Config
Milestones contains a list of 'milestones'. Format is:
* `time` - time required for the milestone. Format using X[unit] (e.g. 3h) supported units are m[minutes], h[hours], d[days]
* `message` - message to send when the milestone is achieved. Format using `RECIPIENT:MESSAGE`. Valid recipients are ALL (all online players) or PLAYER (the player who achieved the milestone). Message supports color codes with '&'
* `permission` - permission to give player when the milestone is achieved. 

### Notes
* Until requested, the only way to have multiple permissions per milestone is to set up two milestones with the same time requirements, `message` can be blank for one, and different permissions set.

### Errors/Bugs
Make an issue on this project's repository
