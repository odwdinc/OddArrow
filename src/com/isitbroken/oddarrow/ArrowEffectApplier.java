package com.isitbroken.oddarrow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;

public class ArrowEffectApplier implements Runnable{
	
    HashMap<Arrow,Location> arrowLocs = new HashMap<Arrow,Location>();
    HashMap<Arrow,Integer> arrowMode = new HashMap<Arrow,Integer>();
    HashMap<Arrow, Material> arrowMaterial = new HashMap<Arrow, Material>();
    
    HashMap<Player, ArrayList<Arrow>> ArrowLists = new HashMap<Player, ArrayList<Arrow>>();
    
    ArrayList<Arrow> arrows=new ArrayList<Arrow>();
    
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
				incoming.getWorld().createExplosion(ThisArrow.getLocation(),(float) 4);
			}
			ThisArrow.remove();
			arrowList.remove(arrow);
		}
		ArrowLists.remove(incoming);

	}
    
    public void ArrowTodo(Arrow arrow){
    	switch (arrowMode.get(arrow)) {
    	case 0://rappade
    		arrow.getWorld().createExplosion(arrow.getLocation(), (float) 5);
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
    	}
    	
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
	
	public void timeTester(Arrow arrow){
		try {
			synchronized(this){
				boolean thistimere = true;
				long curent;
				long time  = arrow.getWorld().getTime();
				while(thistimere) {
					curent =  arrow.getWorld().getTime() - time;
					if (curent > 1){
						thistimere = false;
					}else{
						this.wait(1);
					}
				}
			}
		}catch (InterruptedException e){
		}
		
	}
    
    
    @Override
    public void run() {
        for(int i=0; i<arrows.size();i++){
        	
            Arrow arrow=(Arrow) arrows.get(i);
            
            if(arrowLocs.containsKey(arrow)){
            	
                Location loc=arrowLocs.get(arrow);
                
                timeTester(arrow);
                
                Location loca=arrow.getLocation();
   
                if(loc.getBlockX()==loca.getBlockX()&&loc.getBlockY()==loca.getBlockY() &&loc.getBlockZ()==loca.getBlockZ()){
                	try {
                		ArrowTodo(arrow);
                    	if (arrows.contains(arrow)){
                    		arrows.remove(arrow);
                    	}
                	} catch (NullPointerException e) {
                	}

                }else{
                    arrowLocs.put(arrow, arrow.getLocation());
                }
            }
            else{
                arrowLocs.put(arrow, arrow.getLocation());
            }
        }
    }
}
