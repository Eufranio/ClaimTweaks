package io.github.eufranio.claimtweaks;

import io.github.eufranio.claimtweaks.config.ClaimStorage;
import me.ryanhamshire.griefprevention.api.claim.Claim;
import me.ryanhamshire.griefprevention.api.claim.ClaimType;
import me.ryanhamshire.griefprevention.api.event.BorderClaimEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.cause.Root;

/**
 * Created by Frani on 14/01/2018.
 */
public class ClaimEventHandlers {

    @Listener
    public void onClaimEnter(BorderClaimEvent e, @First Player player) {
        ClaimTweaks.updateSettings(e.getEnterClaim(), player.getUniqueId());

        if (player.hasPermission("claimtweaks.bypass")) return;

        Claim enterClaim = e.getEnterClaim();
        ClaimStorage.Data enterData = ClaimStorage.of(enterClaim.getUniqueId());
        if (enterData != null) {
            for (String cmd : enterData.enterCommands) {
                Sponge.getCommandManager().process(Sponge.getServer().getConsole(), cmd.replace("%player%", player.getName()));
            }
            for (String cmd : enterData.playerEnterCommands) {
                Sponge.getCommandManager().process(player, cmd);
            }
        }

        Claim exitClaim = e.getExitClaim();
        ClaimStorage.Data exitData = ClaimStorage.of(exitClaim.getUniqueId());
        if (exitData != null) {
            for (String cmd : exitData.leaveCommands) {
                Sponge.getCommandManager().process(Sponge.getServer().getConsole(), cmd.replace("%player%", player.getName()));
            }
            for (String cmd : exitData.playerLeaveCommands) {
                Sponge.getCommandManager().process(player, cmd);
            }
        }
    }

}
