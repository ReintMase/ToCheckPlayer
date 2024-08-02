package me.seven.reintmase.tocheckplayer.PluginCommands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CheckTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {

        if(args.length == 1) {
            return Arrays.asList("start", "confirm", "cancel", "help", "msg", "confess", "reload");
        } else if(args[0].equalsIgnoreCase("start") || args[0].equalsIgnoreCase("cancel") || args[0].equalsIgnoreCase("confirm") && args.length == 2) {

            Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();

            ArrayList<String> onlinePlayerList = new ArrayList<>();
            for(Player allPlayer : onlinePlayers){
                String name = allPlayer.getName();
                onlinePlayerList.add(name);
            }
            return onlinePlayerList;
        }
        return new ArrayList<>();
    }
}
