package me.seven.reintmase.tocheckplayer.Manager;

import me.seven.reintmase.tocheckplayer.Array.Container;
import me.seven.reintmase.tocheckplayer.BossBarManage;
import me.seven.reintmase.tocheckplayer.Main;
import me.seven.reintmase.tocheckplayer.PluginCommands.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class PlayerCheck implements CheckManager {

    private final Container container;
    private final BossBarManage bossBarManage;

    private final FileConfiguration config;

    private final String checkConfirmMessage, notOnTheCheck, playerConfessMessage, denyConfess, playerQuitMessage, messageType, playerIsNotOnline;

    private final List<String> checkStartMessage = new ArrayList<>();

    public PlayerCheck(BossBarManage bossBarManage) {
        this.bossBarManage = bossBarManage;
        this.container = Main.getInstance().getContainer();

        this.config = Main.getInstance().getConfig();

        this.checkConfirmMessage = StringUtils.hex(config.getString("messages.check-confirm-message"));
        this.notOnTheCheck = StringUtils.hex(config.getString("messages.not-on-the-check"));
        this.playerConfessMessage = StringUtils.hex(config.getString("messages.player-confess-message"));
        this.playerQuitMessage = StringUtils.hex(config.getString("messages.player-quit-message"));
        this.denyConfess = StringUtils.hex(config.getString("messages.deny-confess"));
        this.messageType = StringUtils.hex(config.getString("check-settings.message-type"));
        this.playerIsNotOnline = StringUtils.hex(config.getString("messages.player-is-not-online"));

        this.checkStartMessage.addAll(config.getStringList("messages.check-start-message"));
    }

    @Override
    public void check(Player checked, Player checker) {
        for(String checkStartMessage : checkStartMessage) {
            checked.sendMessage(StringUtils.hex(checkStartMessage));
        }

        if(config.getBoolean("tp-settings.tp-coords")){
            String worldName = config.getString("tp-settings.world");

            if(worldName == null){
                Bukkit.getLogger().warning("Мир не был найден!");
                return;
            }

            World world = Bukkit.getWorld(worldName);
            int coordx = config.getInt("tp-settings.coord-x");
            int coordy = config.getInt("tp-settings.coord-y");
            int coordz = config.getInt("tp-settings.coord-z");

            Location location = new Location(world, coordx, coordy, coordz);
            checked.teleport(location);
        }

        if(config.getBoolean("check-settings.blindness-effect")){
            checked.addPotionEffect(PotionEffectType.BLINDNESS.createEffect(Integer.MAX_VALUE, 1));
        }

        if(config.getBoolean("check-settings.title-message")){
            String upTitle = config.getString("check-settings.title-message.up-title");
            String downTitle = config.getString("check-settings.title-message.down-title");
            int seconds = config.getInt("check-settings.title-message.seconds");

            checked.sendTitle(StringUtils.hex(upTitle), StringUtils.hex(downTitle), 0, seconds * 20,0);
        }

        container.addPlayers(checked);

        if(config.getBoolean("bossbar-settins.enable")){
            bossBarManage.createBossBar(checked);
        }
    }

    @Override
    public void confirm(Player player) {

        if(!player.isOnline()){
            player.sendMessage(playerIsNotOnline);
            return;
        }

        if(!container.getPlayers().containsKey(player)){
            player.sendMessage(notOnTheCheck);
            return;
        }

        if(player.hasPotionEffect(PotionEffectType.BLINDNESS)){
            player.removePotionEffect(PotionEffectType.BLINDNESS);
        }

        player.sendMessage(checkConfirmMessage);
        container.removePlayer(player);
        bossBarManage.removeBossBar(player);
    }

    @Override
    public void cancel(Player player) {

        if(!player.isOnline()){
            player.sendMessage(playerIsNotOnline);
            return;
        }

        if(!container.getPlayers().containsKey(player)){
            player.sendMessage(notOnTheCheck);
            return;
        }

        container.removePlayer(player);
        bossBarManage.removeBossBar(player);

        String command = config.getString("type-ban.cancel.form-ban");

        if(command == null){
            Bukkit.getLogger().warning("Command = null");
            return;
        }

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
        bossBarManage.removeBossBar(player);
    }

    @Override
    public void confess(Player player, Player adminPlayer) {
        if(!container.getPlayers().containsKey(player)){
            player.sendMessage(notOnTheCheck);
            return;
        }

        container.removePlayer(player);
        String editedPlayerConfessMessage = playerConfessMessage;
        editedPlayerConfessMessage = editedPlayerConfessMessage.replace("%player%", player.getName());
        adminPlayer.sendMessage(editedPlayerConfessMessage);
        bossBarManage.removeBossBar(player);

        String command = config.getString("type-ban.confess.form-ban");

        if(command == null){
            Bukkit.getLogger().warning("Command = null");
            return;
        }

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
        bossBarManage.removeBossBar(player);
    }

    @Override
    public void msg(Player player, Player adminPlayer, String message) {
        if(!container.getPlayers().containsKey(player)){
            player.sendMessage(notOnTheCheck);
            return;
        }

        String editedMessageType = messageType;
        editedMessageType = editedMessageType.replace("%player%", player.getName());
        editedMessageType = editedMessageType.replace("%message%", message);

        player.sendMessage(editedMessageType);
        adminPlayer.sendMessage(editedMessageType);
    }

    @Override
    public void answer(Player player, Player adminPlayer, String answer){
        if(answer.equals("yes")){
            confess(player, adminPlayer);
        } else if(answer.equals("no")){
            player.sendMessage(denyConfess);
        }
    }

    @Override
    public void quitBan(Player player, Player adminPlayer){
        adminPlayer.sendMessage(playerQuitMessage);
        String command = config.getString("type-ban.leave.form-ban");

        if(command == null){
            Bukkit.getLogger().warning("Command = null");
            return;
        }

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
        bossBarManage.removeBossBar(player);
        container.removePlayer(player);
    }
}
