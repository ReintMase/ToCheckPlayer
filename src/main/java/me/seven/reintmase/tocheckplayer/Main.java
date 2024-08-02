package me.seven.reintmase.tocheckplayer;

import lombok.Getter;
import lombok.SneakyThrows;
import me.seven.reintmase.tocheckplayer.Array.Container;
import me.seven.reintmase.tocheckplayer.Manager.Listeners;
import me.seven.reintmase.tocheckplayer.Manager.PlayerCheck;
import me.seven.reintmase.tocheckplayer.PluginCommands.CheckTabCompleter;
import me.seven.reintmase.tocheckplayer.PluginCommands.CommandsPl;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Objects;

@Getter
public final class Main extends JavaPlugin {

    @Getter
    private static Main instance;

    public Container container;
    public PlayerCheck playerCheck;
    public CommandsPl commandsPl;
    public BossBarManage bossBarManage;

    @SneakyThrows
    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;

        container = new Container();
        bossBarManage = new BossBarManage();
        playerCheck = new PlayerCheck(bossBarManage);

        commandsPl = new CommandsPl();

        FileConfiguration config = getConfig();

        Objects.requireNonNull(getCommand("check")).setExecutor(commandsPl);
        getServer().getPluginManager().registerEvents(new Listeners(), this);
        Objects.requireNonNull(getServer().getPluginCommand("check")).setTabCompleter(new CheckTabCompleter());

        addWhiteCommands(config);

        sendAnnounce();
    }

    @Override
    public void onDisable() {
        sendAnnounce();
    }

    private void sendAnnounce() {
        Bukkit.getLogger().info("");
        Bukkit.getLogger().info("Если вы хотите, чтобы я вам помог");
        Bukkit.getLogger().info("или вы хотите заказать у меня плагин,");
        Bukkit.getLogger().info("пишите мне!");
        Bukkit.getLogger().info("");
        Bukkit.getLogger().info("Телеграм: @verylovemetal");
        Bukkit.getLogger().info("Дискорд: verylovemetal");
        Bukkit.getLogger().info("");
    }

    private void addWhiteCommands(FileConfiguration config) {
        List<String> commands = config.getStringList("check-settings.white-commands");
        commands.add("/check");
        container.getWhiteCommands().addAll(commands);
    }
}
