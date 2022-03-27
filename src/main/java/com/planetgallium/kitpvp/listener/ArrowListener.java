package com.planetgallium.kitpvp.listener;

import com.cryptomorin.xseries.XMaterial;
import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.util.Resource;
import com.planetgallium.kitpvp.util.Toolkit;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class ArrowListener implements Listener {

	private Resource config;

	public ArrowListener(Game plugin) {
		this.config = plugin.getResources().getConfig();
	}

	@EventHandler
	public void onShot(EntityDamageByEntityEvent e) {

		if (!Toolkit.inArena(e.getEntity()) || !config.getBoolean("Combat.ArrowHit.Enabled")) return;

		if (e.getEntity() instanceof Player damagedPlayer && e.getDamager() instanceof Arrow arrow
				&& arrow.getShooter() != null && arrow.getShooter() instanceof Player shooter) {

			// ARROW HEALTH MESSAGE

            if (!damagedPlayer.getName().equals(shooter.getName())) {
                Bukkit.getScheduler().runTaskLater(Game.getInstance(), () -> {
                    double health = Math.round(damagedPlayer.getHealth() * 10D) / 10D;
                    if (shooter.hasPermission("kp.arrowmessage") && shooter != damagedPlayer) {
                        if (!Game.getInstance().getArena().getKits().hasKit(damagedPlayer.getName()) || health >= 20D)
                            return;
                        shooter.sendMessage(config.getString("Combat.ArrowHit.Message")
                                .replace("%player%", damagedPlayer.getName())
                                .replace("%health%", String.valueOf(health)));
                    }
                }, 2L);
            }

			// ARROW RETURN

			ItemStack arrowToAdd = new ItemStack(Material.ARROW);

			if (config.getBoolean("Combat.ArrowReturn.Enabled")) {

				for (ItemStack items : shooter.getInventory().getContents()) {

					if (items != null && items.getType() == XMaterial.ARROW.parseMaterial() && items.getAmount() < 64) {

						if (shooter.hasPermission("kp.arrowreturn")) {

							shooter.getInventory().addItem(arrowToAdd);
							shooter.getInventory().addItem(arrowToAdd);

							return;

						}

					}

				}

				if (shooter.getInventory().firstEmpty() == -1) {

					shooter.sendMessage(config.getString("Combat.ArrowReturn.NoSpace"));

				} else {

					shooter.getInventory().addItem(arrowToAdd);
					shooter.getInventory().addItem(arrowToAdd);

				}

			}

		}

	}

}
