package com.javabean.mc.spleef;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.Timer;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class SpleefGameManager implements ActionListener{
	//map with arena name key, CTF game value to ensure only one game per arena
	private HashMap<String, SpleefGame> games = new HashMap<String, SpleefGame>();
	
	//player name, Arena
	public static HashMap<String, Arena> playersInArena = new HashMap<String, Arena>();
	
	//the plugin
	Plugin plugin;
	
	//ticks once every 10 seconds to remove any empty games
	Timer gameGarbageCollector = new Timer(10000, this);
	
	public SpleefGameManager(Plugin p){
		plugin = p;
		gameGarbageCollector.setRepeats(true);
		gameGarbageCollector.setInitialDelay(0);
		gameGarbageCollector.start();
	}
	
	public void createGame(Arena arena){
		//called whenever player joins and game does not exist yet
		games.putIfAbsent(arena.getName(), new SpleefGame(arena, plugin));
	}
	
	//called whenever one player left or time runs out
	public void deleteGame(Arena arena){
		games.remove(arena.getName());
	}
	
	public void attemptPlayerJoin(Player player, Arena arena){
		//arena not playable
		if(!arena.isPlayable()){
			player.sendMessage(ChatColor.RED + "Arena: " + arena.getName() + " is not playable yet.");
		}
		//already in a game
		else if(getPlayerGameArena(player) != null){
			player.sendMessage(ChatColor.RED + "You are already in arena: " + arena.getName() + ". Leave with /spleef leave.");
		}
		//arena already has a game going or still setting up
		else if(getGame(arena) != null && getGame(arena).isInProgress()){
			player.sendMessage(ChatColor.RED + "Arena: " + arena.getName() + " has a game in progress or is being reset.");
		}
		else if(isFull(arena)){
			player.sendMessage(ChatColor.RED + "Arena: " + arena.getName() + " is full (" + getGame(arena).getNumPlayers() + "/" + arena.getSpawns().size() + ").");
		}
		else{
			//allow player to join arena game
			playerJoin(player, arena);
		}
	}
	
	public void playerJoin(Player player, Arena arena){
		if(games.get(arena.getName()) == null){
			createGame(arena);
		}
		games.get(arena.getName()).playerJoin(player);
		player.teleport(arena.getSpawns().get(games.get(arena.getName()).getNumPlayers()).getLocation());
		notifyPlayers(ChatColor.LIGHT_PURPLE + player.getName() + " joined arena: " + arena.getName() + ".", arena);
	}
	
	public void playerLeave(Player player, Arena arena){
		games.get(arena.getName()).playerLeave(player);
		if(games.get(arena.getName()).getNumPlayers() == 0){
			//no more players playing, so delete the game
			deleteGame(arena);
		}
	}
	
	public SpleefGame getGame(Arena arena){
		return games.get(arena.getName());
	}
	
	//returns arena of game player is in, otherwise null
	public Arena getPlayerGameArena(Player player){
		//will be null if not in a game
		return playersInArena.get(player.getName());
	}
	
	public PlayerGameData getPlayerGameData(Player player, Arena arena){
		return games.get(arena.getName()).getPlayerGameData(player.getName());
	}
	
	public String timeLeft(Arena arena){
		return games.get(arena.getName()).timeLeft();
	}
	
	public int getNumPlayersInArena(Arena arena){
		return games.get(arena.getName()).getNumPlayers();
	}
	
	public int getNumGames(){
		return games.size();
	}
	
	public void startArena(Arena arena){
		games.get(arena.getName()).start();
	}
	
	public boolean isInProgress(Arena arena){
		if(games.get(arena.getName()) == null){
			return false;
		}
		return games.get(arena.getName()).isInProgress();
	}
	
	public boolean isFull(Arena arena){
		//null if game does not exist, so it cannot be full
		if(getGame(arena) == null){
			return false;
		}
		return getGame(arena).isFull();
	}
	
	public void notifyPlayers(String message, Arena arena){
		games.get(arena.getName()).notifyPlayers(message);
	}
	
	public void getInfo(Player player){
		for(String arenaName : games.keySet()){
			TextComponent message = new TextComponent((games.get(arenaName).isInProgress() ? ChatColor.RED : ChatColor.GREEN) + "\u25CF " + ChatColor.GREEN + arenaName + ": " + games.get(arenaName).getInfo());
			if(games.get(arenaName).isInProgress()){
				message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.RED + arenaName + " is in progress")));
			}
			else{
				message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
						ChatColor.DARK_GREEN + "" + ChatColor.MAGIC + "|" + ChatColor.GREEN + "Join " + arenaName + ChatColor.DARK_GREEN + "" + ChatColor.MAGIC + "|")));
				message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/spleef join " + arenaName));
			}
			player.spigot().sendMessage(message);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == gameGarbageCollector){
			for(String arenaName : games.keySet()){
				//include in progress so that blocks can reset after game end
				if(games.get(arenaName).getNumPlayers() == 0 && !games.get(arenaName).isInProgress()){
					deleteGame(games.get(arenaName).getArena());
				}
			}
		}
	}
}
