package me.seven.reintmase.tocheckplayer.Manager;

import me.seven.reintmase.tocheckplayer.Array.Container;
import me.seven.reintmase.tocheckplayer.Main;
import me.seven.reintmase.tocheckplayer.PluginCommands.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

public class Listeners implements Listener {

    private final Container container;
    private final PlayerCheck playerCheck;
    private final FileConfiguration config;

    private final String blockCommandMessage;

    public Listeners(){
        container = Main.getInstance().getContainer();
        config = Main.getInstance().getConfig();
        playerCheck = Main.getInstance().getPlayerCheck();

        this.blockCommandMessage = StringUtils.hex(config.getString("messages.block-command-message"));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if(container.getPlayers().containsKey(player)){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if(container.getPlayers().containsKey(player)){
            playerCheck.quitBan(player, container.getPlayerHashMap().get(player));
            container.getPlayers().remove(player);
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {

        Player player = event.getPlayer();
        String message = event.getMessage();

        if(container.getPlayers().containsKey(player)){
            if(config.getBoolean("check-settings.block-chat")){
                event.setCancelled(true);
            }

            if(container.getYesNoCheck().contains(player)){
                if(message.equals("yes") || message.equals("Yes") || message.equals("YES")){
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                        playerCheck.answer(player, container.getPlayerHashMap().get(player), "yes");
                        container.getYesNoCheck().remove(player);
                    });
                } else if(message.equals("no") || message.equals("No") || message.equals("NO")){
                    playerCheck.answer(player, container.getPlayerHashMap().get(player), "no");
                    container.getYesNoCheck().remove(player);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event){
        Player player = event.getPlayer();
        String command = event.getMessage().split(" ")[0];


        if(container.getPlayers().containsKey(player)){
            if(!container.getWhiteCommands().contains(command)){
                event.setCancelled(true);
                player.sendMessage(blockCommandMessage);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        Inventory inventory = event.getInventory();

        if(!container.getInventories().contains(inventory)){
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerCloseInventory(InventoryCloseEvent event){
        Inventory inventory = event.getInventory();

        if(!container.getInventories().contains(inventory)){
            return;
        }

        container.getInventories().remove(inventory);
    }
}
