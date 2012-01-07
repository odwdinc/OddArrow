package com.isitbroken.oddarrow;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryManger {
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
	
	InventoryManger(){
		SADDLE.setType(Material.SADDLE);
		IRON_INGOT.setType(Material.IRON_INGOT);
		BREAD.setType(Material.BREAD);
		WHEAT.setType(Material.WHEAT);
		SULPHUR.setType(Material.SULPHUR);
		sSTRING.setType(Material.STRING);
		BUCKET.setType(Material.BUCKET);
		EGG.setType(Material.EGG);
		
		REDSTONE.setType(Material.REDSTONE);
		RECORD.setType(Material.RECORD_5);
		GOLDEN_APPLE.setType(Material.GOLDEN_APPLE);
		DIAMOND.setType(Material.DIAMOND);
	}

	public void RandomInventory(Inventory inventory) {
		int i;
		
		SADDLE.setAmount(0);
		IRON_INGOT.setAmount(0);
		BREAD.setAmount(0);
		WHEAT.setAmount(0);
		SULPHUR.setAmount(0);
		sSTRING.setAmount(0);
		BUCKET.setAmount(0);
		EGG.setAmount(0);
		REDSTONE.setAmount(0);
		RECORD.setAmount(0);
		GOLDEN_APPLE.setAmount(0);
		DIAMOND.setAmount(0);
		
		for (i = 0; i < 10; i++){
			if(Math.random() * 5 < 4){
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
			if(Math.random()*10 < 1){
				//Redstone Dust	1⁄10 (10%)	 1-4
				if(REDSTONE.getAmount()>4){
					REDSTONE.setAmount(REDSTONE.getAmount()+1);
				}
			}
			if(Math.random()*25 < 2){
				//Music Discs	2⁄25 (8%)	 1
				if(RECORD.getAmount()>1){
					RECORD.setAmount(RECORD.getAmount()+1);
				}
			}
			if(Math.random() * 100 < 8){
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
