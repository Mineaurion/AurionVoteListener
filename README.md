# AurionVoteListener

## ❓ What is it?

AurionVoteListener is a plugin to give rewards to player when his voted for your server. It works with NuVotifier. This plugin is a copy of the functionality of GAListener carry under sponge


## 💡 How it works?

When the player voted, the plugin gives rewards depending on what you have configured, and add the vote. If the player are not online, dont panic, It is added to the waiting list in the database.

## Features

* Give rewards based on the voting site
* Give extra rewards if players get to luck
* Show top voters ingame

## ⚙️ Config

[Here](https://github.com/Mineaurion/AurionVoteListener/blob/master/configuration.md) you can find the explanations to configure the rewards
[Here](https://github.com/Mineaurion/AurionVoteListener/blob/master/example) you can find an exampe of configuration

## ⌨️ Commands

* Without permission :
<code>/Vote</code> -> Sends the "votemessage" of the config file to the player
<code>/Votetop </code>-> Shows the best Voters

* Command for admin with permission "listener.admin"
<code>/sponge plug-ins reload</code> -> reload the config file
<code>/Aurions cleartotals</code> -> clear the database for the vote
<code>/Aurions clearqueue</code> ->  clear the database for the queue without giving any reward
<code>/Aurions forcequeue</code> -> clear the database for the queue by giving the rewards
<code>/Aurions fakevote &lt;player&gt; [Service Name]</code> -> Execute a fake vote
<code>/Aurions set &lt;player&gt; &lt;vote&gt;</code> -> Set vote for a player

## Downloads
If you'd like to download this plugin, you can grab the latest build [here](https://github.com/Mineaurion/AurionVoteListener/releases)

## GitHub Repository
If you'd like to check out the GitHub Repository, you can do so [here](https://github.com/Mineaurion/AurionVoteListener)

## Discord

If you need support regarding our plugin, come on our [discord](https://discord.gg/Zn4ZbP9)
