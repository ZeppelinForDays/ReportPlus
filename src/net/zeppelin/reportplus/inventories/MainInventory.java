package net.zeppelin.reportplus.inventories;

import java.util.Arrays;
import java.util.Collections;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import net.zeppelin.reportplus.reports.ReportHandler;
import net.zeppelin.reportplus.utils.InventoryHandler;
import net.zeppelin.reportplus.utils.ItemUtils;
import net.zeppelin.reportplus.utils.Messages;

@SuppressWarnings("deprecation")
public class MainInventory extends ReportInventory
{
	private ReportHandler reportHandler;
	private Player player;
	
	public MainInventory(InventoryHandler inventoryHandler, int id, ReportHandler reportHandler)
	{
		super(inventoryHandler, id);
		this.reportHandler = reportHandler;
	}
	
	@Override
	public void loadContents()
	{
		// Create inventory
		inventory = Bukkit.createInventory(null, 27, "Report Plus");
		
		// Load inventory
		inventory.setItem(12, ItemUtils.createItem(Material.ENCHANTED_BOOK, "§6Archived Reports", Collections.singletonList("§7List of all archived reports.")));
		ItemStack item = new ItemStack(Bukkit.getVersion().contains("1.14") ? Material.valueOf("PLAYER_HEAD") : Material.valueOf("SKULL_ITEM"));
		if (!Bukkit.getVersion().contains("1.14"))
		{
			item = new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (byte) SkullType.PLAYER.ordinal());
		}
		inventory.setItem(14, ItemUtils.createItem(item, "§aActive Reports", Collections.singletonList("§7View and manage all active reports.")));
		inventory.setItem(18, ItemUtils.createItem(Material.ARROW, ChatColor.RED + "Exit", null));
	}

	@Override
	public void onInventoryClickEvent(InventoryClickEvent event)
	{
		player = (Player) event.getWhoClicked();
		int slot = event.getSlot();
		
		// Check permission
		if (!player.hasPermission("reportplus.reports.view"))
		{
			player.sendMessage(Messages.INVALID_PERMISSION);
			player.closeInventory();
			return;
		}

		if (slot == 14)
		{
			// Check if list is empty
			if (!reportHandler.getActiveReports().isEmpty())
			{
				inventoryHandler.getReportListInventory().openInventory(player, ReportListInventory.ACTIVE_REPORTS);
			}
			else
			{
				player.closeInventory();
				player.sendMessage(Messages.CURRENTLY_NO_REPORTS);
				return;
			}
		}
		else if (slot == 12)
		{
			// Check if list is empty
			if (!reportHandler.getArchivedReports().isEmpty())
			{
				inventoryHandler.getReportListInventory().openInventory(player, ReportListInventory.ARCHIVED_REPORTS);
			} else
			{
				player.closeInventory();
				player.sendMessage(Messages.CURRENTLY_NO_REPORTS);
				return;
			}
		}
		else if (slot == 18)
		{
			player.closeInventory();
		}
	}
}
