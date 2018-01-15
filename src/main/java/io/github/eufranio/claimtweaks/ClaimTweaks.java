package io.github.eufranio.claimtweaks;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import eu.crushedpixel.sponge.packetgate.api.event.PacketEvent;
import eu.crushedpixel.sponge.packetgate.api.listener.PacketListenerAdapter;
import eu.crushedpixel.sponge.packetgate.api.registry.PacketConnection;
import eu.crushedpixel.sponge.packetgate.api.registry.PacketGate;
import io.github.eufranio.claimtweaks.config.ClaimStorage;
import io.github.eufranio.claimtweaks.config.ConfigManager;
import io.netty.buffer.Unpooled;
import me.ryanhamshire.griefprevention.GriefPrevention;
import me.ryanhamshire.griefprevention.api.GriefPreventionApi;
import me.ryanhamshire.griefprevention.api.claim.Claim;
import me.ryanhamshire.griefprevention.api.claim.ClaimType;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.world.WorldServer;
import ninja.leaping.configurate.objectmapping.GuiceObjectMapperFactory;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.Dependency;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Plugin(
        id = "claimtweaks",
        name = "ClaimTweaks",
        description = "Simple plugin that adds some tweaks to GriefPrevention claims",
        authors = {
                "Eufranio"
        },
        dependencies = {
                @Dependency(id = "griefprevention"),
                @Dependency(id = "packetgate")
        }
)
public class ClaimTweaks extends PacketListenerAdapter {

    @Inject
    private Logger logger;

    @Inject
    @ConfigDir(sharedRoot = false)
    public File configDir;

    @Inject
    public GuiceObjectMapperFactory mapper;

    public static GriefPreventionApi API;

    private ConfigManager<ClaimStorage> storage;
    private static ClaimTweaks instance;
    private Map<UUID, Long> time = Maps.newHashMap();
    private List<UUID> weather = Lists.newArrayList();

    @Listener
    public void onServerStart(GamePostInitializationEvent event) {
        Sponge.getServiceManager().provide(PacketGate.class).ifPresent(p -> {
            p.registerListener(this, ListenerPriority.DEFAULT, SPacketTimeUpdate.class);
            p.registerListener(this, ListenerPriority.DEFAULT, SPacketChangeGameState.class);
        });
        instance = this;
        this.storage = new ConfigManager<>(ClaimStorage.class, configDir, "ClaimData.conf", mapper, true, this);
        API = GriefPrevention.getApi();
        try {
            this.totalWorldTime = SPacketTimeUpdate.class.getDeclaredField("field_149369_a");
            this.totalWorldTime.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Sponge.getEventManager().registerListeners(this, new ClaimEventHandlers());
        Sponge.getEventManager().registerListeners(this, new PlayerEventHandlers());
        CommandHandler.registerCommands(this);
    }

    public static ClaimStorage getStorage() {
        return instance.storage.getConfig();
    }

    private Field totalWorldTime; // SPacketTimeUpdate.totalWorldtime
    @Override
    public void onPacketWrite(PacketEvent e, PacketConnection c) {
        try {
            if (e.getPacket() instanceof SPacketChangeGameState && weather.contains(c.getPlayerUUID())) {
                SPacketChangeGameState packet = (SPacketChangeGameState) e.getPacket();
                PacketBuffer buffer = new PacketBuffer(Unpooled.buffer(16));
                packet.writePacketData(buffer);
                if (buffer.readByte() == 7) {
                    e.setPacket(new SPacketChangeGameState(7, 0.0F));
                }
            } else if (e.getPacket() instanceof SPacketTimeUpdate && this.time.containsKey(c.getPlayerUUID())) {
                SPacketTimeUpdate packet = (SPacketTimeUpdate) e.getPacket();
                long totalTime = this.totalWorldTime.getLong(packet);
                long worldTime = this.time.get(c.getPlayerUUID());
                e.setPacket(new SPacketTimeUpdate(totalTime, worldTime, false));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void putOrUpdateTime(UUID player, long time) {
        if (instance.time.containsKey(player)) {
            instance.time.replace(player, time);
        } else {
            instance.time.put(player, time);
        }
    }

    public static void putOrUpdateWeather(UUID player) {
        instance.weather.add(player);
        Sponge.getServer().getPlayer(player).ifPresent(p -> ((EntityPlayerMP) p).connection.sendPacket(new SPacketChangeGameState(7, 0.0F)));
    }

    public static void restorePlayer(UUID player) {
        instance.weather.remove(player);
        instance.time.remove(player);
        Sponge.getServer().getPlayer(player).ifPresent(p ->
                ((EntityPlayerMP) p).connection.sendPacket(new SPacketChangeGameState(7, ((WorldServer) p.getWorld()).getRainStrength(1)))
        );
    }

    public static void updateSettings(Claim claim, UUID player) {
        if (claim.getType() != ClaimType.WILDERNESS) {
            ClaimStorage.Data data = ClaimTweaks.getStorage().of(claim.getUniqueId());
            if (data != null) {
                if (data.timeLock != 0) {
                    ClaimTweaks.putOrUpdateTime(player, data.timeLock);
                }
                if (data.clearWeather) {
                    ClaimTweaks.putOrUpdateWeather(player);
                }
            }
        } else {
            ClaimTweaks.restorePlayer(player);
        }
    }

}
