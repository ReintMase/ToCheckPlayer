package me.seven.reintmase.tocheckplayer.PluginCommands;

import lombok.SneakyThrows;
import me.seven.reintmase.tocheckplayer.Array.Container;
import me.seven.reintmase.tocheckplayer.Main;
import me.seven.reintmase.tocheckplayer.Manager.PlayerCheck;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class CommandsPl implements CommandExecutor {

    private final PlayerCheck playerCheck;
    private final Container container;


    private final String useCommand, questionMessage, useMessage, nopermission, notOnTheCheck, playerIsNotOnline, cantbecalled, playerHasProtect, cantbecanceled, cantbeconfirmed, adminmessageconfirm, adminmessagecancel, adminmessagestart;

    public CommandsPl(){
        this.playerCheck = Main.getInstance().getPlayerCheck();
        this.container = Main.getInstance().getContainer();
        FileConfiguration config = Main.getInstance().getConfig();

        this.useCommand = StringUtils.hex(config.getString("messages.use-command"));
        this.questionMessage = StringUtils.hex(config.getString("messages.question-message"));
        this.useMessage = StringUtils.hex(config.getString("messages.use-message"));
        this.nopermission = StringUtils.hex(config.getString("messages.no-permission"));
        this.playerIsNotOnline = StringUtils.hex(config.getString("messages.player-is-not-online"));
        this.cantbecalled = StringUtils.hex(config.getString("messages.cant-be-called"));
        this.cantbecanceled = StringUtils.hex(config.getString("messages.cant-be-canceled"));
        this.playerHasProtect = StringUtils.hex(config.getString("messages.player-has-protect"));
        this.cantbeconfirmed = StringUtils.hex(config.getString("messages.cant-be-confirmed"));
        this.adminmessageconfirm = StringUtils.hex(config.getString("messages.admin-message-confirm"));
        this.adminmessagecancel = StringUtils.hex(config.getString("messages.admin-message-cancel"));
        this.adminmessagestart = StringUtils.hex(config.getString("messages.admin-message-start"));
        this.notOnTheCheck = StringUtils.hex(config.getString("messages.not-on-the-check"));
    }

    @SneakyThrows
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Данную команду может ввести только игрок!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(useCommand);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "help":
                if(player.hasPermission("checkplayer.admin")){
                    player.sendMessage("");
                    player.sendMessage(StringUtils.hex("&a/check start <ник_игрока> &f- начать проверку игрока"));
                    player.sendMessage(StringUtils.hex("&a/check cancel <ник_игрока> &f- закончить проверку с вердиктом: 'ЧИТЕР'"));
                    player.sendMessage(StringUtils.hex("&a/check confirm <ник_игрока> &f- закончить проверку с вердиктом: 'НЕ ЧИТЕР'"));
                    player.sendMessage(StringUtils.hex("&a/check logs &f- открывает меню с логами"));
                    player.sendMessage(StringUtils.hex(""));
                    player.sendMessage(StringUtils.hex("&c- Для игроков -"));
                    player.sendMessage(StringUtils.hex("&a/check msg <ник_игрока> &f- Сообщение администратору"));
                    player.sendMessage(StringUtils.hex("&a/check confess &f- Сознаться в читах"));
                    player.sendMessage(StringUtils.hex(""));
                } else {
                    player.sendMessage(StringUtils.hex(""));
                    player.sendMessage(StringUtils.hex("&a/check msg <ник_игрока> &f- Сообщение администратору"));
                    player.sendMessage(StringUtils.hex("&a/check confess &f- Сознаться в читах"));
                    player.sendMessage(StringUtils.hex(""));
                }
                return true;
            case "confess":
                if(container.getPlayers().containsKey(player)){
                    container.getYesNoCheck().add(player);
                    player.sendMessage(questionMessage);
                    return true;
                } else{
                    player.sendMessage(notOnTheCheck);
                }
                break;
            case "msg":
                if (args.length < 2) {
                    player.sendMessage(useMessage);
                    return true;
                }
                StringBuilder message = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    message.append(args[i]).append(" ");
                }
                message = new StringBuilder(message.toString().trim());
                playerCheck.msg(player, container.getPlayerHashMap().get(player), message.toString());
                return true;
            case "start":
            case "confirm":
            case "cancel":
                if(!player.hasPermission("checkplayer.admin")){
                    player.sendMessage(nopermission);
                    return true;
                }
                if (args.length < 2) {
                    player.sendMessage(useCommand);
                    return true;
                }
                Player targetPlayer = Bukkit.getPlayer(args[1]);
                if (targetPlayer == null) {
                    String editedPlayerIsNotOnline = playerIsNotOnline;
                    editedPlayerIsNotOnline = editedPlayerIsNotOnline.replace("%player_target%", args[1]);
                    player.sendMessage(editedPlayerIsNotOnline);
                    return true;
                }
                switch (subCommand) {
                    case "start":
                        if(targetPlayer == player){
                            player.sendMessage(cantbecalled);
                            return true;
                        }

                        if(targetPlayer.hasPermission("checkplayer.protect")){
                            player.sendMessage(playerHasProtect);
                            return true;
                        }

                        String editedadminmessagestart = adminmessagestart;
                        editedadminmessagestart = editedadminmessagestart.replace("%player%", targetPlayer.getName());
                        player.sendMessage(editedadminmessagestart);
                        playerCheck.check(targetPlayer, player);
                        container.getPlayerHashMap().put(targetPlayer, player);
                        break;
                    case "confirm":
                        if(targetPlayer == player){
                            player.sendMessage(cantbeconfirmed);
                            return true;
                        }
                        playerCheck.confirm(targetPlayer);
                        String editedadminmessageconfirm = adminmessageconfirm;
                        editedadminmessageconfirm = editedadminmessageconfirm.replace("%player%", targetPlayer.getName());
                        player.sendMessage(editedadminmessageconfirm);
                        break;
                    case "cancel":
                        if(targetPlayer == player){
                            player.sendMessage(cantbecanceled);
                            return true;
                        }
                        playerCheck.cancel(targetPlayer);
                        String editedadminmessagecancel = adminmessagecancel;
                        editedadminmessagecancel = editedadminmessagecancel.replace("%player%", targetPlayer.getName());
                        player.sendMessage(editedadminmessagecancel);
                        break;
                }
                return true;
            default:
                player.sendMessage(useCommand);
                return true;
        }
        return true;
    }
}