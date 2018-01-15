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
        Claim enterClaim = e.getEnterClaim();
        if (enterClaim.getType() != ClaimType.WILDERNESS) {
            ClaimStorage.Data data = ClaimStorage.of(enterClaim.getUniqueId());
            if (data != null) {
                for (String cmd : data.enterCommands) {
                    Sponge.getCommandManager().process(Sponge.getServer().getConsole(), cmd.replace("%player%", player.getName()));
                }
            }
        }
        Claim exitClaim = e.getExitClaim();
        if (exitClaim.getType() != ClaimType.WILDERNESS) {
            ClaimStorage.Data data = ClaimStorage.of(exitClaim.getUniqueId());
            if (data != null) {
                for (String cmd : data.leaveCommands) {
                    Sponge.getCommandManager().process(Sponge.getServer().getConsole(), cmd.replace("%player%", player.getName()));
                }
            }
        }
    }

}
