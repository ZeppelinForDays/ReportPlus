package net.zeppelin.reportplus.player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerHandler implements Listener
{
    private Map<UUID, ReportPlayer> players = new HashMap<>();

    public PlayerHandler()
    {
        for (Player player : Bukkit.getOnlinePlayers())
        {
            if (!players.containsKey(player.getUniqueId()))
            {
                addPlayer(player);
            }
        }
    }

    public ReportPlayer getReportPlayerFromId(UUID id)
    {
        if (players.containsKey(id))
        {
            return players.get(id);
        }
        return null;
    }

    public void addPlayer(Player player)
    {
        players.put(player.getUniqueId(), new ReportPlayer(player.getUniqueId(), player.getName()));
    }

    public void removePlayer(Player player)
    {
        players.remove(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();

        if (!players.containsKey(player.getUniqueId()))
        {
            addPlayer(player);
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();

        if (players.containsKey(player.getUniqueId()))
        {
            removePlayer(player);
        }
    }
}
