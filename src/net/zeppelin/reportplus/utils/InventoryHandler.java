package net.zeppelin.reportplus.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import net.zeppelin.reportplus.inventories.ClickedReportInventory;
import net.zeppelin.reportplus.inventories.MainInventory;
import net.zeppelin.reportplus.inventories.PlayerOptionsInventory;
import net.zeppelin.reportplus.inventories.ReportInventory;
import net.zeppelin.reportplus.inventories.ReportListInventory;
import net.zeppelin.reportplus.player.PlayerHandler;
import net.zeppelin.reportplus.reports.ReportHandler;

public class InventoryHandler implements Listener
{
	private ReportHandler reportHandler;
	private PlayerHandler playerHandler;

	private List<ReportInventory> inventories = new ArrayList<ReportInventory>();

	private MainInventory mainInventory;
	private ClickedReportInventory clickedReportInventory;
	private ReportListInventory reportListInventory;
	private PlayerOptionsInventory playerOptionsInventory;

	public InventoryHandler(ReportHandler reportHandler, PlayerHandler playerHandler)
	{
		this.reportHandler = reportHandler;
		this.playerHandler = playerHandler;
	}

	public void loadInventories()
	{
		mainInventory = new MainInventory(this, 0, reportHandler);
		clickedReportInventory = new ClickedReportInventory(this, 1, reportHandler, playerHandler);
		reportListInventory = new ReportListInventory(this, 2, reportHandler);
		playerOptionsInventory = new PlayerOptionsInventory(this, 3);

		inventories.add(mainInventory);
		inventories.add(clickedReportInventory);
		inventories.add(reportListInventory);
		inventories.add(playerOptionsInventory);

		for (ReportInventory tempInventory : inventories)
		{
			tempInventory.loadContents();
		}
	}

	public void openInventoryFromId(int id, Player player)
	{
		ReportInventory inventory = inventories.get(id);

		if (inventory == null)
		{
			System.out.println(ChatColor.RED + "Failed to open inventory from id: Invalid id");
			return;
		}

		inventory.openInventory(player);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event)
	{
		Player player = (Player) event.getWhoClicked();

		for (ReportInventory tempInventory : inventories)
		{
			boolean isOpen = tempInventory.isOpenForPlayer(player.getUniqueId());
			if (isOpen)
			{
				event.setCancelled(true);
				tempInventory.onInventoryClickEvent(event);
				break;
			}
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event)
	{
		UUID id = event.getPlayer().getUniqueId();

		for (ReportInventory tempInventory : inventories)
		{
			if (tempInventory.isOpenForPlayer(id))
				tempInventory.getInventoryCheck().remove(id);
		}
	}

	public MainInventory getMainInventory()
	{
		return mainInventory;
	}

	public ClickedReportInventory getClickedReportInventory()
	{
		return clickedReportInventory;
	}

	public ReportListInventory getReportListInventory()
	{
		return reportListInventory;
	}

	public PlayerOptionsInventory getPlayerOptionsInventory()
	{
		return playerOptionsInventory;
	}
}
