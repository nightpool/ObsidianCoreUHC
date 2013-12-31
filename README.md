ObsidianCoreUHC
===============

Obsidian Core UHC is an extensible framework for running UHC games with custom rules. 

This is a Bukkit plugin that allows server owners to run last man standing survival games (also known as UHC format). The plugin includes a standard set of rules for UHC to be enforced by the server, and (as or right now) 5 extra rulesets that can be enabled for any particular game. For example, some rulesets included with the plugin are a Team ruleset, that allows players to compete in teams, and a Spectator ruleset that allows those eliminated to watch the match.

Most of the solutions for playing UHC are currently either proprietary, secret sauce plugins, or not extensible enough. My plugin allows for runtime loading of Ruleset modules, making it possible to create a secondary ecosystem where people using this plugin can easily write and distribute custom rules as simple java .class or .jar files. (This is also a prime target for monetization: keep the base plugin free as in freedom and sell premium rulesets that provide new features)


##TODO
 - Team support is incomplete. Picking logic needs debugging.
 - Scatter rule needs to support teams, right now it scatters players individually. 
 - Specators can push entities, such as monsters. This is a bug.
 - Documentation needs to be written, for both the config files and the UHCRuleset framework.
