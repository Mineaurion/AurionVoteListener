# **You can find different sample configurations for rewards**

This is the default config
```
services {
	DEFAULT{
	broadcast="<GREEN><username> <GOLD>has voted @ <GREEN><service> <GOLD>and received <GREEN>5 Diamonds !"
	playermessage="<GREEN>Thanks for voting at <AQUA><service> !"
	commands=[
		"give <username> book 5"
	]
	}
	
	MC-Index{
	broadcast="<GREEN><username> <GOLD>has voted @ <GREEN> MC-Index <GOLD>and received <GREEN>5 Diamonds !"
	playermessage="<GREEN>Thanks for voting at <AQUA><service> !"
	commands=[
		"give <username> book 5"
	]
	}
}

```
Now if you want to add web site, just add this under services:
```
NameOfService{
	broadcast="<GREEN><username> <GOLD>has voted @ <GREEN> MC-Index <GOLD>and received <GREEN>5 Diamonds !"
	playermessage="<GREEN>Thanks for voting at <AQUA><service> !"
	commands=[
		"give <username> book 5",
		"give <username> redstone_block 5"
	]
}

```
**For broadcast and playermessage, you can use this variable:**

- &lt;service&gt; = service name
- &lt;username&gt;= player username
- &lt;votes&gt; = current vote total
- For color you can use the following variables or directly §
- &lt;AQUA&gt; &lt;BLACK&gt; &lt;BLUE&gt; &lt;DARK\_AQUA&gt; &lt;DARK\_BLUE&gt;
- &lt;DARK\_GRAY&gt; &lt;DARK\_GREEN&gt; &lt;DARK\_PURPLE&gt; &lt;DARK\_RED&gt; &lt;GOLD&gt;
- &lt;GRAY&gt; &lt;GREEN&gt; &lt;LIGHT\_PURPLE&gt; &lt;RED&gt; &lt;WHITE&gt; &lt;YELLOW&gt; &lt;BOLD&gt;
- &lt;ITALIC&gt; &lt;UNDERLINE&gt; &lt;STRIKETHROUGH&gt; &lt;STRIKE&gt;
- &lt;STRIKETHROUGH&gt; &lt;MAGIC&gt; &lt;RESET&gt;

**Extra Reward**

To enable additional rewards, you need to set True to AddExtraReward.

The extra reward is based on the %
Example with GiveChanceReward=true

The example below would give a 30% chance of a voter receiving an extra 5 book, and a 10.50% chance of an extra 5 book and 5 ghast tear.
```
ExtraReward{
	"30"{
		broadcast="<GREEN><username> <GOLD>was super lucky and received an <GREEN>extra 5 book"
		playermessage="<GREEN>You were super lucky and received an <GREEN> extra 5 book "
		commands=[
			"give <username> book 5"
		]
	}
	"10.5"{
		broadcast="<GREEN><username> <GOLD>was lucky and received an <GREEN>extra 5 ghast tear”
		playermessage="<GREEN>You were lucky and received an extra 5 book!"
		commands=[
			"give <username> ghast tear 5"
		]
	}
}

```
Now if I take the same config and I turn GiveChanceReward to false

I have 70 % to have any, 20% to have 5 book and 10% to have 5 ghast tear













**Tips:**

If you want to give random rewards use the ExtraReward part as in the following example.
```
services {
	DEFAULT{
	broadcast="<GREEN><username> <GOLD>has voted @ <GREEN><service"
	playermessage="<GREEN>Thanks for voting at <AQUA><service> !"
	commands=[
		
	]
	}
	
	MC-Index{
	broadcast="<GREEN><username> <GOLD>has voted @ <GREEN> MC-Index "
	playermessage="<GREEN>Thanks for voting at <AQUA><service> !"
	commands=[
			]
	}
}
ExtraReward{
	30{
		broadcast="<GREEN><username> <GOLD>was super lucky and received an <GREEN>extra 5 book"
		playermessage="<GREEN>You were super lucky and received an <GREEN> extra 5 book "
		commands=[
			"give <username> book 5"
		]
	}
	10{
		broadcast="<GREEN><username> <GOLD>was lucky and received an <GREEN>extra 5 ghast tear”
		playermessage="<GREEN>You were lucky and received an extra 5 book!"
		commands=[
			"give <username> ghast tear 5"
		]
	}
}

```
Now I will get the rewards as in the Extra Reward part
