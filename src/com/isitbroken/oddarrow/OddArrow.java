package com.isitbroken.oddarrow;

//import java.lang.Thread.State;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class OddArrow extends JavaPlugin{

	public static OddArrow plugin;
	public boolean UseLocation = false;
	ArrowChecker Arrowtask = new ArrowChecker(this);
	//ArrowEfectTask ArrowEfect = new ArrowEfectTask(this);

	public HashMap<Player, Boolean > oddArrowEnabledHash = new HashMap<Player, Boolean>();
	public HashMap<Player, Integer> oddArrowModeHash = new HashMap<Player, Integer>();
	public HashMap<Player, Material> arrowMaterialHash = new HashMap<Player, Material>();
	public HashMap<Location, Double > oddArrowZoneSize = new HashMap<Location, Double>();
	ArrayList<Location> oddLocation = new ArrayList<Location>();


	public final Logger logger = Logger.getLogger("Minecraft");

	PbEntityListener playerListener =  new PbEntityListener(this);

	static String mainDirectory = "plugins/OddArrow"; //sets the main directory for easy reference
	static File OddArrowdat = new File(mainDirectory + File.separator + "OddArrow.properties");
	static Properties prop = new Properties(); //creates a new properties file


	public int BlastSize;
	PluginDescriptionFile pdfFile;
	public InventoryManger inventorymanger;

	@Override
	public void onDisable() {
		this.logger.info("[OddArrow] is Disabled!");
	}

	@Override
	public void onEnable() {
		new File(mainDirectory).mkdir();
		inventorymanger = new InventoryManger();
		PluginManager pm = getServer().getPluginManager();		
		pm.registerEvent(Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);
		pm.registerEvent(Type.PLAYER_MOVE, playerListener, Priority.Normal, this);
		pm.registerEvent(Type.PROJECTILE_HIT, Arrowtask, Priority.Normal, this);

		pdfFile = this.getDescription();
		plugin = this;
		setupCommands();
		//BukkitScheduler bs=this.getServer().getScheduler();
		//bs.scheduleAsyncRepeatingTask(this, ArrowEfect,0, 30);

		try {
			loadProcedure();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.logger.info( "["+pdfFile.getName() + "] version " + pdfFile.getVersion() + " is Enabled");
	}

	public void loadProcedure() throws Exception { 

		if(OddArrowdat.exists()){
			FileInputStream in;
			try {
				in = new FileInputStream(OddArrowdat);
				prop.load(in); //loads the file contents of zones ("in" which references to the zones file) from the input stream.
				BlastSize = Integer.parseInt(prop.getProperty("BlastSize")); //explained below
				this.logger.info( "["+pdfFile.getName() + "] BlastSize " + BlastSize);
				
				if(prop.getProperty("UseLocations").equalsIgnoreCase("true")){
					UseLocation = true;
					this.logger.info("["+pdfFile.getName() + "] Useing Locations");
				}else{
					this.logger.info("["+pdfFile.getName() + "] Not Useing Locations");
				}
				this.logger.info("["+pdfFile.getName() + "] Loaded Property");

			} catch (FileNotFoundException e) {
				this.logger.info("["+pdfFile.getName() + "] Error loading Property");
			} //Creates the input stream
			catch (IOException e) {
				this.logger.info("["+pdfFile.getName() + "] Error loading Property");
			}

		}else{
			try { //try catch clause explained below in tutorial
				OddArrowdat.createNewFile(); //creates the file zones.dat
				FileOutputStream out = new FileOutputStream(OddArrowdat); //creates a new output steam needed to write to the file
				prop.put("BlastSize", "5"); //put the property ZoneCount with a value of 0 into the properties file, this will show up as ZoneCount=0 in the properties file.
				prop.put("UseLocations", "False");
				prop.store(out, "Edit this config!"); //You need this line! It stores what you just put into the file and adds a comment.
				out.flush();  //Explained below in tutorial
				out.close(); //Closes the output stream as it is not needed anymore.'
				this.logger.info("["+pdfFile.getName() + "] Crated New Property file");
			} catch (IOException ex) { 
				this.logger.info("["+pdfFile.getName() + "] Error Crating Property file");
			}
		}
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
			incoming.sendMessage("["+pdfFile.getName() + "] Welcomes you.");
			playerListener.setArrowMode(incoming, -1);
		}else{
			oddArrowEnabledHash.remove(incoming);
			incoming.sendMessage("["+pdfFile.getName() + "] Will miss you.");
		}
	}


	public void setupCommands(){
		playerListener.oddArrowModListHash.put(0, "Rapid");
		playerListener.oddArrowModListHash.put(1, "Remote");
		playerListener.oddArrowModListHash.put(2, "Light");
		playerListener.oddArrowModListHash.put(3, "Replace");
		playerListener.oddArrowModListHash.put(4, "Create");
		playerListener.oddArrowModListHash.put(5, "Topsoil");
		playerListener.oddArrowModListHash.put(6, "Lightning");
		playerListener.oddArrowModListHash.put(7, "Bridges");
		playerListener.oddArrowModListHash.put(8, "Mob");
		playerListener.oddArrowModListHash.put(9, "Measuring Tape");
		playerListener.oddArrowModListHash.put(10, "Chests");
		playerListener.oddArrowModListHash.put(-1, "Off");
	}

	public void PlayerMode(Player ThisPlayer, Integer Mode){
		switch (Mode) {
		case 0:		
			if(ThisPlayer.hasPermission("oddarrow.oa.rapid")) {
				playerListener.setArrowMode(ThisPlayer, 0);
				ThisPlayer.sendMessage("["+pdfFile.getName() + "] Rapid Fire");
				break;
			}

		case 1:	
			if(ThisPlayer.hasPermission("oddarrow.oa.remote")) {
				playerListener.setArrowMode(ThisPlayer, 1);
				ThisPlayer.sendMessage("["+pdfFile.getName() + "] Remote Explosions");
				ThisPlayer.sendMessage("               Type /boom to detonate.");
				break;
			}

		case 2:	
			if(ThisPlayer.hasPermission("oddarrow.oa.light")){
				playerListener.setArrowMode(ThisPlayer, 2);
				ThisPlayer.sendMessage("["+pdfFile.getName() + "] Create Light");
				break;
			}
		case 3:	
			if(ThisPlayer.hasPermission("oddarrow.oa.replace")) {
				playerListener.setArrowMode(ThisPlayer, 3);
				ThisPlayer.sendMessage("["+pdfFile.getName() + "] Replace With " + playerListener.getArrowMaterial(ThisPlayer) );
				ThisPlayer.sendMessage("               Type /oa replace <Block> to Change");
				break;
			}	
		case 4:	
			if(ThisPlayer.hasPermission("oddarrow.oa.create")) {
				playerListener.setArrowMode(ThisPlayer, 4);
				ThisPlayer.sendMessage("["+pdfFile.getName() + "] Create " + playerListener.getArrowMaterial(ThisPlayer) );
				ThisPlayer.sendMessage("               Type /oa create <Block> to Change");
				break;
			}
		case 5:	
			if(ThisPlayer.hasPermission("oddarrow.oa.topsoil")) {
				playerListener.setArrowMode(ThisPlayer, 5);
				ThisPlayer.sendMessage("["+pdfFile.getName() + "] Topsoil removal");
				break;
			}

		case 6:	
			if(ThisPlayer.hasPermission("oddarrow.oa.lightning")) {
				playerListener.setArrowMode(ThisPlayer, 6);
				ThisPlayer.sendMessage("["+pdfFile.getName() + "] Lightning strike");
				break;
			}

		case 7:	
			if(ThisPlayer.hasPermission("oddarrow.bridges")) {
				playerListener.setArrowMode(ThisPlayer, 7);
				ThisPlayer.sendMessage("["+pdfFile.getName() + "] Bridges!");
				break;
			}
		case 8:	
			if(ThisPlayer.hasPermission("oddarrow.mobs")) {
				playerListener.setArrowMode(ThisPlayer, 7);
				ThisPlayer.sendMessage("["+pdfFile.getName() + "] Mobs!");
				break;
			}
		case 9:	
			if(ThisPlayer.hasPermission("oddarrow.mtape")) {
				playerListener.setArrowMode(ThisPlayer, 7);
				ThisPlayer.sendMessage("["+pdfFile.getName() + "] Measuring Tape!");
				break;
			}
		case 10:	
			if(ThisPlayer.hasPermission("oddarrow.chests")) {
				playerListener.setArrowMode(ThisPlayer, 7);
				ThisPlayer.sendMessage("["+pdfFile.getName() + "] Chests!");
				break;
			}
		default:	
			if(ThisPlayer.hasPermission("oddarrow.oa")) {
				playerListener.setArrowMode(ThisPlayer, -1);
				ThisPlayer.sendMessage("["+pdfFile.getName() + "] [Off]");
			}
			break;
		}
	}


	public boolean onCommand (CommandSender sender, Command cmd, String commandLable, String[] args){
		// Permission check
		if(sender instanceof Player){
			Player ThisPlayer = (Player)sender;
			if(ThisPlayer.hasPermission("oddarrow.Enabled")){
				if (isPlayer(ThisPlayer)){
					if (commandLable.equalsIgnoreCase("Boom") && ThisPlayer.hasPermission("oddarrow.oa.boom")){
						Arrowtask.RemoteExplosions(ThisPlayer);
						return true;
					}

					if (commandLable.equalsIgnoreCase("oar") && ThisPlayer.hasPermission("oddarrow.oa")){
						setIfPlayer(ThisPlayer,false);
						sender.sendMessage("["+pdfFile.getName() + "] Disabled.");
						return true;
					}


					if (commandLable.equalsIgnoreCase("oa") && ThisPlayer.hasPermission("oddarrow.oa")){
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
							if(args[0].equalsIgnoreCase("debug") && ThisPlayer.hasPermission("oddarrow.debug")){
								String Output ="";
								List<Arrow> debugEntities = Arrowtask.arrows;
								for (int i = 0; i < debugEntities.size(); i++){
									Arrow ThisArrow =debugEntities.get(i);
									Output = Output + ThisArrow.toString();
								}
								ThisPlayer.sendMessage("Curent Location = "+ ThisPlayer.getLocation().toString());

								ThisPlayer.sendMessage(" ");

								ThisPlayer.sendMessage("Arrow in list = "+ Output);
								return true;
							}

						}else if (args.length == 2 ){
							if(args[0].equalsIgnoreCase("loc") && ThisPlayer.hasPermission("oddarrow.loc") && UseLocation){
								Location thisloction = ThisPlayer.getLocation();
								oddLocation.add(thisloction);
								oddArrowZoneSize.put(thisloction,(double) Integer.parseInt(args[1]));
								sender.sendMessage("["+pdfFile.getName() + "] New location set @ your Location Size:"+Integer.parseInt(args[1]));
								return true;

							}else if( playerListener.getArrowMode(ThisPlayer) == 3 || playerListener.getArrowMode(ThisPlayer) == 4) {
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
									if(ThisPlayer.hasPermission("oddarrow.oa.Material."+ArrowMaterial.toString())){
										sender.sendMessage("[OddArrow] [ArrowMaterial "+ArrowMaterial.toString()+"]");
										playerListener.setArrowMaterial(ThisPlayer,ArrowMaterial);
										return true;
									}
								}catch(NullPointerException e){
									ThisPlayer.sendMessage("["+pdfFile.getName() + "] Could not Find "+args[1]);
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
		return false;
	}
}	
