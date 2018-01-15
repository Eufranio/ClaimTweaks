## ClaimTweaks
ClaimTweaks is an addon to [GriefPrevention](https://forums.spongepowered.org/t/griefprevention-official-thread-1-10-1-11-1-12-town-wecui-support/1123) that adds some simple stuff to claims, like per-claim weather, per-claim time, commands on enter/exit, etc. Those features are supposed to be used only by admins, but they will also work if you give the proper permissions to your players. You also don't need to change anything on config files, just drop the plugin on your plugins/mods folder and it's ready!

### Features
* Clear the weather of the player when he enters a specific claim (such as your spawn)
* Sets the time of the player to a specific time (such as day)
* Run commands when players enter a specific claim
* Run commands when players leave a specific claim

### Basic Setup
1. Install [GriefPrevention](https://forums.spongepowered.org/t/griefprevention-official-thread-1-10-1-11-1-12-town-wecui-support/1123) and [PacketGate](https://github.com/CrushedPixel/PacketGate/releases) on your server (they are both dependencies, don't forget it!)
2. Say you want to change the time of your spawn to always day. Go to your spawn claim and run **/ctweaks setTime 6000**, you're done!

### Command Usage and Permissions (use inside the target claim)
* /ctweaks addCommand [type] [command]
    * Adds a specific command to the claim you're in, that will run when a player enters/leaves it, depending of `type`.
    * `type` is either `enter` or `leave`. The command accepts the `%player%` placeholder.
* /ctweaks listCommands
    * Lists all the commands assigned to the claim that you're in
* /ctweaks removeCommand [type] [id]
    * Removes an command from the `type` (see above) command list of the claim that you're in. `id` is the ID of the command, shown in `listCommands`.
* /ctweaks setClearWeather
    * Toggles the weather state of the claim (sets it to always clear if raining or vice versa)
* /ctweaks setTime [time]
    * Sets the time of players on the claim that you're in to `time`, in ticks. See the Minecraft wiki for exact values. Ex: `/ctweaks setTime 6000` will set the time to half day.
    
**If you find any issues, report them to the plugin's issue tracker. If you want, you can donate for me trough PayPal, my paypal email is frani@magitechserver.com**.