package com.isitbroken.oddarrow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ArrowChecker implements Listener{
	private OddArrow plugin;
	
	public ArrowChecker(OddArrow oddArrow) {
		plugin = oddArrow;
	}

	HashMap<Arrow,Integer> arrowMode = new HashMap<Arrow,Integer>();
	HashMap<Arrow, Material> arrowMaterial = new HashMap<Arrow, Material>();
	HashMap<Player, ArrayList<Arrow>> ArrowLists = new HashMap<Player, ArrayList<Arrow>>();

	ArrayList<Arrow> arrows = new ArrayList<Arrow>();

	public ArrayList<Arrow> getArrowList(Player player){
		if(ArrowLists.containsKey(player)){
			return ArrowLists.get(player);
		}else{
			return new ArrayList<Arrow>();
		}
	}

	public void RemoteExplosions(Player incoming){
		List<Arrow> arrowList = getArrowList(incoming);
		for(int arrow = 0; arrow < arrowList.size(); arrow++) {
			Arrow ThisArrow = arrowList.get(arrow);
			if (!ThisArrow.isDead()){
				incoming.getWorld().createExplosion(ThisArrow.getLocation(),(float) plugin.BlastSize);
			}
			ThisArrow.remove();
			arrowList.remove(arrow);
		}
		ArrowLists.remove(incoming);

	}

	public void ArrowTodo(Arrow arrow){
		Location Arrowlocation;
		Location playerlocation;
		double distince2;
		double distince;
		CommandSender thisplayer;
		switch (arrowMode.get(arrow)) {
		case 0://Raped
		arrow.getWorld().createExplosion(arrow.getLocation(), (float) plugin.BlastSize);
		arrow.remove();
		break;
		case 1://remote
			if (ArrowLists.containsKey(arrow.getShooter())){
				ArrayList<Arrow> ThisList = ArrowLists.get(arrow.getShooter());
				ThisList.add(arrow);
				ArrowLists.put((Player) arrow.getShooter(), ThisList);
			}else{
				ArrayList<Arrow> ThisList = new ArrayList<Arrow>();
				ThisList.add(arrow);
				ArrowLists.put((Player) arrow.getShooter(), new ArrayList<Arrow>());
			}
			break;		
		case 2://light
			setMaterials(arrow, Material.GLOWSTONE, 1,false,true);
			arrow.remove();
			break;	
		case 3: //replace
			
			setMaterials(arrow, arrowMaterial.get(arrow), 2,plugin.UseInventory,false);
			arrow.remove();
			break;
		case 4://crate
			setMaterials(arrow, arrowMaterial.get(arrow), 0,plugin.UseInventory,false);
			arrow.remove();
			break;	
		case 5://top
			arrow.getWorld().createExplosion(arrow.getLocation(), (float) 1);
			arrow.remove();
			break;
		case 6://Lightning strike
			arrow.getWorld().strikeLightning(arrow.getLocation());
			arrow.remove();
			break;
		case 7://Bridges
			Arrowlocation = arrow.getLocation().add(0, -1, 0);
			playerlocation = arrow.getShooter().getLocation().add(0, -1, 0);			
					
			distince2 = playerlocation.distanceSquared(Arrowlocation);
			distince = playerlocation.distance(Arrowlocation);
			
			thisplayer = (Player) arrow.getShooter();
			
			thisplayer.sendMessage("Distance = "+distince);
			loop:
			for (double i= 0; i < distince2; i++ ){
				
				Location Templocation = bridgebulder(Arrowlocation,playerlocation,i);
				
				if (playerlocation.distance(Templocation) < distince){
					Block thisblock = Templocation.getWorld().getBlockAt(Templocation);
					if( thisblock.getType() == Material.AIR) {
						if(plugin.UseInventory){
							if(!setMaterials(arrow, arrowMaterial.get(arrow), 0,plugin.UseInventory,false)){
								break loop;
							}
						}else{
							thisblock.setType(plugin.BridgeMaterial);
						}
					}
					
				}
			}
			
			arrow.remove();
			break;
		case 8://Mobs
			if(arrow.getWorld().getAllowMonsters()){
				arrow.getWorld().playEffect(arrow.getLocation(), Effect.SMOKE, 10);
				
				arrow.getWorld().spawnEntity(arrow.getLocation(), EntityType.SKELETON);
			}else if(arrow.getWorld().getAllowAnimals()){
				arrow.getWorld().playEffect(arrow.getLocation(), Effect.SMOKE, 10);
				arrow.getWorld().spawnEntity(arrow.getLocation(),EntityType.COW);
			}else{
				arrow.getWorld().playEffect(arrow.getLocation(), Effect.SMOKE, 10);
			}
			
			arrow.remove();
			break;
		case 9://Measuring Tape
			Arrowlocation = arrow.getLocation().add(0, -1, 0);
			playerlocation = arrow.getShooter().getLocation().add(0, -1, 0);			
					
			distince2 = playerlocation.distanceSquared(Arrowlocation);
			distince = playerlocation.distance(Arrowlocation);
			
			thisplayer = (Player) arrow.getShooter();
			
			thisplayer.sendMessage("Distance = "+distince);
			break;
		case 10://Chests
			Arrowlocation = arrow.getLocation();
			
			Block thisblock = arrow.getWorld().getBlockAt(Arrowlocation);
			thisblock.setType(Material.CHEST);
			Chest thischest = (Chest) thisblock.getState();
			Inventory inventory = thischest.getInventory();
			plugin.inventorymanger.RandomInventory(inventory);
			arrow.remove();
			break;
		}

	}
	public Location bridgebulder (Location to, Location form, double point){
		point = point/50;
		Double TempX = (form.getX() + (to.getX() - form.getX() )*point);
		Double TempY = (form.getY() + (to.getY() - form.getY() )*point);
		Double TempZ = (form.getZ() + (to.getZ() - form.getZ() )*point);
		Location Temp = new Location(form.getWorld(),TempX,TempY,TempZ);
		return Temp;
		
	}
	
	

	public boolean setMaterials(Arrow arrow, Material material, Integer value, boolean b, boolean c ){
		Player temp = (Player) arrow.getShooter();
		PlayerInventory inv = temp.getInventory();
		ItemStack tempstack = new ItemStack(material);
		tempstack.setAmount(1);
		
		if(b){
			if(!material.isBlock()){
				temp.sendMessage("You can not do that with "+ material.name());
				return false;
			}
			if (value == 2 ){
				if (inv.contains(material, 16)){
					value = 2;
				}else if(inv.contains(material, 4)){
					value = 1;
				}else{
					temp.sendMessage("You have not enough "+ material.name());
					return false;
	
				}
			}
		}
		if (value != 0 ){
			for(int x = -1*value; x < value; x++){
				for(int y = -1*value; y < value; y++){
					for(int z = -1*value; z < value; z++){				
						Location newlocation = new Location(arrow.getWorld(), arrow.getLocation().getX()+x,  arrow.getLocation().getY()+y,  arrow.getLocation().getZ()+z);
						if(!(arrow.getWorld().getBlockAt(newlocation).getType() == Material.AIR) ){
							if(!b){
								if(c){
									if(!plugin.LightMaterialHash.containsKey(newlocation.getBlock())){
										plugin.LightMaterialHash.put(newlocation.getBlock(), newlocation.getBlock().getType());
									}
								}
								arrow.getWorld().getBlockAt(newlocation).setType(material);
								
							}else{
								if(inv.contains(material, 1)){
									HashMap<Integer, ItemStack> list = inv.removeItem(tempstack);
									if(list.size() == 0){
										arrow.getWorld().getBlockAt(newlocation).setType(material);
									}else{
										temp.sendMessage("You have no more "+ material.name());
										return false;
									}
								}else{
									temp.sendMessage("You have no more "+ material.name());
									return false;
								}
								
							}
							
						}
					}
				}

			}
		}else{
			if(!b){
				arrow.getWorld().getBlockAt(arrow.getLocation()).setType(material);
			}else{
				if(inv.contains(material, 1)){
					HashMap<Integer, ItemStack> list = inv.removeItem(tempstack);
					if(list.size() == 0){
						arrow.getWorld().getBlockAt(arrow.getLocation()).setType(material);
					}else{
						temp.sendMessage("You have no more "+ material.name());
					}
				}else{
					temp.sendMessage("You have no more "+ material.name());
					return false;
				}
				
			}
		}
		return true;
	}
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
				
		if(event.getEntity() instanceof Arrow){
			Arrow ThisArrow = (Arrow) event.getEntity();
			
			
		
			if(!plugin.playerListener.arrowinzone(ThisArrow.getLocation())){
					return;
			}
			
			if(ThisArrow.getShooter() instanceof Player == false){
				return;
			}
			if(arrows.contains(ThisArrow)){
				ArrowTodo(ThisArrow);
				if (arrows.contains(ThisArrow)){
					arrows.remove(ThisArrow);
				}
			}else if(plugin.isPlayer((Player) ThisArrow.getShooter())){
				//try {	
					Player plu = (Player) ThisArrow.getShooter();
					//plu.sendMessage("arrow hit");
					arrowMode.put(ThisArrow, plugin.playerListener.getArrowMode(plu));
					arrowMaterial.put(ThisArrow, plugin.playerListener.getArrowMaterial(plu));
					ArrowTodo(ThisArrow);
				//} catch (NullPointerException e) {
					//ThisArrow.remove();
				//}
			}
			
		}
		
	}

}
