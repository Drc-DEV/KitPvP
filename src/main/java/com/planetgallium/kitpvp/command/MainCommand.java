package com.planetgallium.kitpvp.command;

import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.messages.ActionBar;
import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.api.Kit;
import com.planetgallium.kitpvp.api.PlayerArenaTeleportEvent;
import com.planetgallium.kitpvp.game.Arena;
import com.planetgallium.kitpvp.util.CacheManager;
import com.planetgallium.kitpvp.util.Resource;
import com.planetgallium.kitpvp.util.Resources;
import com.planetgallium.kitpvp.util.Toolkit;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

public class MainCommand implements CommandExecutor {

    private final Game plugin;
    private final Arena arena;
    private final Resources resources;
    private final Resource config;
    private final Resource messages;

    public MainCommand(Game game) {
        this.plugin = game;
        this.arena = game.getArena();
        this.resources = game.getResources();
        this.config = resources.getConfig();
        this.messages = resources.getMessages();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            String header = messages.getString("Messages.Commands.HelpHeader");
            if (header != null) {
                sender.sendMessage(header);
                sender.sendMessage(Toolkit.translate(" "));
            }
            String format = messages.getString("Messages.Commands.HelpFormat");
            sender.sendMessage(Toolkit.translate(format.replace("%cmd%", "kp help")
                    .replace("%args%", "").replace("%desc%", "Displays the help message")));
            sender.sendMessage(Toolkit.translate(format.replace("%cmd%", "kp reload")
                    .replace("%args%", "").replace("%desc%", "Reloads the configuration files")));
            sender.sendMessage(Toolkit.translate(format.replace("%cmd%", "kp addspawn")
                    .replace("%args%", "").replace("%desc%", "Adds a spawn to an arena")));
            sender.sendMessage(Toolkit.translate(format.replace("%cmd%", "kp arena")
                    .replace("%args%", "<arena>").replace("%desc%", "Teleports you to a different arena")));
            sender.sendMessage(Toolkit.translate(format.replace("%cmd%", "kp delarena")
                    .replace("%args%", "").replace("%desc%", "Removes an arena")));
            sender.sendMessage(Toolkit.translate(format.replace("%cmd%", "kp spawn")
                    .replace("%args%", "").replace("%desc%", "Teleports you to the local arena spawn")));

            sender.sendMessage(Toolkit.translate(format.replace("%cmd%", "kp create")
                    .replace("%args%", "<kitName>").replace("%desc%", "Creates a kit from your inventory")));
            sender.sendMessage(Toolkit.translate(format.replace("%cmd%", "kp delete")
                    .replace("%args%", "<kitName>").replace("%desc%", "Deletes an existing kit")));
            sender.sendMessage(Toolkit.translate(format.replace("%cmd%", "kp preview")
                    .replace("%args%", "<kitName>").replace("%desc%", "Preview the contents of a kit")));
            sender.sendMessage(Toolkit.translate(format.replace("%cmd%", "kp kits")
                    .replace("%args%", "").replace("%desc%", "Lists all available kits")));

            sender.sendMessage(Toolkit.translate(format.replace("%cmd%", "kp kit")
                    .replace("%args%", "<kitName>").replace("%desc%", "Select a kit")));
            sender.sendMessage(Toolkit.translate(format.replace("%cmd%", "kp kit")
                    .replace("%args%", "<kitName> <player>").replace("%desc%", "Attempts to select a kit for a player")));
            sender.sendMessage(Toolkit.translate(format.replace("%cmd%", "kp clear")
                    .replace("%args%", "").replace("%desc%", "Clears your current kit")));
            sender.sendMessage(Toolkit.translate(format.replace("%cmd%", "kp clear")
                    .replace("%args%", "<player>").replace("%desc%", "Clears a kit for a player")));
            sender.sendMessage(Toolkit.translate(format.replace("%cmd%", "kp stats")
                    .replace("%args%", "").replace("%desc%", "View your stats")));
            sender.sendMessage(Toolkit.translate(format.replace("%cmd%", "kp stats")
                    .replace("%args%", "<player>").replace("%desc%", "View the stats of another player")));
            sender.sendMessage(Toolkit.translate(format.replace("%cmd%", "kp menu")
                    .replace("%args%", "").replace("%desc%", "Displays the kits menu")));
            sender.sendMessage(Toolkit.translate(format.replace("%cmd%", "kp setstats")
                    .replace("%args%", "<player> <type> <amount>").replace("%desc%", "Change stats of a player")));
            sender.sendMessage(Toolkit.translate(format.replace("%cmd%", "kp export")
                    .replace("%args%", "").replace("%desc%", "Exports all stats to the new storage format")));
            String footer = messages.getString("Messages.Commands.HelpFooter");
            if (footer != null) {
                sender.sendMessage(Toolkit.translate(" "));
                sender.sendMessage(footer);
            }
            return true;

        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload") && hasPermission(sender, "kp.command.reload")) {

                resources.reload();
                CacheManager.clearCaches();
                arena.getMenus().getKitMenu().clearCache();

                sender.sendMessage(messages.getString("Messages.Commands.Reload"));
                return true;

            } else if (args[0].equalsIgnoreCase("export") && hasPermission(sender, "kp.command.export")) {

                File statsFile = new File(plugin.getDataFolder().getAbsolutePath() + "/stats.yml");

                if (statsFile.exists()) {
                    sender.sendMessage(Toolkit.translate("%prefix% &7Exporting data, this may take a while..."));
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            plugin.getDatabase().exportStats();
                        }
                    }.runTaskAsynchronously(plugin);
                    sender.sendMessage(Toolkit.translate("%prefix% &aStats successfully exported to database."));
                } else {
                    sender.sendMessage(Toolkit.translate("%prefix% &cNo stats.yml was found to export from."));
                }

                return true;

            } else if (args[0].equalsIgnoreCase("kits") && hasPermission(sender, "kp.command.kits")) {

                String message = "";

                for (String kitName : resources.getKitList(false)) {
                    String[] fileName = kitName.split(".yml", 2);
                    message += fileName[0] + ", ";
                }

                message = message.substring(0, message.length() - 2);

                sender.sendMessage(messages.getString("Messages.Commands.Kits").replace("%kits%", message));

                return true;

            }

        } else if (args.length == 2) {

            if (args[0].equalsIgnoreCase("clear") && hasPermission(sender, "kp.command.clear.other")) {

                String playerName = args[1];

                Player target = Bukkit.getPlayer(playerName);

                if (target != null && Toolkit.inArena(target)) {

                    clearKit(target);

                    target.sendMessage(messages.getString("Messages.Commands.Cleared"));
                    sender.sendMessage(messages.getString("Messages.Commands.ClearedOther").replace("%player%", target.getName()));

                } else {

                    sender.sendMessage(messages.getString("Messages.Error.Offline"));

                }

                return true;

            } else if (args[0].equalsIgnoreCase("delete") && hasPermission(sender, "kp.command.delete")) {

                String kitName = args[1];

                if (arena.getKits().isKit(kitName)) {
                    arena.getKits().deleteKit(kitName);
                    sender.sendMessage(messages.getString("Messages.Commands.Delete")
                            .replace("%kit%", kitName));
                } else {
                    sender.sendMessage(messages.getString("Messages.Error.Lost"));
                }

                return true;

            } else if (args[0].equalsIgnoreCase("stats") && hasPermission(sender, "kp.command.stats.other")) {

                String targetName = args[1];

                if (plugin.getDatabase().databaseTableContainsPlayer("stats", targetName)) {
                    sendStatsMessage(sender, targetName);
                } else {
                    sender.sendMessage(messages.getString("Messages.Error.Offline"));
                }

            }

        } else if (args.length == 3) {

            if (args[0].equalsIgnoreCase("kit") && hasPermission(sender, "kp.command.kit.other")) {

                String kitName = args[1];
                String playerName = args[2];

                Player target = Bukkit.getPlayer(playerName);

                if (target != null && Toolkit.inArena(target)) {

                    Kit kitToGive = arena.getKits().getKitByName(kitName);
                    arena.getKits().attemptToGiveKitToPlayer(target, kitToGive);

                    sender.sendMessage(messages.getString("Messages.Commands.KitOther").replace("%player%", playerName).replace("%kit%", kitName));

                } else {

                    sender.sendMessage(messages.getString("Messages.Error.Offline"));

                }

                return true;

            }

        } else if (args.length == 4) {

            if (args[0].equalsIgnoreCase("setstats") && hasPermission(sender, "kp.command.setstats")) {

                String playerName = args[1];
                String statsIdentifier = args[2];
                String possibleAmount = args[3];

                if (statsIdentifier.equalsIgnoreCase("kills") ||
                        statsIdentifier.equalsIgnoreCase("deaths") ||
                        statsIdentifier.equalsIgnoreCase("level") ||
                        statsIdentifier.equalsIgnoreCase("experience")) {

                    if (StringUtils.isNumeric(possibleAmount)) {

                        String playerUUID = plugin.getDatabase().usernameToUUID(playerName);

                        if (playerUUID != null) {

                            int amount = Integer.parseInt(possibleAmount);
                            arena.getStats().setStat(statsIdentifier, playerName, amount);
                            sender.sendMessage(resources.getMessages().getString("Messages.Commands.SetStats")
                                    .replace("%player%", playerName)
                                    .replace("%amount%", String.valueOf(amount))
                                    .replace("%type%", statsIdentifier));
                            return true;

                        } else {
                            sender.sendMessage(resources.getMessages().getString("Messages.Error.Offline"));
                        }

                    } else {

                        sender.sendMessage(resources.getMessages().getString("Messages.Error.InvalidNumber")
                                .replace("%number%", possibleAmount));

                    }

                } else {

                    sender.sendMessage(resources.getMessages().getString("Messages.Error.InvalidType")
                            .replace("%type%", statsIdentifier)
                            .replace("%types%", "kills, deaths, level, experience"));

                }

                return true;

            }

        }

        if (sender instanceof Player p) {

            if (args.length == 1) {

                if (args[0].equalsIgnoreCase("stats") && hasPermission(sender, "kp.command.stats")) {

                    sendStatsMessage(p, p.getName());

                } else if (args[0].equalsIgnoreCase("menu") && hasPermission(sender, "kp.command.menu")) {

                    arena.getMenus().getKitMenu().open(p);

                } else if (args[0].equalsIgnoreCase("spawn") && hasPermission(sender, "kp.command.spawn")) {

                    if (!config.contains("Arenas." + p.getWorld().getName())) {
                        p.sendMessage(messages.getString("Messages.Error.Arena").replace("%arena%", p.getWorld().getName()));
                    } else if (!plugin.isTeleporting(p)) {
                        PlayerArenaTeleportEvent event = new PlayerArenaTeleportEvent(p);
                        Bukkit.getPluginManager().callEvent(event);
                        if (event.isCancelled()) {
                            p.sendMessage(messages.getString("Messages.Error.TeleportDenied"));
                            return true;
                        }
                        plugin.startTeleport(p);

                        p.sendMessage(messages.getString("Messages.Commands.Teleporting"));
                        XSound.play(p, "ENTITY_ITEM_PICKUP, 1, -1");

                        int[] beforeLocation = new int[]{p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ()};

                        new BukkitRunnable() {
                            public int time = config.getInt("Spawn.Time") + 1;

                            @Override
                            public void run() {
                                time--;
                                if (!p.isOnline() || p.getGameMode() == GameMode.SPECTATOR) {
                                    plugin.cancelTeleport(p);
                                    cancel();
                                    return;
                                }
                                if (!plugin.isTeleporting(p)) {
                                    p.sendMessage(messages.getString("Messages.Error.TeleportCombat"));
                                    cancel();
                                    return;
                                }
                                if (beforeLocation[0] != p.getLocation().getBlockX() || beforeLocation[1] != p.getLocation().getBlockY() || beforeLocation[2] != p.getLocation().getBlockZ()) {
                                    p.sendMessage(messages.getString("Messages.Error.Moved"));
                                    plugin.cancelTeleport(p);
                                    cancel();
                                    return;
                                }
                                if (time != 0) { // Still waiting
                                    if (config.getBoolean("Spawn.UseActionBar"))
                                        ActionBar.sendActionBar(p, messages.getString("Messages.Commands.Time")
                                                .replace("%time%", String.valueOf(time)));
                                    else
                                        p.sendMessage(messages.getString("Messages.Commands.Time")
                                                .replace("%time%", String.valueOf(time)));
                                    XSound.play(p, "BLOCK_NOTE_BLOCK_SNARE, 1, 1");
                                } else { // Wait ended
                                    p.sendMessage(messages.getString("Messages.Commands.Teleport"));
                                    arena.toSpawn(p, p.getWorld().getName());
                                    if (config.getBoolean("Arena.ClearKitOnCommandSpawn"))
                                        clearKit(p);
                                    plugin.cancelTeleport(p);
                                    XSound.play(p, "ENTITY_ENDERMAN_TELEPORT, 1, 1");
                                    cancel();
                                }
                            }
                        }.runTaskTimer(plugin, 0L, 20L);

                    }

                } else if (args[0].equalsIgnoreCase("clear") && hasPermission(sender, "kp.command.clear")) {

                    clearKit(p);
                    p.sendMessage(messages.getString("Messages.Commands.Cleared"));
                    return true;

                } else if (args[0].equalsIgnoreCase("addspawn") && hasPermission(p, "kp.command.addspawn")) {

                    String arenaName = p.getWorld().getName();
                    int spawnNumber = Toolkit.getNextAvailable(config, "Arenas." + arenaName, 1000, false, 1);

                    Toolkit.saveLocationToResource(config, "Arenas." + arenaName + "." + spawnNumber, p.getLocation());

                    p.sendMessage(messages.getString("Messages.Commands.Added")
                            .replace("%number%", String.valueOf(spawnNumber))
                            .replace("%arena%", arenaName));
                    XSound.play(p, "ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1, 1");

                    return true;

                } else if (args[0].equalsIgnoreCase("delarena") && hasPermission(sender, "kp.command.delarena")) {

                    String arenaName = p.getWorld().getName();

                    if (config.contains("Arenas." + arenaName)) {

                        config.set("Arenas." + arenaName, null);
                        plugin.saveConfig();

                        p.sendMessage(messages.getString("Messages.Commands.Removed").replace("%arena%", arenaName));
                        XSound.play(p, "ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 1, 1");

                    } else {

                        p.sendMessage(messages.getString("Messages.Error.Arena"));

                    }

                    return true;

                }

            } else if (args.length == 2) {

                if (args[0].equalsIgnoreCase("arena") && hasPermission(sender, "kp.command.spawn")) {

                    String arenaName = args[1];

                    if (resources.getConfig().getBoolean("Arena.PreventArenaSignUseWithKit")) {
                        if (arena.getKits().hasKit(p.getName())) {
                            p.sendMessage(messages.getString("Messages.Error.KitInvalid"));
                            return true;
                        }
                    }

                    arena.toSpawn(p, arenaName);
                    return true;

                } else if (args[0].equalsIgnoreCase("preview") && hasPermission(sender, "kp.command.preview")) {

                    String kitName = args[1];

                    if (arena.getKits().isKit(kitName)) {

                        Kit kitToPreview = arena.getKits().getKitByName(kitName);
                        arena.getMenus().getPreviewMenu().open(p, kitToPreview);

                    } else {

                        p.sendMessage(messages.getString("Messages.Error.Lost"));

                    }

                } else if (args[0].equalsIgnoreCase("create") && hasPermission(sender, "kp.command.create")) {

                    String kitName = args[1];

                    if (!arena.getKits().isKit(kitName)) {

                        arena.getKits().createKit(p, kitName);

                        p.sendMessage(messages.getString("Messages.Commands.Create")
                                .replace("%kit%", kitName));

                    } else {

                        p.sendMessage(messages.getString("Messages.Error.Exists"));

                    }

                } else if (args[0].equalsIgnoreCase("kit")/* && hasPermission(sender, "kp.command.kit")*/) {

                    if (Toolkit.inArena(p)) {

                        String kitName = args[1];
                        Kit kitToGive = arena.getKits().getKitByName(kitName);

                        arena.getKits().attemptToGiveKitToPlayer(p, kitToGive);

                    } else {

                        p.sendMessage(messages.getString("Messages.Error.Location"));

                    }

                    return true;

                }

            }

        } else {

            sender.sendMessage(messages.getString("Messages.General.Player"));

        }
        return false;

    }

    private boolean hasPermission(CommandSender sender, String permission) {

        if (sender.hasPermission(permission)) {
            return true;
        }
        sender.sendMessage(messages.getString("Messages.General.Permission").replace("%permission%", permission));
        return false;

    }

    private void clearKit(Player p) {

        p.getInventory().setArmorContents(null);
        p.getInventory().clear();

        Toolkit.setMaxHealth(p, 20);
        if (!p.isDead()) p.setHealth(20.0);

        for (PotionEffect effect : p.getActivePotionEffects())
            p.removePotionEffect(effect.getType());

        if (config.getBoolean("Arena.GiveItemsOnClear"))
            arena.giveItems(p);

        arena.getKits().resetKit(p.getName());

    }

    private void sendStatsMessage(CommandSender receiver, String username) {

        for (String line : messages.getStringList("Messages.Stats.Message")) {
            receiver.sendMessage(arena.replaceBuiltInPlaceholdersIfPresent(line, username));
        }

    }

}
