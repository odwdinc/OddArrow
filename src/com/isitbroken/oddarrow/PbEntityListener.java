package com.isitbroken.oddarrow;

import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;


public class PbEntityListener extends PlayerListener {
		
	private ItemStack stack = new ItemStack((Material) Material.ARROW, 64);
	private OddArrow plugin;
	
	public PbEntityListener(OddArrow instantince) {
		plugin =  instantince;
	}
	
	public void onPlayerInteract (PlayerInteractEvent event)
	{
		if (plugin.isPlayer(event.getPlayer())){
			if (event.getItem().getType() == Material.BOW){
				if (event.getPlayer().getInventory().contains(Material.ARROW)){
					plugin.lookforarrows(event.getPlayer());
				}else{
					event.getPlayer().sendMessage("You Have No Arrows!!");
					event.getPlayer().getInventory().addItem(stack);
					event.getPlayer().sendMessage("Have 64, go have some fun!!");
				}
			}
			
		}
	}
}
