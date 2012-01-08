package com.isitbroken.oddarrow;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;

public class PbBlockListener extends BlockListener {

	private OddArrow plugin;

	public PbBlockListener(OddArrow oddArrow) {
		plugin = oddArrow;
		
	}
	@Override
	public void onBlockBreak(BlockBreakEvent event){
		Block Location = event.getBlock();
		//event.getPlayer().sendMessage(Location.toString());
		
		if(plugin.LightMaterialHash.containsKey(Location)){
			Material setmet = plugin.LightMaterialHash.get(Location);
			event.getBlock().setType(setmet);
			plugin.LightMaterialHash.remove(Location);
			//event.getPlayer().sendMessage("Created [] Replaced with "+setmet.name());
		}
	}
}
