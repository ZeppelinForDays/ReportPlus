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
	private Map<UUID, ReportPlayer> players = new HashMap<UUID, ReportPlayer>();

	public PlayerHandler()
	{
		for (Player player : Bukkit.getOnlinePlayers())
		{
			if (!players.containsKey(player.getUniqueId()))
			{
				addPlayer(player.getUniqueId());
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

	public void addPlayer(UUID uniqueId)
	{
		players.put(uniqueId, new ReportPlayer(uniqueId));
	}

	public void removePlayer(UUID uniqueId)
	{
		players.remove(uniqueId);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();

		if (!players.containsKey(player.getUniqueId()))
		{
			addPlayer(player.getUniqueId());
		}
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();

		if (players.containsKey(player.getUniqueId()))
		{
			removePlayer(player.getUniqueId());
		}
	}
}
