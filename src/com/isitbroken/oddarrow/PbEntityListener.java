package com.isitbroken.oddarrow;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;


public class PbEntityListener implements Listener {
	
	public HashMap<Player, Integer> oddArrowModeHash = new HashMap<Player, Integer>();
	public HashMap<Player, Material> arrowMaterialHash = new HashMap<Player, Material>();
	public final HashMap<Player, Float> oddArrowBlastSizeHash = new HashMap<Player, Float>();
	public final HashMap<Integer, String> oddArrowModListHash = new HashMap<Integer, String>();
	
	private ItemStack stack = new ItemStack((Material) Material.ARROW, 64);  // crate a stack of 64 arrows for giving if player runs out. 
	
	private OddArrow plugin;
	
	public PbEntityListener(OddArrow instantness) {
		plugin =  instantness;								//local instantness of OddArrow plugin.
	}
	

	public Material getArrowMaterial(Player player){
		
		if (arrowMaterialHash.containsKey(player)){ 		//if we have player in arrowMaterialHash.
			return arrowMaterialHash.get(player);			//Get the Material from the arrowMaterialHash and return.
		}else{
			arrowMaterialHash.put(player, Material.GLOWSTONE);	//Set Default Material "Material.GLOWSTONE" to the players arrowMaterialHash.
			return Material.GLOWSTONE;							//return with "Material.GLOWSTONE".
		}
	}
	
	public Integer getArrowMode(Player player){		
		if (oddArrowModeHash.containsKey(player)){
			return oddArrowModeHash.get(player);
		}else{
			oddArrowModeHash.put(player, 0);
			return 0;
		}	
	}
	
	public void setArrowMaterial(Player player, Material mode){
		arrowMaterialHash.put(player, mode);
	}
	
	public void setArrowMode(Player player,Integer mode ){
		
		oddArrowModeHash.put(player, mode);
	}
	
	
	public void toggleArrowMode(Player player){
		if(getArrowMode(player).intValue() >= oddArrowModListHash.size()){
			oddArrowModeHash.put(player, -1);
		}else{
			Integer val = getArrowMode(player);
			Integer newval = new Integer(val.intValue()+1);
			plugin.PlayerMode(player, newval);
		}
	}
	
	
	public void crateOddArrow(Player player)	{
		try{
		Arrow arrow = player.launchProjectile(Arrow.class);
		plugin.Arrowtask.arrows.add(arrow);
		plugin.Arrowtask.arrowMode.put(arrow, getArrowMode(player));
		plugin.Arrowtask.arrowMaterial.put(arrow, getArrowMaterial(player));
		} catch (ArrayIndexOutOfBoundsException e){
			player. launchProjectile(Arrow.class);
		}
		
	}
	
	public boolean arrowinzone(Location ToTest){
		if(plugin.UseProtectedLocations){
			for(int oddloc = 0; oddloc < plugin.PoddLocation.size(); oddloc++ ){
			
				double distance = ToTest.distance(plugin.PoddLocation.get(oddloc));
			
				if(distance < plugin.PoddArrowZoneSize.get(plugin.PoddLocation.get(oddloc))){
					return false;
				}
			}
		}
		if(plugin.UseProtectedLocations && !plugin.UseLocation){
			return false;
		}
		if(plugin.UseLocation){
			for(int oddloc = 0; oddloc < plugin.oddLocation.size(); oddloc++ ){
				
				double distance = ToTest.distance(plugin.oddLocation.get(oddloc));
				
				if(distance < plugin.oddArrowZoneSize.get(plugin.oddLocation.get(oddloc))){
					return true;
				}
			}
		}
		if(!plugin.UseProtectedLocations && !plugin.UseLocation){
			return true;
		}
		return false;
		
	}
	
	@EventHandler
	public void onPlayerInteract (PlayerInteractEvent event)
	{
		Player player=event.getPlayer();
		if (plugin.isPlayer(player) && plugin.UseInventory){
			PlayerInventory inventory = player.getInventory();
			ItemStack item = inventory.getItem(8);
			if(item != null){
				Material met = item.getType();
				if(!met.equals(Material.AIR)){
					plugin.playerListener.setArrowMaterial(player, met);
				}
			}
		}
				
		if ((plugin.isPlayer(player)) && (event.getItem().getType() == Material.BOW)){
			
			boolean inzone = arrowinzone(player.getLocation());
			
			if(event.getAction()==Action.RIGHT_CLICK_AIR && inzone ){
				if (player.getInventory().contains(Material.ARROW)){
					//event.setCancelled(true);
					//crateOddArrow(player);
					//player.sendMessage("Arrow shot!");
				}else{
					if(player.hasPermission("oddarrow.oa.allammo")){
					event.getPlayer().sendMessage("You Have No Arrows!!");
					event.getPlayer().getInventory().addItem(stack);
					event.getPlayer().sendMessage("Have 64, go have some fun!!");
					}
				}
			}else if(event.getAction()==Action.LEFT_CLICK_AIR){
				//this cycles through effects when the user left clicks air with bow thanks skeletonofchaos!
				toggleArrowMode(player);
			}else{
				//player.sendMessage("There are " +plugin.oddLocation.size() + " OddZones in game");
			}
		}else{
			//player.sendMessage("Not using bow");
		}

	}

}
