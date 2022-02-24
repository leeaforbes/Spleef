// package com.javabean.mc.spleef;

// import java.util.HashMap;

// import org.bukkit.GameMode;
// import org.bukkit.block.BlockState;
// import org.bukkit.entity.EntityType;
// import org.bukkit.entity.Player;
// import org.bukkit.event.EventHandler;
// import org.bukkit.event.Listener;
// import org.bukkit.event.block.Action;
// import org.bukkit.event.block.BlockBreakEvent;
// import org.bukkit.event.block.BlockPlaceEvent;
// import org.bukkit.event.entity.EntityDamageEvent;
// import org.bukkit.event.entity.FoodLevelChangeEvent;
// import org.bukkit.event.player.PlayerInteractEvent;
// import org.bukkit.event.player.PlayerItemDamageEvent;
// import org.bukkit.event.player.PlayerMoveEvent;
// import org.bukkit.event.player.PlayerQuitEvent;
// import org.bukkit.inventory.EquipmentSlot;

// public class SpleefListener implements Listener{
	
// 	private SpleefGameManager gameManager;
// 	private HashMap<String, Arena> arenaMap = new HashMap<String, Arena>();
	
// 	public SpleefListener(SpleefGameManager ctfgm, HashMap<String, Arena> am){
// 		gameManager = ctfgm;
// 		arenaMap = am;
// 	}
	
// 	//prevents the player from losing hunger
// 	@EventHandler
//     public void onFoodLevelChange(FoodLevelChangeEvent event){
// 		if(event.getEntityType() == EntityType.PLAYER && gameManager.getPlayerGameArena((Player)event.getEntity()) != null){
// 			event.setCancelled(true);
// 		}
//     }
	
// 	//item durability will not decrease on use
// 	@EventHandler
// 	public void onPlayerItemDamage(PlayerItemDamageEvent event){
// 		if(gameManager.getPlayerGameArena(event.getPlayer()) != null){
// 			event.setCancelled(true);
// 		}
// 	}
	
// 	@EventHandler
// 	public void onEntityDamage(EntityDamageEvent event){
// 		if(event.getEntityType() == EntityType.PLAYER){
// 			Player victim = (Player) event.getEntity();
// 			if(gameManager.getPlayerGameArena(victim) != null){
// 				event.setCancelled(true);
// 			}
// 		}
// 	}
	
// 	@EventHandler
// 	public void onBreakBlock(BlockBreakEvent event){
// 		Arena playerArena = gameManager.getPlayerGameArena(event.getPlayer());
// 		if(playerArena != null){
// 			Floor floor = playerArena.getFloorOfBlock(event.getBlock().getLocation());
// 			if(gameManager.getGame(playerArena).isInProgress() && floor != null && event.getBlock().getBlockData().getMaterial() == floor.getFloorMaterial() && !gameManager.getGame(playerArena).isALoser(event.getPlayer().getName())){
// 				event.setDropItems(false);
// 				gameManager.getPlayerGameData(event.getPlayer(), playerArena).destroyBlock();
// 			}
// 			else{
// 				event.setCancelled(true);
// 			}
// 		}
// 	}
	
// 	@EventHandler
// 	public void onBlockPlace(BlockPlaceEvent event){
// 		if(gameManager.getPlayerGameArena(event.getPlayer()) != null){
// 			//if player is not OP
// 			if(!event.getPlayer().isOp()){
// 				event.setCancelled(true);
// 			}
// 			//player not in creative mode
// 			else if(event.getPlayer().getGameMode() != GameMode.CREATIVE){
// 				event.setCancelled(true);
// 			}
// 		}
// 	}
	
// 	@EventHandler
// 	public void onPlayerInteract(PlayerInteractEvent event){
// 		if(event.getHand() == EquipmentSlot.HAND && (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK)){
// 			signClickEvent(event);
// 		}
// 	}
	
// 	private void signClickEvent(PlayerInteractEvent event){
// 		if(Arena.isASign(event.getClickedBlock().getType())){
// 			BlockState signBlockState = event.getClickedBlock().getState();
// 			if(signBlockState.hasMetadata("spleefarena")){
// 				//send player to join that arena if possible
// 				String arenaName = signBlockState.getMetadata("spleefarena").get(0).asString();
// 				gameManager.attemptPlayerJoin(event.getPlayer(), arenaMap.get(arenaName));
// 			}
// 		}
// 	}
	
// 	@EventHandler
// 	public void onPlayerLeave(PlayerQuitEvent event){
// 		//force player to leave game if they are in one to avoid combat logging and holding flags when disconnected
// 		Arena playerArena = gameManager.getPlayerGameArena(event.getPlayer());
// 		if(playerArena != null){
// 			gameManager.playerLeave(event.getPlayer(), playerArena);
// 		}
// 	}
	
// 	@EventHandler
// 	public void onPlayerMove(PlayerMoveEvent event){
// 		Arena playerArena = gameManager.getPlayerGameArena(event.getPlayer());
// 		if(playerArena != null){
// 			if(event.getPlayer().getLocation().getBlockY() < playerArena.getLowestFloorY() && !gameManager.getGame(playerArena).isALoser(event.getPlayer().getName())){
// 				event.getPlayer().getInventory().clear();
// 				event.getPlayer().teleport(playerArena.getSpectateLocation());
// 				SpleefGame game = gameManager.getGame(playerArena);
// 				gameManager.getPlayerGameData(event.getPlayer(), playerArena).setTimeLasted(game.getTimeElapsed());
// 				game.addLoser(event.getPlayer().getName());
// 			}
// 		}
// 	}
// }
