package net.zeppelin.reportplus.player;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ReportPlayer
{
	private UUID uniqueId;

	public ReportPlayer(UUID uniqueId)
	{
		this.uniqueId = uniqueId;
	}

	public Player getHandler()
	{
		return Bukkit.getPlayer(uniqueId);
	}

	public UUID getUniqueId()
	{
		return uniqueId;
	}

	public void setUniqueId(UUID uniqueId)
	{
		this.uniqueId = uniqueId;
	}
}
