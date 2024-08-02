package me.seven.reintmase.tocheckplayer.Manager;

import org.bukkit.entity.Player;

import java.sql.SQLException;

public interface CheckManager {

    void check(Player checked, Player checker) throws SQLException;

    void confirm(Player player);

    void cancel(Player player);

    void confess(Player player, Player adminPlayer);

    void quitBan(Player player, Player adminPlayer);

    void msg(Player player, Player adminPlayer, String message);

    void answer(Player player, Player adminPlayer,String answer);
}
