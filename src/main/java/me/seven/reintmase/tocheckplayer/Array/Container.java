package me.seven.reintmase.tocheckplayer.Array;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

@Getter
public class Container {

    private final HashMap<Player, Player> playerHashMap = new HashMap<>();

    @Getter
    private final HashSet<String> whiteCommands = new HashSet<>();

    @Getter
    private final HashSet<Inventory> inventories = new HashSet<>();

    @Getter
    private final HashSet<Player> yesNoCheck = new HashSet<>();

    @Getter
    private final HashSet<String> standardCommands = new HashSet<>();

    @Getter
    private final HashMap<Player, UUID> players = new HashMap<>();

    public void addPlayers(Player player) {
        players.put(player, player.getUniqueId());
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

}
