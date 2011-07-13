package com.isitbroken.oddarrow;

import java.util.ArrayList;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;

public class Arrowtask extends OddArrow implements Runnable	{
	public Player ThisPlayer;
	
	public Arrowtask(Player arrowshooter) {
		ThisPlayer = arrowshooter;
	}

	@Override
	public synchronized void run() {
		boolean ArrowsInFlight = true;
		ArrayList<Arrow> arrowList = plugin.oddArrowListHash.get(ThisPlayer);
		while(ArrowsInFlight){
			ArrowsInFlight = false;
			for(int CurrentArrow = 0; CurrentArrow < arrowList.size(); CurrentArrow++) {
				
				Arrow ThisArrow = arrowList.get(CurrentArrow);
				
				if((!ThisArrow.isDead()) && (plugin.isPlayer(ThisArrow.getShooter()))){
					if(!plugin.getDidArrowMoved(ThisArrow)){								
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

							if (plugin.getDidArrowMoved(ThisArrow)){
								plugin.getArrowTodo((Player) ThisArrow.getShooter(), ThisArrow);
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
//logger.info("[OddArrow] Damond Going to sleep !");
}


}
