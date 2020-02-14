# **You can find different sample configurations for rewards**

This is the default config
```
services {
    DEFAULT{
        broadcast="&a<username> &6has voted @ &a<service> &6and received &a5 Diamonds !"
        playerMessage="&aThanks for voting at &b<service> !"
        commands=[
            "give <username> diamonds 5"
         ]
    }
	
    "Minecraft-MP.com" {
        broadcast="&a<username> &6has voted @ &a<service> &6and received &a5 Diamonds !"
        playerMessage="&aThanks for voting at &b<service> !"
        commands=[
            "give <username> diamonds 5"
        ]
    }
}

```
Now if you want to add web site, just add this under services (if needed enclose the service string with quotes) :
```
    NameOfTheService {
        broadcast="&a<username> &6has voted @ &a<service> &6and received &a5 Diamonds !"
        playerMessage="&aThanks for voting at &b<service> !"
        commands=[
            "give <username> diamonds 5"
        ]
    }
```
**For broadcast and playerMessage, you can use this variable:**

- &lt;service&gt; = service name
- &lt;username&gt;= player username
- &lt;votes&gt; = current vote total
- You can the use the classic color code

**Extra Reward**

The extra reward is based on the % to enable it set to true `chanceReward=true`

*The example below would give a 30% chance of a voter receiving an extra 5 book, and a 10% chance of an extra 5 ghast tear.*
```
extraReward{
    30{
        broadcast="&a<username> &6was super lucky and received an &aextra 5 book"
        playerMessage="&aYou were super lucky and received an &a extra 5 book "
        commands=[
            "give <username> book 5"
        ]
    }
    10{
        broadcast="&a<username> &6was lucky and received an &aextra 5 book"
        playerMessage="&aYou were lucky and received an &a extra 5 book "
        commands=[
            "give <username> ghast_tear 5"
        ]
    }
}

```

**Tips:**

If you want to give normal reward and not always the chance reward just add a reward section wich give nothing.

*The example below would give a 80% chance of not giving reward, a 30% chance of a voter receiving an extra 5 book and a 10% chance of an extra 5 ghast tear.*

```
extraReward{
    80{
        broadcast="&a<username> &6was unlucky and got nothing"
        playerMessage="&aYou were unlucky this time, take your chance by voting again"
        commands=[""]
    }
    30{
        broadcast="&a<username> &6was super lucky and received an &aextra 5 book"
        playerMessage="&aYou were super lucky and received an &a extra 5 book "
        commands=[
            "give <username> book 5"
        ]
    }
    10{
        broadcast="&a<username> &6was lucky and received an &aextra 5 book"
        playerMessage="&aYou were lucky and received an &a extra 5 book "
        commands=[
            "give <username> ghast_tear 5"
        ]
    }
}

```
