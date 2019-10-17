package net.zeppelin.reportplus.inventories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import net.zeppelin.reportplus.player.PlayerHandler;
import net.zeppelin.reportplus.player.ReportPlayer;
import net.zeppelin.reportplus.reports.Report;
import net.zeppelin.reportplus.reports.ReportHandler;
import net.zeppelin.reportplus.utils.InventoryHandler;
import net.zeppelin.reportplus.utils.ItemUtils;
import net.zeppelin.reportplus.utils.Messages;

public class ClickedReportInventory extends ReportInventory
{
	private Report report;
	private ReportHandler reportHandler;
	private PlayerHandler playerHandler;
	
	public ClickedReportInventory(InventoryHandler inventoryHandler, int id, ReportHandler reportHandler, PlayerHandler playerHandler)
	{
		super(inventoryHandler, id, true);
		this.reportHandler = reportHandler;
		this.playerHandler = playerHandler;
	}

	@Override
	public void loadContents()
	{
		if (report == null) return;

		String targetName = report.getTargetPlayer().getName();
		String reporterName = report.getReportPlayer().getName();

		// Load inventory
		inventory = Bukkit.createInventory(null, 27, "Report for " + targetName);

		// Create reportInfoLore
		List<String> reportInfoLore = new ArrayList<>();
		reportInfoLore.add("§6Player: §7" + targetName);
		reportInfoLore.add("§6Reason: §7" + report.getReason());
		reportInfoLore.add("");
		reportInfoLore.add("§6Reported By: §7" + reporterName);
		if (reportHandler.getReportsAgainstPlayer(report.getTargetPlayer().getUniqueId()) > 1)
		{
			reportInfoLore.add("");
			reportInfoLore.add(ChatColor.RED + "This player has multiple reports against them.");
		}

		inventory.setItem(4, ItemUtils.createItem(Material.PAPER, "§aReport Info", reportInfoLore));
		inventory.setItem(10, ItemUtils.createItem(Material.ENCHANTED_BOOK, "§6Archive Report", Arrays.asList("§7Send this report to the archive.")));
		inventory.setItem(12, ItemUtils.createItem(Material.EMERALD, report.isClaimed() ? "§cAlready Claimed" : "§aClaim Report", Arrays.asList("§7Claim to be the handler of the", "§7situation of this report.")));
		inventory.setItem(14, ItemUtils.createItem(Material.REDSTONE, "§cDelete Report", Arrays.asList("§7Delete this report.")));
		inventory.setItem(16, ItemUtils.createItem(Material.BOOK, "§6Player Options", Arrays.asList("§7List of options for the player being reported.")));
		inventory.setItem(18, ItemUtils.createItem(Material.ARROW, "§cBack", null));
	}

	@Override
	public void onInventoryClickEvent(InventoryClickEvent event)
	{
		if (report == null) return;
		
		Player player = (Player) event.getWhoClicked();
		ReportPlayer reportPlayer = playerHandler.getReportPlayerFromId(player.getUniqueId());
		int slot = event.getSlot();
		
		if (slot == 14)
		{
			if (!player.hasPermission("reportplus.reports.remove"))
			{
				player.sendMessage(Messages.INVALID_PERMISSION);
				return;
			}

			reportHandler.removeActiveReport(report);
			report = null;
			
			if (reportHandler.getActiveReports().size() <= 0) inventoryHandler.getMainInventory().openInventory(player);
			else inventoryHandler.getReportListInventory().openInventory(player, ReportListInventory.ACTIVE_REPORTS);
		}
		else if (slot == 12)
		{
			if (!player.hasPermission("reportplus.reports.claim"))
			{
				player.sendMessage(Messages.INVALID_PERMISSION);
				return;
			}

			if (!report.isClaimed())
			{
				report.claim(reportPlayer);
				inventoryHandler.getReportListInventory().openInventory(player, ReportListInventory.ACTIVE_REPORTS);
			}
			else
			{
				player.sendMessage(Messages.REPORT_CLAIMED);
				player.closeInventory();
			}
		}
		else if (slot == 10)
		{
			// Check permission
			if (!player.hasPermission("reportplus.reports.archive"))
			{
				player.sendMessage(Messages.INVALID_PERMISSION);
				player.closeInventory();
				return;
			}

			reportHandler.removeActiveReport(report);
			reportHandler.addArchivedReport(report);
			player.sendMessage("§7Report for §6" + report.getTargetPlayer().getName() + "§7 sent to the archive.");
			
			if (reportHandler.getActiveReports().size() <= 0)
				inventoryHandler.getMainInventory().openInventory(player);
			else
				inventoryHandler.getReportListInventory().openInventory(player, ReportListInventory.ACTIVE_REPORTS);
		}
		else if (slot == 16)
		{
			if (!player.hasPermission("reportplus.players.manage"))
			{
				player.sendMessage(Messages.INVALID_PERMISSION);
				player.closeInventory();
				return;
			}
			
			inventoryHandler.getPlayerOptionsInventory().openInventoryFromReport(player, report);
		}
		else if (slot == 18)
		{
			inventoryHandler.getReportListInventory().openInventory(player, ReportListInventory.ACTIVE_REPORTS);
		}
	}
	
	public void openInventoryFromReport(Player player, Report report)
	{
		this.report = report;
		super.openInventory(player);
	}

	public Report getReport()
	{
		return report;
	}

	public void setReport(Report report)
	{
		this.report = report;
	}
}
