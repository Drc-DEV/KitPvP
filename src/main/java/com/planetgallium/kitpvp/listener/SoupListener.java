package com.planetgallium.kitpvp.listener;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.util.Resource;
import com.planetgallium.kitpvp.util.Toolkit;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SoupListener implements Listener {
	
	private Resource config;
	private int soupBoost;
	
	public SoupListener(Game plugin) {
		this.config = plugin.getResources().getConfig();
		this.soupBoost = plugin.getConfig().getInt("Soups.RegenAmount");
	}

	@EventHandler
	public void onDamage(PlayerDeathEvent e) {
		Player victim = e.getEntity();
		if (!Toolkit.inArena(victim) || victim.getKiller() == null) return;
		Player killer = victim.getKiller();
		if (config.getBoolean("Kill.SoupReward.Enabled") && killer.hasPermission("kp.soupreturn")) {
			Bukkit.getScheduler().runTaskLater(Game.getInstance(), () -> {
				if (killer.isOnline()) { // incase they logged off
					int count = 0;
					for (int i = 0; i < 36; i++) {
						if (killer.getInventory().getItem(i) == null) {
							count++;
						}
					}
					if (count < config.getInt("Kill.SoupReward.Amount")) {
						killer.sendMessage(config.getString("Kill.SoupReward.NoSpace")
								.replace("%amount%", String.valueOf((config.getInt("Kill.SoupReward.Amount") - count))));
					} else {
						count = config.getInt("Kill.SoupReward.Amount");
					}
					ItemStack soup = XMaterial.MUSHROOM_STEW.parseItem();
					if (soup != null) {
						ItemMeta soupMeta = soup.getItemMeta();
						soupMeta.setDisplayName(config.getString("Soups.Name"));
						soupMeta.setLore(Toolkit.colorizeList(config.getStringList("Soups.Lore")));
						soup.setItemMeta(soupMeta);
					}
					for (int r = 0; r < count; r++) {
						killer.getInventory().addItem(soup);
					}
				}
			}, config.getInt("Kill.SoupReward.Delay") * 20L);
		}
	}

	@EventHandler
	public void useSoup(PlayerInteractEvent e) {

		if (!config.getBoolean("Soups.Enabled")) return;
		Player p = e.getPlayer();
		if (!Toolkit.inArena(p)) return;
		if ((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
				&& (Toolkit.getMainHandItem(p).getType() == XMaterial.MUSHROOM_STEW.parseMaterial() || Toolkit.getOffhandItem(p).getType() == XMaterial.MUSHROOM_STEW.parseMaterial())) {

			e.setCancelled(true);

			if (p.getHealth() < 20.0) {
				p.setHealth(Math.min(p.getHealth() + (double) soupBoost, 20.0));
				String soundString = config.getString("Soups.Sound");
				if (soundString != null)
					XSound.matchXSound(soundString).ifPresent(s -> {
						Sound parsedSound = s.parseSound();
						if (parsedSound != null)
							p.playSound(p.getLocation(), parsedSound, 1, (float) config.getInt("Soups.Pitch"));
					});
				ItemStack newSoup = new ItemStack(config.getBoolean("Soups.RemoveAfterUse") ? XMaterial.AIR.parseItem() : XMaterial.BOWL.parseItem());
				if (Toolkit.getMainHandItem(p).getType() == XMaterial.MUSHROOM_STEW.parseMaterial()) {
					Toolkit.setMainHandItem(p, newSoup);
				} else if (Toolkit.getOffhandItem(p).getType() == XMaterial.MUSHROOM_STEW.parseMaterial()) {
					Toolkit.setOffhandItem(p, newSoup);
				}
			}
		}
	}

	
}
