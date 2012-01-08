package com.isitbroken.oddarrow;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryManger{
	private ItemStack SADDLE;
	private ItemStack IRON_INGOT;
	private ItemStack BREAD;
	private ItemStack WHEAT;
	private ItemStack SULPHUR;
	private ItemStack sSTRING;
	private ItemStack BUCKET;
	private ItemStack EGG;
	private ItemStack REDSTONE;
	private ItemStack RECORD;
	private ItemStack GOLDEN_APPLE;
	private ItemStack DIAMOND;
	
	InventoryManger(OddArrow oddArrow){
		SADDLE= new ItemStack(Material.SADDLE);
		IRON_INGOT= new ItemStack(Material.IRON_INGOT);
		BREAD= new ItemStack(Material.BREAD);
		WHEAT= new ItemStack(Material.WHEAT);
		SULPHUR= new ItemStack(Material.SULPHUR);
		sSTRING= new ItemStack(Material.STRING);
		BUCKET= new ItemStack(Material.BUCKET);
		EGG= new ItemStack(Material.EGG);
		
		REDSTONE= new ItemStack(Material.REDSTONE);
		RECORD= new ItemStack(Material.RECORD_5);
		GOLDEN_APPLE= new ItemStack(Material.GOLDEN_APPLE);
		DIAMOND= new ItemStack(Material.DIAMOND);
		
	}
	

	public void RandomInventory(Inventory inventory) {
		int i;
		
		SADDLE.setAmount(-1);
		IRON_INGOT.setAmount(-1);
		BREAD.setAmount(-1);
		WHEAT.setAmount(-1);
		SULPHUR.setAmount(-1);
		sSTRING.setAmount(-1);
		BUCKET.setAmount(-1);
		EGG.setAmount(-1);
		REDSTONE.setAmount(-1);
		RECORD.setAmount(-1);
		GOLDEN_APPLE.setAmount(-1);
		DIAMOND.setAmount(-1);
		
		for (i = 0; i < 10; i++){
			if(Math.random() < .8){
				if(SADDLE.getAmount()>1){
					SADDLE.setAmount(SADDLE.getAmount()+1);
				}
				if(IRON_INGOT.getAmount()>4){
					IRON_INGOT.setAmount(IRON_INGOT.getAmount()+1);
				}
				if(BREAD.getAmount()>1){
					BREAD.setAmount(BREAD.getAmount()+1);
				}
				if(WHEAT.getAmount()>1){
					WHEAT.setAmount(WHEAT.getAmount()+1);
				}
				if(SULPHUR.getAmount()>4){
					SULPHUR.setAmount(SULPHUR.getAmount()+1);
				}
				if(sSTRING.getAmount()>4){
					sSTRING.setAmount(sSTRING.getAmount()+1);
				}
				if(BUCKET.getAmount()>1){
					BUCKET.setAmount(BUCKET.getAmount()+1);
				}
				if(EGG.getAmount()>2){
					EGG.setAmount(EGG.getAmount()+1);
				}

			}
			if(Math.random() < .3){
				//Redstone Dust	1⁄10 (10%)	 1-4
				if(REDSTONE.getAmount()>4){
					REDSTONE.setAmount(REDSTONE.getAmount()+1);
				}
			}
			if(Math.random() < .25){
				//Music Discs	2⁄25 (8%)	 1
				if(RECORD.getAmount()>1){
					RECORD.setAmount(RECORD.getAmount()+1);
				}
			}
			if(Math.random() < .01){
				if(GOLDEN_APPLE.getAmount()>1){
					GOLDEN_APPLE.setAmount(GOLDEN_APPLE.getAmount()+1);
				}
				//Diamond	1⁄125 (0.8%)	 1-2
				if(DIAMOND.getAmount()>2){
					DIAMOND.setAmount(DIAMOND.getAmount()+1);
				}
			
			}
		}
		inventory.addItem(SADDLE);
		inventory.addItem(IRON_INGOT);
		inventory.addItem(BREAD);
		inventory.addItem(WHEAT);
		inventory.addItem(SULPHUR);
		inventory.addItem(sSTRING);
		inventory.addItem(BUCKET);
		inventory.addItem(EGG);
		inventory.addItem(REDSTONE);
		inventory.addItem(RECORD);
		inventory.addItem(GOLDEN_APPLE);
		inventory.addItem(DIAMOND);
	}
}
