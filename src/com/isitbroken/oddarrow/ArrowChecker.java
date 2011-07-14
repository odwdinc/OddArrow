package com.isitbroken.oddarrow;

import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class ArrowChecker implements Runnable{
	private OddArrow plugin;
	
	public ArrowChecker(OddArrow instantness) {
		plugin =  instantness;								//local instantness of OddArrow plugin.
	}
	
	public void crateOddArrow(Player player, Arrow arrow)	{
		plugin.Arrowtask.arrows.add(arrow);
		plugin.Arrowtask.arrowMode.put(arrow, plugin.playerListener.getArrowMode(player));
		plugin.Arrowtask.arrowMaterial.put(arrow, plugin.playerListener.getArrowMaterial(player));
	}
	
	@Override
	public void run() {
		//  method to look for all arrows not found by ArrowEffectApplier
		List<World> Worlds = plugin.getServer().getWorlds();
		for (int WorldId = 0; WorldId < Worlds.size(); WorldId++){
			World ThisWorld = Worlds.get(WorldId);
			List<Entity> entities = ThisWorld.getEntities();
			for (int entitieID = 0; entitieID < entities.size(); entitieID++){
				if(entities.get(entitieID) instanceof Arrow){
					Arrow ThisArrow = (Arrow) entities.get(entitieID);
					if(plugin.isPlayer((Player) ThisArrow.getShooter())){
						crateOddArrow((Player) ThisArrow.getShooter(),ThisArrow);
					}
				}
			}
		}
		
		
		
	}
	

}
