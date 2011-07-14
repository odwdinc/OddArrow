package com.isitbroken.oddarrow;

//import java.lang.Thread.State;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class OddArrow extends JavaPlugin{

	public static OddArrow plugin;
	
	ArrowEffectApplier Arrowtask = new ArrowEffectApplier();
	
	ArrowChecker ArrowDubbleCheck = new ArrowChecker(this);
	
	public final HashMap<Player, Boolean > oddArrowEnabledHash = new HashMap<Player, Boolean>();

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
		setupCommands();
		BukkitScheduler bs=this.getServer().getScheduler();
        bs.scheduleAsyncRepeatingTask(this, Arrowtask, 1, 2);
        //bs.scheduleAsyncRepeatingTask(this, ArrowDubbleCheck, 1, 50);
	}


	public boolean isPlayer(final Player incoming){

		if (oddArrowEnabledHash.containsKey(incoming)){
			return oddArrowEnabledHash.get(incoming);
		}
		return false;
	}

	public void setIfPlayer(final Player incoming, final Boolean value){
		if(value){
			oddArrowEnabledHash.put(incoming, value);
			incoming.sendMessage("[OddArrow] Welcomes you.");
			playerListener.setArrowMode(incoming, -1);
		}else{
			oddArrowEnabledHash.remove(incoming);
			incoming.sendMessage("[OddArrow] Will miss you.");
		}
	}

	public boolean Permission(Player player , String permission){
		if(getServer().getPluginManager().isPluginEnabled("PermissionsEx")){
			PermissionManager permissions = PermissionsEx.getPermissionManager();
			if(permissions.has(player, permission)){
				return true;
			}else{
				player.sendMessage("You need permission "+permission);
				return false;
			}
		}else{
			return true;
		}
		
	}
	
	
	public void setupCommands(){
		playerListener.oddArrowModListHash.put(0, "Rapid");
		playerListener.oddArrowModListHash.put(1, "Remote");
		playerListener.oddArrowModListHash.put(2, "Light");
		playerListener.oddArrowModListHash.put(3, "Replace");
		playerListener.oddArrowModListHash.put(4, "Create");
		playerListener.oddArrowModListHash.put(5, "Topsoil");
		playerListener.oddArrowModListHash.put(6, "Lightning]");
		playerListener.oddArrowModListHash.put(-1, "Off");
	}
	
	public void PlayerMode(Player ThisPlayer, Integer Mode){
		switch (Mode) {
		case 0:		
			if(Permission(ThisPlayer, "oddarrow.oa.rapid")) {
				playerListener.setArrowMode(ThisPlayer, 0);
				ThisPlayer.sendMessage("[OddArrow] Rapid Fire");
			}
			break;
		case 1:	
			if(Permission(ThisPlayer, "oddarrow.oa.remote")) {
				playerListener.setArrowMode(ThisPlayer, 1);
				ThisPlayer.sendMessage("[OddArrow] Remote Explosions");
				ThisPlayer.sendMessage("               Type /boom to detonate.");
			}
			break;
		case 2:	
			if(Permission(ThisPlayer, "oddarrow.oa.light")){
				playerListener.setArrowMode(ThisPlayer, 2);
				ThisPlayer.sendMessage("[OddArrow] Create Light");
			}
			break;
		case 3:	
			if(Permission(ThisPlayer, "oddarrow.oa.replace")) {
				playerListener.setArrowMode(ThisPlayer, 3);
				ThisPlayer.sendMessage("[OddArrow] Replace With " + playerListener.getArrowMaterial(ThisPlayer) );
				ThisPlayer.sendMessage("               Type /oa replace <Block> to Change");
			}
			break;
		case 4:	
			if(Permission(ThisPlayer, "oddarrow.oa.create")) {
				playerListener.setArrowMode(ThisPlayer, 4);
				ThisPlayer.sendMessage("[OddArrow] Create " + playerListener.getArrowMaterial(ThisPlayer) );
				ThisPlayer.sendMessage("               Type /oa create <Block> to Change");
			}
			break;
		case 5:	
			if(Permission(ThisPlayer, "oddarrow.oa.topsoil")) {
				playerListener.setArrowMode(ThisPlayer, 5);
				ThisPlayer.sendMessage("[OddArrow] Topsoil removal");
			}
			break;
		case 6:	
			if(Permission(ThisPlayer, "oddarrow.oa.lightning")) {
				playerListener.setArrowMode(ThisPlayer, 6);
				ThisPlayer.sendMessage("[OddArrow] Lightning strike");	
			}
			break;
		default:	
			if(Permission(ThisPlayer, "oddarrow.oa")) {
				playerListener.setArrowMode(ThisPlayer, -1);
				ThisPlayer.sendMessage("[OddArrow] [Off]");
			}
			break;
		}
	}
	

	public boolean onCommand (CommandSender sender, Command cmd, String commandLable, String[] args){
		// Permission check
		if(Permission((Player) sender, "oddarrow.Enabled")){
			if (isPlayer((Player) sender)){
				Player ThisPlayer = (Player) sender;
				
				if (commandLable.equalsIgnoreCase("Boom") && Permission(ThisPlayer, "oddarrow.oa.boom")){
					Arrowtask.RemoteExplosions(ThisPlayer);
					return true;
				}
				
				if (commandLable.equalsIgnoreCase("oar") && Permission(ThisPlayer, "oddarrow.oa")){
					setIfPlayer(ThisPlayer,false);
					sender.sendMessage("[OddArrow] Disabled.");
					return true;
				}
				

				if (commandLable.equalsIgnoreCase("oa") && Permission(ThisPlayer, "oddarrow.oa")){
					if (args.length == 1 ){

						if(playerListener.oddArrowModListHash.containsValue(args[0])){
							for(int key = 0; key < playerListener.oddArrowModListHash.size(); key++){
								String ThisMode = playerListener.oddArrowModListHash.get(key);
								if(args[0].equalsIgnoreCase(ThisMode)){
									PlayerMode(ThisPlayer, key);
									return true;
								}
							}
						}		
					}else if (args.length == 2 ){

						if( playerListener.getArrowMode(ThisPlayer) == 3 || playerListener.getArrowMode(ThisPlayer) == 4) {
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
								if(Permission(ThisPlayer, "oddarrow.oa.Material."+ArrowMaterial.toString())){
									sender.sendMessage("[OddArrow] [ArrowMaterial "+ArrowMaterial.toString()+"]");
									playerListener.setArrowMaterial(ThisPlayer,ArrowMaterial);
									return true;
								}
							}catch(NullPointerException e){
								ThisPlayer.sendMessage("[OddArrow] Could not Find "+args[1]);
								return false;
							}

						}
					}else{

						playerListener.toggleArrowMode(ThisPlayer);
						return true;
					}
				}

			}else{
				setIfPlayer((Player) sender, true);
				return true;
			}	
			return false;
		}
		return true;
	}
	
}	
