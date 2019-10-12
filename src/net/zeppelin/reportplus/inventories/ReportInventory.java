package net.zeppelin.reportplus.inventories;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import net.zeppelin.reportplus.utils.InventoryHandler;

public abstract class ReportInventory
{
	protected InventoryHandler inventoryHandler;
	protected int id;
	protected Inventory inventory;
	protected List<UUID> inventoryCheck = new ArrayList<UUID>();
	protected boolean needsRefresh = false;

	public ReportInventory(InventoryHandler inventoryHandler, int id)
	{
		this.inventoryHandler = inventoryHandler;
		this.id = id;
	}

	public ReportInventory(InventoryHandler inventoryHandler, int id, boolean needsRefresh)
	{
		this.inventoryHandler = inventoryHandler;
		this.id = id;
		this.needsRefresh = needsRefresh;
	}

	public abstract void loadContents();
	public abstract void onInventoryClickEvent(InventoryClickEvent event);

	public void openInventory(Player player)
	{
		if (needsRefresh)
			loadContents();
		player.openInventory(inventory);
		this.inventoryCheck.add(player.getUniqueId());
	}

	public boolean isOpenForPlayer(UUID id)
	{
		if (inventoryCheck.contains(id))
			return true;
		return false;
	}

	public List<UUID> getInventoryCheck()
	{
		return inventoryCheck;
	}

	public void setInventoryCheck(List<UUID> inventoryCheck)
	{
		this.inventoryCheck = inventoryCheck;
	}

	public void setInventory(Inventory inventory)
	{
		this.inventory = inventory;
	}

	public Inventory getInventory()
	{
		return inventory;
	}

	public int getId()
	{
		return id;
	}
}
