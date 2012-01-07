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
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.Inventory;

public class ArrowChecker extends EntityListener{
	private OddArrow plugin;
	
	public ArrowChecker(OddArrow instantness) {
		plugin =  instantness;								//local instantness of OddArrow plugin.
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
			setMaterials(arrow, Material.GLOWSTONE, 1);
			arrow.remove();
			break;	
		case 3: //replace
			setMaterials(arrow, arrowMaterial.get(arrow), 2);
			arrow.remove();
			break;
		case 4://crate
			setMaterials(arrow, arrowMaterial.get(arrow), 0);
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
			
			thisplayer.sendMessage("Distince = "+distince);
			
			for (double i= 0; i < distince2; i++ ){
				
				Location Templocation = bridgebulder(Arrowlocation,playerlocation,i);
				
				if (playerlocation.distance(Templocation) < distince){
					Block thisblock = Templocation.getWorld().getBlockAt(Templocation);
					if( thisblock.getType() == Material.AIR) thisblock.setType(Material.STONE);
					
				}
			}
			
			arrow.remove();
			break;
		case 8://Mobs
			if(arrow.getWorld().getAllowMonsters()){
				arrow.getWorld().playEffect(arrow.getLocation(), Effect.SMOKE, 10);
				arrow.getWorld().spawnCreature(arrow.getLocation(),CreatureType.SKELETON);
			}else if(arrow.getWorld().getAllowAnimals()){
				arrow.getWorld().playEffect(arrow.getLocation(), Effect.SMOKE, 10);
				arrow.getWorld().spawnCreature(arrow.getLocation(),CreatureType.COW);
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
			
			thisplayer.sendMessage("Distince = "+distince);
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
				
		//plugin.logger.info(Temp.toString());
		return Temp;
		
	}
	
	

	public void setMaterials(Arrow arrow, Material material, Integer value ){
		if (value != 0 ){
			for(int x = -1*value; x < value; x++){
				for(int y = -1*value; y < value; y++){
					for(int z = -1*value; z < value; z++){				
						Location newlocation = new Location(arrow.getWorld(), arrow.getLocation().getX()+x,  arrow.getLocation().getY()+y,  arrow.getLocation().getZ()+z);
						if(!(arrow.getWorld().getBlockAt(newlocation).getType() == Material.AIR) ){
							arrow.getWorld().getBlockAt(newlocation).setType(material);
						}
					}
				}

			}
		}else{
			arrow.getWorld().getBlockAt(arrow.getLocation()).setType(arrowMaterial.get(arrow));
		}

	}
	
	public void onProjectileHit(ProjectileHitEvent event) {
				
		if(event.getEntity() instanceof Arrow){
			Arrow ThisArrow = (Arrow) event.getEntity();
			
			
			
			if(plugin.UseLocation){
				if(plugin.playerListener.arrowinzone(ThisArrow.getLocation()) == -1){
					return;
				}
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
