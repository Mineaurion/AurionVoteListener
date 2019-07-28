# AurionVoteListener

## ❓ What is it?

AurionVoteListener is a plugin to give rewards to player when his voted for your server. It works with NuVotifier. This plugin is a copy of the functionality of GAListener carry under sponge.

**This is not votifier listener so you need a listener like nuvotifier to make it work.**


## 💡 How it works?

When a vote is received via nuvotifier, the plugin gives rewards depending on what you have configured, and add the vote. If the player are not online, dont panic, It is added to the waiting list in the database.

## Features

* Give rewards based on the voting site
* Give extra rewards if players get to luck
* Give extra rewards if players reach a certain number of vote
* Give extra rewards based on permission
* Show top voters ingame


## ⌨️ Commands

Without permission :
* <code>/vote</code> -> Sends the "votemessage" of the config file to the player
* <code>/votetop </code>-> Shows the best Voters

Command for admin with permission "listener.admin"
* <code>/sponge plug-ins reload</code> -> reload the config file
* <code>/aurion cleartotals</code> -> clear the database for the vote
* <code>/aurion clearqueue</code> ->  clear the database for the queue without giving any reward
* <code>/aurion forcequeue</code> -> clear the database for the queue by giving the rewards
* <code>/aurion fakevote &lt;player&gt; [Service Name]</code> -> Execute a fake vote
* <code>/aurion set &lt;player&gt; &lt;vote&gt;</code> -> Set vote for a player

## ⚙️ Config
[Here](https://github.com/Mineaurion/AurionVoteListener/blob/master/configuration.md) you can find the explanations to configure the rewards

## Downloads
If you'd like to download this plugin, you can grab the latest build [here](https://github.com/Mineaurion/AurionVoteListener/releases)

## GitHub Repository
If you'd like to check out the GitHub Repository, you can do so [here](https://github.com/Mineaurion/AurionVoteListener)

## Discord
If you need support regarding our plugin, come on our [discord](https://discord.gg/Zn4ZbP9)

## Migrating from 2.4 version
If you come from the 2.4, don't worry the plugin create a new folder call aurionvotelistener (same as before but without the `s`) with the default config.
You need to transfer you actual config to the new one, the database is unchanged.
