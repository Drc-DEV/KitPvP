package com.planetgallium.kitpvp.listener;

import com.cryptomorin.xseries.XSound;
import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.game.Arena;
import com.planetgallium.kitpvp.util.Resource;
import com.planetgallium.kitpvp.util.Toolkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class HitListener implements Listener {

	private final Arena arena;
	private final Resource config;
	private final XSound.Record hitSound;

	public HitListener(Game plugin) {
		this.arena = plugin.getArena();
		this.config = plugin.getResources().getConfig();

		String soundString = config.getString("Combat.HitSound.Sound") + ", 1, " + config.getInt("Combat.HitSound.Pitch");
		this.hitSound = XSound.parse(soundString);
	}

	@EventHandler
	public void onHit(EntityDamageByEntityEvent e) {

		if (e.getEntity() instanceof Player victim && e.getDamager() instanceof Player attacker && Toolkit.inArena(victim)) {
			arena.getHitCache().put(victim.getName(), attacker.getName());
			Game.getInstance().cancelTeleport(victim);
			Game.getInstance().cancelTeleport(attacker);
			if (config.getBoolean("Combat.HitSound.Enabled")) {
				hitSound.forPlayer(victim).play();
				hitSound.forPlayer(attacker).play();
			}
		}

	}
	
}
