package me.seven.reintmase.tocheckplayer;

import me.seven.reintmase.tocheckplayer.Array.Container;
import me.seven.reintmase.tocheckplayer.PluginCommands.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class BossBarManage {

    private final HashMap<Player, Integer> secondMap = new HashMap<>();
    public HashMap<Player, BossBar> bossBar = new HashMap<>();

    private final Container container;

    private final String bossbartitle, barColor;

    public BossBarManage(){
        this.container = Main.getInstance().getContainer();
        FileConfiguration config = Main.getInstance().getConfig();

        this.bossbartitle = StringUtils.hex(config.getString("bossbar-settins.title"));
        this.barColor = StringUtils.hex(config.getString("bossbar-settins.color"));
    }

    public void createBossBar(Player player) {
        int initialSeconds = 1;
        BossBar bossBar = Bukkit.createBossBar("", BarColor.valueOf(barColor), BarStyle.SEGMENTED_20);
        this.bossBar.put(player, bossBar);
        bossBar.setProgress(1);
        bossBar.addPlayer(player);
        secondMap.put(player, initialSeconds);

        bukkitRunable(initialSeconds, player);
    }

    private void bukkitRunable(int initialSeconds, Player player){
        new BukkitRunnable() {
            int seconds = initialSeconds;

            @Override
            public void run() {
                if (container.getPlayers().containsKey(player)) {
                    String editedbossbartitle = bossbartitle;
                    editedbossbartitle = editedbossbartitle.replace("%time%", seconds + "");
                    bossBar.get(player).setTitle(editedbossbartitle);
                    seconds++;
                } else {
                    bossBar.get(player).removePlayer(player);
                }
            }
        }.runTaskTimer(Main.getInstance(), 20L, 20L);
    }

    public void removeBossBar(Player player) {
        bossBar.get(player).removePlayer(player);
    }
}
