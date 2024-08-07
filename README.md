# ChopChat for Minecraft 1.21
*A lightweight Minecraft chat moderation plugin.*

Lucian Tash | July 2023

---

### Mute
- Command usage: `/mute <player> [duration] [reason]`
- Prevents the muted player from sending any public chat messages.
- Defaults to a permanent mute, but a duration in minutes can be specified.
- Messages that are blocked will still be logged by the server.
- This command is for operators only.
### Lock Chat
- Command usage: `/lockchat`
- Prevents all normal players from sending public chat messages.
- Operators override this and can still chat as normal.
- The command is a toggle; run it again to unlock the chat.
- This command is for operators only.
### Clear Chat
- Command usage: `/clearchat` or `/cc`
- Fills the chat with empty lines, effectively clearing any previous chat messages from all players' view.
- Note that some players may use clients which allow them to scroll higher in the chat, bypassing the clear chat.
- This command is for operators only.
### Alert
- Command usage: `/alert <msg>` or `/a <msg>`
- Sends a whitespaced message in chat for higher visibility.
- This command is for operators only.
- ![Alert message](image.png)
### Spam Filter
- Can be enabled or disabled in the `config.yml`.
- Blocks a message from being sent by a player who has already sent too many messages recently.
- Blocks attempts to send duplicate messages within a short timespan.
- Blocks messages with non-English or non-standard characters.
- The timespans and number of allowed recent messages are configurable.
- Operators are ignored by the spam filter.
### Banned Words Filter
- Words can be added or removed in the `config.yml`.
- Automatically mutes any player who sends a message containing a banned word, and blocks the message.
- Words are detected using a naive substring, i.e. if "tar" is a banned word, a message containing "ca**tar**act" will be filtered.
### Chat Color Codes
- Operators can use color codes to decorate their chat messages.
- These follow the [standard Minecraft color codes](https://htmlcolorcodes.com/minecraft-color-codes/) with `&` as the prefix.
- For example, `&6&lCat &r&nDog &aBird` will look like:
- ![Alt text](image-1.png)
### Ban-Evasion Prevention
- Accounts which attempt to join and are disconnected due to being banned will be redundantly banned by IP and username.
- This ensures that players are doubly-banned by both IP and username, and makes it more difficult to evade with additional accounts or VPNs.
- This should be paired with the modified ban command defined in `commands.yml`, and moderators should use `/ban` for all bans, not `/ban-ip` or `/minecraft:ban`.
- See the [`commands.yml`](https://github.com/luciankt/chopchat/blob/main/server/commands.yml) file for reference.

---
