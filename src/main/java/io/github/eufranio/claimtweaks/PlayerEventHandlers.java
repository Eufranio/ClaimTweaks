package io.github.eufranio.claimtweaks;

import me.ryanhamshire.griefprevention.api.claim.Claim;
import me.ryanhamshire.griefprevention.api.claim.ClaimType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;

/**
 * Created by Frani on 14/01/2018.
 */
public class PlayerEventHandlers {

    @Listener
    public void onQuit(ClientConnectionEvent.Disconnect event, @Root Player p) {
        ClaimTweaks.restorePlayer(p.getUniqueId());
    }

    @Listener
    public void onJoin(ClientConnectionEvent.Join event, @Root Player p) {
        Claim claim = ClaimTweaks.API.getClaimManager(p.getWorld()).getClaimAt(p.getLocation());
        if (claim.getType() != ClaimType.WILDERNESS) {
            ClaimTweaks.updateSettings(claim, p.getUniqueId());
        }
    }

}
