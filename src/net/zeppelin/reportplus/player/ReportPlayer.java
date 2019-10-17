package net.zeppelin.reportplus.player;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ReportPlayer
{
	private UUID uniqueId;
	private String name;

	public ReportPlayer(UUID uniqueId, String name)
	{
		this.uniqueId = uniqueId;
		this.name = name;
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

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
}
