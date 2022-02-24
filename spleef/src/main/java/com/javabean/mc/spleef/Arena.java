package com.javabean.mc.spleef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import net.md_5.bungee.api.ChatColor;

public class Arena{
	private String arenaName;
	private double floor = Double.MAX_VALUE;
	private ArrayList<Location> spawns = new ArrayList<>();
	private Location spectateTeleport;
	private ArrayList<Material> mineableMaterials = new ArrayList<>();

	private LinkedList<Location> signLocations = new LinkedList<Location>();
	public static HashMap<Material, Boolean> wallMaterials = new HashMap<Material, Boolean>();

	public Arena(String arenaName){
		this.arenaName = arenaName;
	}
	
	public String getName(){
		return arenaName;
	}
	
	public double getFloor(){
		return floor;
	}

	public void setFloor(double floor){
		this.floor = floor;
	}
	
	public void addSpawn(Location spawn){
		spawns.add(spawn);
	}

	public Location getSpawn(int index){
		return spawns.get(index);
	}
	
	public int numSpawns(){
		return spawns.size();
	}
	
	public Location getSpectateLocation(){
		return spectateTeleport;
	}
	
	public void setSpectateLocation(Location loc){
		spectateTeleport = loc;
	}

	public void setMineableMaterials(ArrayList<Material> mineableMaterials){
		this.mineableMaterials = mineableMaterials;
	}

	public ArrayList<Material> getMineableMaterials(){
		return mineableMaterials;
	}
	
	//call this before allowing a players to play in this arena
	public boolean isPlayable(){
		return floor != Double.MAX_VALUE && spawns.size() >= 2;
	}
	
	public void createJoinSign(Player player, Plugin plugin){
		if(createJoinSign(Spleef.getBlockLookingAt(player), plugin)){
			player.sendMessage(ChatColor.GREEN + "Sign created successfully.");
		}
		else{
			player.sendMessage(ChatColor.RED + "You must be looking at an existing sign.");
		}
	}
	
	//returns true if it is a sign and created successfully
	public boolean createJoinSign(Location lookingAt, Plugin plugin){
		//if is a sign
		if(isASign(lookingAt.getBlock().getType())){
			Block signBlock = lookingAt.getBlock();
			BlockState signBlockState = signBlock.getState();
			Sign sign = (Sign)signBlockState;
			sign.setLine(0, ChatColor.DARK_RED + "" + ChatColor.BOLD + "" + ChatColor.UNDERLINE + "[Spleef]");
			sign.setLine(1, ChatColor.AQUA + "" + ChatColor.BOLD + "" + getName());
			sign.setLine(2, ChatColor.DARK_GREEN + "" + numSpawns() + " players");
			sign.setMetadata("spleefarena", new FixedMetadataValue(plugin, getName()));
			sign.update();
			signBlockState.update();
			signLocations.add(lookingAt);
			return true;
		}
		return false;
	}
	
	public void removeJoinSign(Location lookingAt, Plugin plugin){
		Iterator<Location> signIterator = signLocations.iterator();
		while(signIterator.hasNext()){
			Location signLocation = signIterator.next();
			if(lookingAt.getWorld().getName() == signLocation.getWorld().getName() && lookingAt.getX() == signLocation.getX() && lookingAt.getY() == signLocation.getY() && lookingAt.getZ() == signLocation.getZ()){
				signLocation.getBlock().getState().removeMetadata("spleefarena", plugin);
				signIterator.remove();
			}
		}
	}
	
	public boolean isAJoinSign(Location lookingAt){
		
		for(Location signLocation : signLocations){
			if(lookingAt.getWorld().getName() == signLocation.getWorld().getName() && lookingAt.getX() == signLocation.getX() && lookingAt.getY() == signLocation.getY() && lookingAt.getZ() == signLocation.getZ()){
				return true;
			}
		}
		return false;
	}
	
	public static boolean isASign(Material materialInQuestion){
		return wallMaterials.get(materialInQuestion) != null;
	}
	
	public LinkedList<Location> getSigns(){
		return signLocations;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("Arena: ");
		sb.append(arenaName);
		sb.append("Players: " + numSpawns());
		return sb.toString();
	}
}
