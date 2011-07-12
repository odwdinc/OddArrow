package com.isitbroken.oddarrow;

//import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;


import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class OddArrow extends JavaPlugin{
	
	public static OddArrow plugin;
	
	public final HashMap<Player, ArrayList<Arrow>> OddArrowArrows = new HashMap<Player, ArrayList<Arrow>>();
	public final HashMap<Player, Float> OddArrowBlastSize = new HashMap<Player, Float>();
	public final HashMap<Player, Material> ArrowMaterial = new HashMap<Player, Material>();
	public final HashMap<Player, Integer > OddArrowMode = new HashMap<Player, Integer>();
	public final HashMap<Player, Boolean > OddArrowEnabled = new HashMap<Player, Boolean>();
	public final HashMap<Arrow, Double> startlocation = new HashMap<Arrow, Double>();
	
	public final HashMap<Player, Integer > OddArrowLooper = new HashMap<Player, Integer>();
	
	public final Logger logger = Logger.getLogger("Minecraft");
	
	PbEntityListener playerListener =  new PbEntityListener(this);
	
	@Override
	public void onDisable() {
		this.logger.info("OddArrow is Disabled!");
	}
	
	@Override
	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info( pdfFile.getName() + " version " + pdfFile.getVersion() + " is Enabled");
		plugin = this;	
	}
	
	
	public Runnable Arrowtask = new Runnable() 
	{
		@Override
		public synchronized void run() {
			boolean ArrowsInFlight = true;
			//logger.info("[OddArrow] Damond Starting..");
			
			List<World> Worldliat = plugin.getServer().getWorlds();
			
			while(ArrowsInFlight){
				ArrowsInFlight = false;
				for(int CurentWorld = 0; CurentWorld < Worldliat.size(); CurentWorld++) {
					World Thisworld = Worldliat.get(CurentWorld);
					
					List<Entity> arrowList = Thisworld.getEntities();
					
					for(int CurrentArrow = 0; CurrentArrow < arrowList.size(); CurrentArrow++) {
						Entity ThisIsArrow = arrowList.get(CurrentArrow);
						if(ThisIsArrow instanceof Arrow){
							Arrow ThisArrow = (Arrow) ThisIsArrow;
							if((!ThisArrow.isDead()) && (plugin.isLivingEntity(ThisArrow.getShooter()))){
								if(!plugin.TestArrow(ThisArrow)){								
										try {
											boolean thistimere = true;
											long time  = ThisArrow.getWorld().getTime();
											long curent;
											while(thistimere) {
												curent =  ThisArrow.getWorld().getTime()- time;
												if (curent > 1){
													thistimere = false;
												}else{
													//plugin.logger.info("Time "+ curent);
													this.wait(1);
												}
											}
											
											if (plugin.TestArrow(ThisArrow)){
												plugin.ThisOddArrow((Player) ThisArrow.getShooter(), ThisArrow);
											}else{
												ArrowsInFlight = true;
											}
										} catch (InterruptedException e) {
											//plugin.logger.info("interpt");
											//plugin.clearArrow(ThisArrow);
											return;
										} catch (NullPointerException e){
											ArrowsInFlight = true;
										}
								
								}else{
									ArrowsInFlight = true;
								}
								
								
							}		
						}
					}
					
					
				}
				
			}
			//logger.info("[OddArrow] Damond Going to sleep !");
		}
	};
	
	//@SuppressWarnings("deprecation")
	public void lookforarrows(Player Arrowshooter){
		
		if (isPlayer(Arrowshooter)){
			//Arrowshooter.sendMessage("Lest see Where that went");
			int thislooper;
			if(OddArrowLooper.containsKey(Arrowshooter)){
				thislooper = OddArrowLooper.get(Arrowshooter);
				getServer().getScheduler().cancelTask(thislooper);
			}
			
			thislooper = getServer().getScheduler().scheduleAsyncDelayedTask(this, Arrowtask, 2);
			OddArrowLooper.put(Arrowshooter, thislooper);
		}else{
			//Arrowshooter.sendMessage("You need run /oa");
		}
		
		
	}
	
	public boolean isPlayer(final Player incoming){
		
		if (OddArrowEnabled.containsKey(incoming)){
			return OddArrowEnabled.get(incoming);
		}
		return false;
	}
	
	public boolean isLivingEntity(final LivingEntity incoming){
		
		if (OddArrowEnabled.containsKey(incoming)){
			return OddArrowEnabled.get(incoming);
		}
		return false;
	}
	
	public void enablePlayer(final Player incoming, final Boolean value){
		if(value){
			OddArrowEnabled.put(incoming, value);
			incoming.sendMessage("[OddArrow] Welcomes you.");
			incoming.sendMessage("[OddArrow] [RappadFire]");
			setPlayerMode(incoming,0);
		}else{
			OddArrowEnabled.remove(incoming);
			incoming.sendMessage("[OddArrow] Will miss you.");
		}
	}
	
	public void setPlayerMode(final Player incoming, final Integer value){
		if (!OddArrowMode.containsKey(incoming)){
			
		}
		OddArrowMode.put(incoming, value);
	}
	
	public Integer getPlayerMode(final Player incoming){
		if (OddArrowMode.containsKey(incoming)){
			return OddArrowMode.get(incoming);
		}else{
			OddArrowMode.put(incoming, 0);
			return 0;
		}
	}
	
	
	public void setArrowMaterial(final Player incoming, final Material value){
			ArrowMaterial.put(incoming, value);
	}
	
	public Material getArrowMaterial(final Player incoming){
		if (ArrowMaterial.containsKey(incoming)){
			return ArrowMaterial.get(incoming);
		}else{
			ArrowMaterial.put(incoming, Material.GLOWSTONE);
			return Material.GLOWSTONE;
		}
	}
	
	public ArrayList<Arrow> getArrowList(final Player incoming){
		if(OddArrowArrows.containsKey(incoming)){
			return OddArrowArrows.get(incoming);
		}else{
			return new ArrayList<Arrow>();
		}
	}
	
	
	public void setArrowList(final Player incoming, final ArrayList<Arrow> value){
		if(OddArrowArrows.containsKey(incoming)){
			 OddArrowArrows.put(incoming,value);
		}else{
			OddArrowArrows.put(incoming, new ArrayList<Arrow>());
		}
			
	}
	
	public float getBlastSiz(final Player incoming){
		if (OddArrowBlastSize.containsKey(incoming)){
			return OddArrowBlastSize.get(incoming);
		}else{
			OddArrowBlastSize.put(incoming, (float) 5);
			return 5;
		}
		
		
	}
	
	public synchronized void  ArrowExplosions(Player incoming){
		
		if (OddArrowArrows.containsKey(incoming)){
			List<Arrow> arrowList = OddArrowArrows.get(incoming); 
			for(int arrow = 0; arrow < arrowList.size(); arrow++) {
				Arrow ThisArrow = arrowList.get(arrow);
				if (!ThisArrow.isDead()){
					incoming.getWorld().createExplosion(ThisArrow.getLocation(), getBlastSiz(incoming));
					ThisArrow.remove();
					arrowList.remove(arrow);
				}else{
					arrowList.remove(arrow);
				}
			}
			arrowList.clear();
		}
		
	}
	
	
	public Float getBlastzise(Player ThisPlayer){
		if(OddArrowBlastSize.containsKey(ThisPlayer)){
			return OddArrowBlastSize.get(ThisPlayer);
		}else{
			OddArrowBlastSize.put(ThisPlayer, (float) 5);
			return (float) 5;
		}
	}
	
	public synchronized void ThisOddArrowbulder(final Player ThisPlayer,final Location thisArrow, final Material thisMaterial, final Integer value ){
		if (value != 0 ){
			for(int x = -1*value; x < value; x++){
				for(int y = -1*value; y < value; y++){
					for(int z = -1*value; z < value; z++){				
						Location newlocation = new Location(ThisPlayer.getWorld(), thisArrow.getX()+x, thisArrow.getY()+y, thisArrow.getZ()+z);
						if(!(thisArrow.getWorld().getBlockAt(newlocation).getType() == Material.AIR) ){
							thisArrow.getWorld().getBlockAt(newlocation).setType(thisMaterial);
						}
					}
				}
				
			}
		}else{
			thisArrow.getWorld().getBlockAt(thisArrow).setType(thisMaterial);
		}
		
	}
	
	public synchronized boolean TestArrow(Arrow thisArrow) {
		
		if (!startlocation.containsKey(thisArrow)){	
			//logger.info("[OddArrow] Found arrow!");
			startlocation.put(thisArrow, thisArrow.getLocation().getY());	
		}else{
			
			//if (thisArrow.getWorld().getBlockAt(thisArrow.getLocation()).getType() == Material.AIR){
			//	thisArrow.getWorld().getBlockAt(thisArrow.getLocation().add(0, 1, 0)).setType(Material.WOOL);
			//}
			
			if(startlocation.get(thisArrow).equals(thisArrow.getLocation().getY())){
				startlocation.remove(thisArrow);
				return true;
			}else{
				//logger.info("[OddArrow] Testing arrow " + thisArrow.getEntityId() + thisArrow.getVelocity().getY());
				startlocation.put(thisArrow, thisArrow.getLocation().getY());
			}
			
		}
		return false;
	}	
	
	
	public synchronized boolean clearArrow(Arrow thisArrow) {
		
		if (!startlocation.containsKey(thisArrow)){	
			//logger.info("[OddArrow] Found arrow!");
			startlocation.put(thisArrow, (double) 0);
			return true;
		}else{
			return false;
		}
	}	
	
	public void ArrowList(Player thisPlayer) {
		
		List<Entity> arrowList = thisPlayer.getNearbyEntities(100, 100, 100);
		
		for(int arrow = 0; arrow < arrowList.size(); arrow++) {
			Entity curr;
			curr = arrowList.get(arrow);
			if(curr instanceof Arrow){
				LivingEntity entity = ((Arrow) curr).getShooter();
				
				thisPlayer.sendMessage("[] " + entity.toString());
			}
		}
		
	}
	
	
	public synchronized void ThisOddArrow(Player ThisPlayer, Arrow thisArrow){
		switch (getPlayerMode(ThisPlayer)) {
		case 0://rappade
			OddArrowBlastSize.put(ThisPlayer, (float) 5);
			thisArrow.getWorld().createExplosion(thisArrow.getLocation(), getBlastzise(ThisPlayer));
			thisArrow.remove();
			break;
		case 1://remote
			if (OddArrowArrows.containsKey(ThisPlayer)){
				ArrayList<Arrow> ThisList = OddArrowArrows.get(ThisPlayer);
				ThisList.add(thisArrow);
				OddArrowArrows.put(ThisPlayer, ThisList);
			}else{
				ArrayList<Arrow> ThisList = new ArrayList<Arrow>();
				ThisList.add(thisArrow);
				OddArrowArrows.put(ThisPlayer, new ArrayList<Arrow>());
			}
			break;		
		case 2://light
			ThisOddArrowbulder(ThisPlayer,thisArrow.getLocation(), Material.GLOWSTONE, 1);
			thisArrow.remove();
			break;	
		case 3: //replace
			ThisOddArrowbulder(ThisPlayer,thisArrow.getLocation(),getArrowMaterial(ThisPlayer), 2);
			thisArrow.remove();
			break;
		case 4://crate
			ThisOddArrowbulder(ThisPlayer,thisArrow.getLocation(),getArrowMaterial(ThisPlayer), 0);
			thisArrow.remove();
			break;	
		case 5://top
			OddArrowBlastSize.put(ThisPlayer, (float) 1);
			thisArrow.getWorld().createExplosion(thisArrow.getLocation(), getBlastzise(ThisPlayer));
			thisArrow.remove();
			break;
		}
	
		
	}




	public boolean onCommand (CommandSender sender, Command cmd, String commandLable, String[] args){
		
		if (isPlayer((Player) sender)){
			Player ThisPlayer = (Player) sender;
			
			if (commandLable.equalsIgnoreCase("Boom")){
				ArrowExplosions(ThisPlayer);
				return true;
			}else if (commandLable.equalsIgnoreCase("listoa")){
				ArrowList(ThisPlayer);
				return true;
			}else if (commandLable.equalsIgnoreCase("oar")){
				enablePlayer(ThisPlayer,false);
				sender.sendMessage("[OddArrow] Disabled.");
				return true;
			}else if (commandLable.equalsIgnoreCase("oa")){
				if (args.length == 1 ){
							 if(args[0].equalsIgnoreCase("Rappad")) {
								 setPlayerMode(ThisPlayer, 0);
								 ThisPlayer.sendMessage("[OddArrow] [RappadFire]");
								 return true;
							 }else if(args[0].equalsIgnoreCase("Remote")) {
								 setPlayerMode(ThisPlayer, 1);
								 ThisPlayer.sendMessage("[OddArrow] [Remote Explosions] type (/boom) to detonate.");
								 return true;
							 }else if(args[0].equalsIgnoreCase("Light")){
								 setPlayerMode(ThisPlayer, 2);
								 ThisPlayer.sendMessage("[OddArrow] [Create Light]");
								 return true;
							 }else if(args[0].equalsIgnoreCase("Replace")) {
								 setPlayerMode(ThisPlayer, 3);
								 ThisPlayer.sendMessage("[OddArrow] [Replace With" + getArrowMaterial(ThisPlayer) + "]");
								 return true;
							 }else if(args[0].equalsIgnoreCase("Create")) {
								 setPlayerMode(ThisPlayer, 4);
								 ThisPlayer.sendMessage("[OddArrow] [Create " + getArrowMaterial(ThisPlayer) + "]");
								 return true;
							 }else if(args[0].equalsIgnoreCase("Topsoil")) {
								 setPlayerMode(ThisPlayer, 5);
								 ThisPlayer.sendMessage("[OddArrow] [Topsoil removal]");
								 return true;
							 }else if(args[0].equalsIgnoreCase("Off")) {
								 setPlayerMode(ThisPlayer, -1);
								 ThisPlayer.sendMessage("[OddArrow] [Off]");
								 return true;
							 }else if( getPlayerMode(ThisPlayer) == 3 || getPlayerMode(ThisPlayer) == 4) {
								 Material ArrowMaterial;
								 try
								 {
									ArrowMaterial = Material.getMaterial(Integer.parseInt(args[0]));
								 }
								 catch(NumberFormatException nfe)
								 {
									 ArrowMaterial = Material.getMaterial(args[0]);
								 }
								 try{
									 sender.sendMessage("[OddArrow] [ArrowMaterial "+ArrowMaterial.toString()+"]");
									 setArrowMaterial(ThisPlayer,ArrowMaterial);
									 return true;
								 }catch(NullPointerException e){
									 ThisPlayer.sendMessage("[OddArrow] Could not Find "+args[0]);
									 return false;
								 }		
							 }
				}else{
					setPlayerMode(ThisPlayer,getPlayerMode(ThisPlayer)+1);
					
					switch (getPlayerMode(ThisPlayer)) {
						case 0:
							ThisPlayer.sendMessage("[OddArrow] [RappadFire]");
							break;
						case 1:
							ThisPlayer.sendMessage("[OddArrow] [Remote Explosions] type (/boom) to detonate.");
							break;		
						case 2:
							ThisPlayer.sendMessage("[OddArrow] [Create Light]");
							break;	
						case 3:
							ThisPlayer.sendMessage("[OddArrow] [Replace With" + getArrowMaterial(ThisPlayer) + "]");
							break;
						case 4:
							ThisPlayer.sendMessage("[OddArrow] [Create " + getArrowMaterial(ThisPlayer) + "]");
							break;
						case 5:
							ThisPlayer.sendMessage("[OddArrow] [Topsoil removal]");
							break;
						case 6:
							ThisPlayer.sendMessage("[OddArrow] [Off]");
							setPlayerMode(ThisPlayer, -1);
							break;	
					}
					return true;
				}
			}
			
		}else{
			enablePlayer((Player) sender, true);
			return true;
		}
		return false;
	}
	
	
	
}
