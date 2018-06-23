package io.github.eufranio.claimtweaks;

import io.github.eufranio.claimtweaks.config.ClaimStorage;
import me.ryanhamshire.griefprevention.api.claim.Claim;
import me.ryanhamshire.griefprevention.api.claim.ClaimType;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.world.WorldServer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 * Created by Frani on 14/01/2018.
 */
public class CommandHandler {

    public static void registerCommands(Object plugin) {

        CommandSpec removeCommand = CommandSpec.builder()
                .permission("claimtweaks.command.removecommand")
                .arguments(
                        GenericArguments.string(Text.of("type")),
                        GenericArguments.integer(Text.of("id")),
                        GenericArguments.optional(
                                GenericArguments.bool(Text.of("as player"))
                        )
                )
                .executor((sender, context) -> {
                    if (!(sender instanceof Player)) {
                        throw new CommandException(Text.of("You must be a player to run this command!"));
                    }
                    Player p = (Player) sender;
                    Claim claim = ClaimTweaks.API.getClaimManager(p.getWorld()).getClaimAt(p.getLocation());
                        ClaimStorage.Data data = ClaimStorage.getOrCreateData(claim.getUniqueId());
                        String type = context.<String>getOne("type").get();
                        int id = context.<Integer>getOne("id").get();
                        boolean asPlayer = context.<Boolean>getOne("as player").orElse(false);
                        if (type.equalsIgnoreCase("enter")) {
                            if (asPlayer) {
                                data.playerEnterCommands.remove(id);
                            } else {
                                data.enterCommands.remove(id);
                            }
                            sender.sendMessage(Text.of(TextColors.GREEN, "Successfully removed the command to this claim!"));
                        } else if (type.equalsIgnoreCase("leave")) {
                            data.leaveCommands.remove(id);
                            sender.sendMessage(Text.of(TextColors.GREEN, "Successfully removed the command to this claim!"));
                        } else {
                            sender.sendMessage(Text.of(TextColors.RED, "Unknown type! Use enter/leave!"));
                        }
                    return CommandResult.success();
                })
                .build();

        CommandSpec addCommand = CommandSpec.builder()
                .permission("claimtweaks.command.addcommand")
                .arguments(
                        GenericArguments.string(Text.of("type")),
                        GenericArguments.bool(Text.of("as player")),
                        GenericArguments.remainingJoinedStrings(Text.of("cmd"))
                )
                .executor((sender, context) -> {
                    if (!(sender instanceof Player)) {
                        throw new CommandException(Text.of("You must be a player to run this command!"));
                    }
                    Player p = (Player) sender;
                    Claim claim = ClaimTweaks.API.getClaimManager(p.getWorld()).getClaimAt(p.getLocation());
                    ClaimStorage.Data data = ClaimStorage.getOrCreateData(claim.getUniqueId());
                    String type = context.<String>getOne("type").get();
                    boolean asPlayer = context.<Boolean>getOne("as player").orElse(false);
                    try {
                        if (type.equalsIgnoreCase("enter")) {
                            if (asPlayer) {
                                data.playerEnterCommands.add(context.<String>getOne("cmd").get());
                            } else {
                                data.enterCommands.add(context.<String>getOne("cmd").get());
                            }
                            sender.sendMessage(Text.of(TextColors.GREEN, "Successfully added the command to this claim!"));
                        } else if (type.equalsIgnoreCase("leave")) {
                            if (asPlayer) {
                                data.playerLeaveCommands.add(context.<String>getOne("cmd").get());
                            } else {
                                data.leaveCommands.add(context.<String>getOne("cmd").get());
                            }
                            sender.sendMessage(Text.of(TextColors.GREEN, "Successfully added the command to this claim!"));
                        } else {
                            sender.sendMessage(Text.of(TextColors.RED, "Unknown type! Use enter/leave!"));
                        }
                    } catch (IndexOutOfBoundsException e) {
                        sender.sendMessage(Text.of(TextColors.RED, "This isn't an valid ID! Try taking a look at /ctweaks listcommands again!"));
                    }
                    return CommandResult.success();
                })
                .build();

        CommandSpec listCommands = CommandSpec.builder()
                .permission("claimtweaks.command.listcommands")
                .executor((sender, context) -> {
                    if (!(sender instanceof Player)) {
                        throw new CommandException(Text.of("You must be a player to run this command!"));
                    }
                    Player p = (Player) sender;
                    Claim claim = ClaimTweaks.API.getClaimManager(p.getWorld()).getClaimAt(p.getLocation());
                    ClaimStorage.Data data = ClaimStorage.getOrCreateData(claim.getUniqueId());

                    sender.sendMessage(Text.of(TextColors.GRAY, "--------------------------------------------------"));

                    sender.sendMessage(Text.of(TextColors.GREEN, "Enter commands assigned to this claim: "));
                    int id = 0;
                    for (String cmd : data.enterCommands) {
                        sender.sendMessage(Text.of(TextColors.BLUE, "   *" + id + " - " + cmd));
                        id++;
                    }
                    sender.sendMessage(Text.of());

                    sender.sendMessage(Text.of(TextColors.GREEN, "Enter commands (as player) assigned to this claim: "));
                    id = 0;
                    for (String cmd : data.playerEnterCommands) {
                        sender.sendMessage(Text.of(TextColors.BLUE, "   *" + id + " - " + cmd));
                        id++;
                    }
                    sender.sendMessage(Text.of());

                    sender.sendMessage(Text.of(TextColors.GREEN, "Leave commands assigned to this claim: "));
                    id = 0;
                    for (String cmd : data.leaveCommands) {
                        sender.sendMessage(Text.of(TextColors.BLUE, "   *" + id + " - " + cmd));
                        id++;
                    }
                    sender.sendMessage(Text.of());

                    sender.sendMessage(Text.of(TextColors.GREEN, "Leave commands (as player) assigned to this claim: "));
                    id = 0;
                    for (String cmd : data.playerLeaveCommands) {
                        sender.sendMessage(Text.of(TextColors.BLUE, "   *" + id + " - " + cmd));
                        id++;
                    }

                    sender.sendMessage(Text.of(TextColors.GRAY, "--------------------------------------------------"));
                    return CommandResult.success();
                })
                .build();

        CommandSpec setTime = CommandSpec.builder()
                .permission("claimtweaks.command.settime")
                .arguments(GenericArguments.integer(Text.of("time")))
                .executor((sender, context) -> {
                    if (!(sender instanceof Player)) {
                        throw new CommandException(Text.of("You must be a player to run this command!"));
                    }
                    Player p = (Player) sender;
                    Claim claim = ClaimTweaks.API.getClaimManager(p.getWorld()).getClaimAt(p.getLocation());
                    ClaimStorage.Data data = ClaimStorage.getOrCreateData(claim.getUniqueId());
                    data.timeLock = context.<Integer>getOne("time").get();
                    sender.sendMessage(Text.of(TextColors.GREEN, "Successfully updated the time lock of this claim!"));
                    return CommandResult.success();
                })
                .build();

        CommandSpec setClearWeather = CommandSpec.builder()
                .permission("claimtweaks.command.setweather")
                .executor((sender, context) -> {
                    if (!(sender instanceof Player)) {
                        throw new CommandException(Text.of("You must be a player to run this command!"));
                    }
                    Player p = (Player) sender;
                    Claim claim = ClaimTweaks.API.getClaimManager(p.getWorld()).getClaimAt(p.getLocation());
                    ClaimStorage.Data data = ClaimStorage.getOrCreateData(claim.getUniqueId());
                    data.clearWeather = !data.clearWeather;
                    if (!data.clearWeather) {
                        ((EntityPlayerMP) p).connection.sendPacket(new SPacketChangeGameState(7, ((WorldServer) p.getWorld()).getRainStrength(1)));
                    }
                    sender.sendMessage(Text.of(TextColors.GREEN, "Successfully toggled the permanent weather of this claim!"));
                    return CommandResult.success();
                })
                .build();

        CommandSpec main = CommandSpec.builder()
                .permission("claimtweaks.command.main")
                .executor((sender, context) -> {
                    sender.sendMessage(Text.of(TextColors.RED, "Error! Valid subcommands: setClearWeather, setTime, listCommands, addCommand, removeCommand"));
                    return CommandResult.success();
                })
                .child(setClearWeather, "setClearWeather")
                .child(setTime, "setTime")
                .child(listCommands, "listCommands")
                .child(addCommand, "addCommand")
                .child(removeCommand, "removeCommand")
                .build();

        Sponge.getCommandManager().register(plugin, main, "claimtweaks", "ct", "ctweaks", "ctw");

    }

}
