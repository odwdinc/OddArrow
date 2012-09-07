package com.isitbroken.oddarrow;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class PbBlockListener implements Listener {

	private OddArrow plugin;

	public PbBlockListener(OddArrow oddArrow) {
		plugin = oddArrow;
		
	}
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		Block Location = event.getBlock();
		//event.getPlayer().sendMessage(Location.toString());
		
		if(plugin.LightMaterialHash.containsKey(Location)){
			Material setmet = plugin.LightMaterialHash.get(Location);
			event.getBlock().setType(setmet);
			plugin.LightMaterialHash.remove(Location);
			event.getPlayer().sendMessage("Created [] Replaced with "+setmet.name());
		}
	}
}
