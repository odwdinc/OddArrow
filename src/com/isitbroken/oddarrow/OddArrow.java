package com.isitbroken.oddarrow;

//import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;

import com.isitbroken.oddarrow.Arrowtask;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class OddArrow extends JavaPlugin{

	public static OddArrow plugin;
	
	public final HashMap<Player, ArrayList<Arrow>> oddArrowListHash = new HashMap<Player, ArrayList<Arrow>>();
	public final HashMap<Player, Float> oddArrowBlastSizeHash = new HashMap<Player, Float>();
	public final HashMap<Player, Material> arrowMaterialHash = new HashMap<Player, Material>();
	public final HashMap<Player, Integer > oddArrowModeHash = new HashMap<Player, Integer>();
	public final HashMap<Player, Boolean > oddArrowEnabledHash = new HashMap<Player, Boolean>();
	public final HashMap<Arrow, Double> arrowTestLocationHash = new HashMap<Arrow, Double>();

	public final HashMap<Player, Integer > oddArrowTaskHash = new HashMap<Player, Integer>();

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

	//@SuppressWarnings("deprecation")
	public void lookforarrows(Player Arrowshooter){

		if (isPlayer(Arrowshooter)){
			//Arrowshooter.sendMessage("Lest see Where that went");
			int thislooper;
			if(oddArrowTaskHash.containsKey(Arrowshooter)){
				thislooper = oddArrowTaskHash.get(Arrowshooter);
				getServer().getScheduler().cancelTask(thislooper);
			}

			thislooper = getServer().getScheduler().scheduleAsyncDelayedTask(this, new Arrowtask(Arrowshooter), 2);
			oddArrowTaskHash.put(Arrowshooter, thislooper);
		}else{
			//Arrowshooter.sendMessage("You need run /oa");
		}


	}

	public boolean isPlayer(final Player incoming){

		if (oddArrowEnabledHash.containsKey(incoming)){
			return oddArrowEnabledHash.get(incoming);
		}
		return false;
	}

	public boolean isPlayer(final LivingEntity incoming){

		if (oddArrowEnabledHash.containsKey(incoming)){
			return oddArrowEnabledHash.get(incoming);
		}
		return false;
	}

	public void setIfPlayer(final Player incoming, final Boolean value){
		if(value){
			oddArrowEnabledHash.put(incoming, value);
			incoming.sendMessage("[OddArrow] Welcomes you.");
			incoming.sendMessage("[OddArrow] [RappadFire]");
			setPlayerMode(incoming,0);
		}else{
			oddArrowEnabledHash.remove(incoming);
			incoming.sendMessage("[OddArrow] Will miss you.");
		}
	}

	public void setPlayerMode(final Player incoming, final Integer value){
		if (!oddArrowModeHash.containsKey(incoming)){

		}
		oddArrowModeHash.put(incoming, value);
	}

	public Integer getPlayerMode(final Player incoming){
		if (oddArrowModeHash.containsKey(incoming)){
			return oddArrowModeHash.get(incoming);
		}else{
			oddArrowModeHash.put(incoming, 0);
			return 0;
		}
	}


	public void setArrowMaterial(final Player incoming, final Material value){
		arrowMaterialHash.put(incoming, value);
	}

	public Material getArrowMaterial(final Player incoming){
		if (arrowMaterialHash.containsKey(incoming)){
			return arrowMaterialHash.get(incoming);
		}else{
			arrowMaterialHash.put(incoming, Material.GLOWSTONE);
			return Material.GLOWSTONE;
		}
	}

	public ArrayList<Arrow> getArrowList(final Player incoming){
		if(oddArrowListHash.containsKey(incoming)){
			return oddArrowListHash.get(incoming);
		}else{
			return new ArrayList<Arrow>();
		}
	}


	public void setArrowList(final Player incoming, final ArrayList<Arrow> value){
		if(oddArrowListHash.containsKey(incoming)){
			oddArrowListHash.put(incoming,value);
		}else{
			oddArrowListHash.put(incoming, new ArrayList<Arrow>());
		}

	}

	public float getBlastSiz(final Player incoming){
		if (oddArrowBlastSizeHash.containsKey(incoming)){
			return oddArrowBlastSizeHash.get(incoming);
		}else{
			oddArrowBlastSizeHash.put(incoming, (float) 5);
			return 5;
		}


	}

	public synchronized void  setOffArrowExplosions(Player incoming){

		if (oddArrowListHash.containsKey(incoming)){
			List<Arrow> arrowList = oddArrowListHash.get(incoming); 
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

	public synchronized void setLocationsMaterial(final Player ThisPlayer,final Location thisArrow, final Material thisMaterial, final Integer value ){
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

	public synchronized boolean getDidArrowMoved(Arrow thisArrow) {

		if (!arrowTestLocationHash.containsKey(thisArrow)){	
			//logger.info("[OddArrow] Found arrow!");
			arrowTestLocationHash.put(thisArrow, thisArrow.getLocation().getY());	
		}else{

			switch (getPlayerMode((Player) thisArrow.getShooter())) {
			}

			//if (thisArrow.getWorld().getBlockAt(thisArrow.getLocation()).getType() == Material.AIR){
			//	thisArrow.getWorld().getBlockAt(thisArrow.getLocation().add(0, 1, 0)).setType(Material.WOOL);
			//}

			if(arrowTestLocationHash.get(thisArrow).equals(thisArrow.getLocation().getY())){
				arrowTestLocationHash.remove(thisArrow);
				return true;
			}else{
				//logger.info("[OddArrow] Testing arrow " + thisArrow.getEntityId() + thisArrow.getVelocity().getY());
				arrowTestLocationHash.put(thisArrow, thisArrow.getLocation().getY());
			}

		}
		return false;
	}	


	public synchronized boolean clearArrowLocation(Arrow thisArrow) {

		if (!arrowTestLocationHash.containsKey(thisArrow)){	
			//logger.info("[OddArrow] Found arrow!");
			arrowTestLocationHash.put(thisArrow, (double) 0);
			return true;
		}else{
			return false;
		}
	}	

	public synchronized void getArrowTodo(Player ThisPlayer, Arrow thisArrow){
		switch (getPlayerMode(ThisPlayer)) {
		case 0://rappade
			oddArrowBlastSizeHash.put(ThisPlayer, (float) 5);
			thisArrow.getWorld().createExplosion(thisArrow.getLocation(), getBlastSiz(ThisPlayer));
			thisArrow.remove();
			break;
		case 1://remote
			if (oddArrowListHash.containsKey(ThisPlayer)){
				ArrayList<Arrow> ThisList = oddArrowListHash.get(ThisPlayer);
				ThisList.add(thisArrow);
				oddArrowListHash.put(ThisPlayer, ThisList);
			}else{
				ArrayList<Arrow> ThisList = new ArrayList<Arrow>();
				ThisList.add(thisArrow);
				oddArrowListHash.put(ThisPlayer, new ArrayList<Arrow>());
			}
			break;		
		case 2://light
			setLocationsMaterial(ThisPlayer,thisArrow.getLocation(), Material.GLOWSTONE, 1);
			thisArrow.remove();
			break;	
		case 3: //replace
			setLocationsMaterial(ThisPlayer,thisArrow.getLocation(),getArrowMaterial(ThisPlayer), 2);
			thisArrow.remove();
			break;
		case 4://crate
			setLocationsMaterial(ThisPlayer,thisArrow.getLocation(),getArrowMaterial(ThisPlayer), 0);
			thisArrow.remove();
			break;	
		case 5://top
			oddArrowBlastSizeHash.put(ThisPlayer, (float) 1);
			thisArrow.getWorld().createExplosion(thisArrow.getLocation(), getBlastSiz(ThisPlayer));
			thisArrow.remove();
			break;
		case 6://Lightning strike
			thisArrow.getWorld().strikeLightning(thisArrow.getLocation());
			thisArrow.remove();
			break;
		}

	}

	public boolean onPermission(Player player , String permission){
		if(getServer().getPluginManager().isPluginEnabled("PermissionsEx")){
			PermissionManager permissions = PermissionsEx.getPermissionManager();
			if(permissions.has(player, permission)){
				return false;
			}else{
				player.sendMessage("You need permission");
				return true;
			}
		}else{
			return false;
		}
		
	}

	public boolean onCommand (CommandSender sender, Command cmd, String commandLable, String[] args){
		// Permission check
		if(onPermission((Player) sender, "com.isitbroken.oddarrow.permission"))return false;
		if (isPlayer((Player) sender)){
			Player ThisPlayer = (Player) sender;

			if (commandLable.equalsIgnoreCase("Boom")){
				setOffArrowExplosions(ThisPlayer);
				return true;
			}else if (commandLable.equalsIgnoreCase("oar")){
				setIfPlayer(ThisPlayer,false);
				sender.sendMessage("[OddArrow] Disabled.");
				return true;
			}else if (commandLable.equalsIgnoreCase("oa")){
				if (args.length == 1 ){
					if(args[0].equalsIgnoreCase("Rapid")) {
						setPlayerMode(ThisPlayer, 0);
						ThisPlayer.sendMessage("[OddArrow] [Rapid Fire]");
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
					}else if(args[0].equalsIgnoreCase("Lightning")) {
						setPlayerMode(ThisPlayer, 6);
						ThisPlayer.sendMessage("[OddArrow] [Lightning strike]");
						return true;
					}else if(args[0].equalsIgnoreCase("Off")) {
						setPlayerMode(ThisPlayer, -1);
						ThisPlayer.sendMessage("[OddArrow] [Off]");
						return true;
					}		
				}else if (args.length == 2 ){
					
					if( getPlayerMode(ThisPlayer) == 3 || getPlayerMode(ThisPlayer) == 4) {
						Material ArrowMaterial;
						try
						{
							ArrowMaterial = Material.getMaterial(Integer.parseInt(args[1]));
						}
						catch(NumberFormatException nfe)
						{
							ArrowMaterial = Material.getMaterial(args[1]);
						}
						try{
							sender.sendMessage("[OddArrow] [ArrowMaterial "+ArrowMaterial.toString()+"]");
							setArrowMaterial(ThisPlayer,ArrowMaterial);
							return true;
						}catch(NullPointerException e){
							ThisPlayer.sendMessage("[OddArrow] Could not Find "+args[1]);
							return false;
						}
					}
				}else{
					setPlayerMode(ThisPlayer,getPlayerMode(ThisPlayer)+1);

					switch (getPlayerMode(ThisPlayer)) {
					case 0:
						ThisPlayer.sendMessage("[OddArrow] [Rapid Fire]");
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
						ThisPlayer.sendMessage("[OddArrow] [Lightning strike]");
						break;
					default:
						ThisPlayer.sendMessage("[OddArrow] [Off]");
						setPlayerMode(ThisPlayer, -1);
						break;	
					}
					return true;
				}
			}

		}else{
			setIfPlayer((Player) sender, true);
			return true;
		}	
		return false;
	}
}	
