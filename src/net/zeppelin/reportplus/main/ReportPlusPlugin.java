package net.zeppelin.reportplus.main;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.zeppelin.reportplus.inventories.ReportListInventory;
import net.zeppelin.reportplus.player.PlayerHandler;
import net.zeppelin.reportplus.player.ReportPlayer;
import net.zeppelin.reportplus.reports.Report;
import net.zeppelin.reportplus.reports.ReportHandler;
import net.zeppelin.reportplus.utils.InventoryHandler;
import net.zeppelin.reportplus.utils.Messages;

public class ReportPlusPlugin extends JavaPlugin implements CommandExecutor
{
	public static final String PREFIX = "§7[§6§lReport§8Plus§7] §r";

	// Managers
	private PlayerHandler playerHandler;
	private ReportHandler reportHandler;
	private InventoryHandler inventoryHandler;

	// Configs
	private File reportsFile;
	private File archiveFile;
	private FileConfiguration reportsConfig;
	private FileConfiguration archiveConfig;

	/*
	 * Permissions:
	 *
	 * - reportplus.report: Allows a player to report someone.
	 * - reportplus.reports.manage: Allows a player to open the report manager menu.
	 * - reportplus.reports.view: Allows a player to view reports.
	 * - reportplus.reports.remove: Allows a player to remove all reports from a target player.
	 * - reportplus.reports.claim: Allows a player to claim a report.
	 * - reportplus.reports.archive: Allows a player to archive a report.
	 * - reportplus.players.manage: Allows a player to access the player options menu.
	 * - reportplus.notify.receive
	 */

	@Override
	public void onEnable()
	{
		// Managers
		this.playerHandler = new PlayerHandler();
		this.reportHandler = new ReportHandler();
		this.inventoryHandler = new InventoryHandler(reportHandler, playerHandler);
		inventoryHandler.loadInventories();

		// Configs
		this.reportsFile = new File(this.getDataFolder() + "/storage/reports.yml");
		this.archiveFile = new File(this.getDataFolder() + "/storage/archive.yml");
		this.reportsConfig = YamlConfiguration.loadConfiguration(reportsFile);
		this.archiveConfig = YamlConfiguration.loadConfiguration(archiveFile);

		// Load reports from configuration file.
		long startTime = System.currentTimeMillis();
		// Load active reports
		int totalReportsLoaded = 0;
		int counter = 0;
		while (reportsConfig.getString(String.valueOf(counter)) != null)
		{
			String reporter = reportsConfig.getString(counter + ".reporter");
			String target = reportsConfig.getString(counter + ".target");
			String reason = reportsConfig.getString(counter + ".reason");

			UUID reporterId = UUID.fromString(reporter);
			UUID targetId = UUID.fromString(target);

			ReportPlayer reportPlayer = new ReportPlayer(reporterId);
			ReportPlayer targetPlayer = new ReportPlayer(targetId);

			Report report = new Report(reportPlayer, targetPlayer, reason);
			reportHandler.addActiveReport(report);

			totalReportsLoaded++;
			counter++;
		}

		// Load archived reports
		counter = 0;
		while (archiveConfig.getString(String.valueOf(counter)) != null)
		{
			String reporter = archiveConfig.getString(counter + ".reporter");
			String target = archiveConfig.getString(counter + ".target");
			String reason = archiveConfig.getString(counter + ".reason");

			UUID reporterId = UUID.fromString(reporter);
			UUID targetId = UUID.fromString(target);

			ReportPlayer reportPlayer = new ReportPlayer(reporterId);
			ReportPlayer targetPlayer = new ReportPlayer(targetId);

			Report report = new Report(reportPlayer, targetPlayer, reason);
			reportHandler.addArchivedReport(report);

			totalReportsLoaded++;
			counter++;
		}
		int elapsedTime = (int) (System.currentTimeMillis() - startTime);
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "" + totalReportsLoaded + " report(s) has been loaded successfully, took " + elapsedTime + "ms.");

		// Register listeners
		getServer().getPluginManager().registerEvents(playerHandler, this);
		getServer().getPluginManager().registerEvents(inventoryHandler, this);

		// Register commands
		getCommand("report").setExecutor(this);
		getCommand("reports").setExecutor(this);
	}

	@Override
	public void onDisable()
	{
		/*
		 * Save reports to configuration file.
		 */
		long startTime = System.currentTimeMillis();
		// Save reportsConfig
		int totalReportsSaved = 0;
		int counter = 0;
		while (true)
		{
			if (counter < reportHandler.getActiveReports().size())
			{
				Report tempReport = reportHandler.getActiveReports().get(counter);

				String reporter = tempReport.getReportPlayer().getUniqueId().toString();
				String target = tempReport.getReportPlayer().getUniqueId().toString();
				String reason = tempReport.getReason();

				reportsConfig.set(counter + ".reporter", reporter);
				reportsConfig.set(counter + ".target", target);
				reportsConfig.set(counter + ".reason", reason);
				totalReportsSaved++;
			} else
			{
				if (reportsConfig.getString(String.valueOf(counter)) != null)
				{
					reportsConfig.set(String.valueOf(counter), null);
				} else break;
			}
			counter++;
		}
		saveReportsConfig();

		// Save archiveConfig
		counter = 0;
		while (true)
		{
			if (counter < reportHandler.getArchivedReports().size())
			{
				Report tempReport = reportHandler.getArchivedReports().get(counter);

				String reporter = tempReport.getReportPlayer().getUniqueId().toString();
				String target = tempReport.getReportPlayer().getUniqueId().toString();
				String reason = tempReport.getReason();

				archiveConfig.set(counter + ".reporter", reporter);
				archiveConfig.set(counter + ".target", target);
				archiveConfig.set(counter + ".reason", reason);
				totalReportsSaved++;
			} else
			{
				if (archiveConfig.getString(String.valueOf(counter)) != null)
				{
					archiveConfig.set(String.valueOf(counter), null);
				} else break;
			}
			counter++;
		}
		saveArchiveConfig();
		int elapsedTime = (int) (System.currentTimeMillis() - startTime);
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "" + totalReportsSaved + " report(s) has been saved successfully, took " + elapsedTime + "ms.");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (!(sender instanceof Player))
		{
			sender.sendMessage(Messages.INVALID_SENDER);
			return false;
		}

		Player player = (Player) sender;
		ReportPlayer reportPlayer = playerHandler.getReportPlayerFromId(player.getUniqueId());

		if (command.getName().equalsIgnoreCase("report"))
		{
			if (args.length <= 0)
			{
				// Checks permission
				if (!player.hasPermission("reportplus.reports.manage"))
				{
					player.sendMessage(Messages.INVALID_PERMISSION);
					return false;
				}

				// Opens report inventory
				inventoryHandler.getMainInventory().openInventory(player);
			} else if (args.length >= 2)
			{
				if (args[0].equalsIgnoreCase("removeall"))
				{ // removeall command
					// Checks permission
					if (!player.hasPermission("reportplus.reports.remove"))
					{
						player.sendMessage(Messages.INVALID_PERMISSION);
						return false;
					}

					// Delete reports for target player
					Player target = Bukkit.getPlayer(args[1]);

					if (target != null)
					{
						int deleteCounter = 0;
						for (int i = 0; i < reportHandler.getActiveReports().size(); i++)
						{
							Report tempReport = reportHandler.getActiveReports().get(i);

							if (tempReport.getTargetPlayer().getUniqueId().equals(target.getUniqueId()))
							{
								reportHandler.removeActiveReport(tempReport);
								i--;
								deleteCounter++;
							}
						}

						// Check number of reports that was removed if any
						if (deleteCounter > 0)
						{
							// Inform the sender of the reports removed
							player.sendMessage("§6" + deleteCounter + "§7 report(s) has been removed for §6" + target.getName());
							return false;
						} else
						{
							// No reports for this user
							player.sendMessage(Messages.INVALID_TARGET_REPORT);
							return false;
						}
					} else
					{
						// Could not find player
						player.sendMessage(ChatColor.RED + "Could not find player: " + args[1]);
						return false;
					}
				}

				// Checks permission
				if (!player.hasPermission("reportplus.report"))
				{
					player.sendMessage(Messages.INVALID_PERMISSION);
					return false;
				}

				// Continue reporting command
				Player target = Bukkit.getPlayer(args[0]);

				if (target != null)
				{
					ReportPlayer targetPlayer = playerHandler.getReportPlayerFromId(target.getUniqueId());
					String reason = "";

					// Check if player is reporting themselves.
					if (target.getUniqueId().equals(player.getUniqueId()))
					{
						player.sendMessage(Messages.REPORT_SELF);
						return false;
					}

					// Convert args to a String
					for (int i = 1; i < args.length; i++)
					{
						if (i == args.length - 1)
						{
							reason += args[i];
							break;
						}
						reason += args[i] + " ";
					}

					// Create report
					Report report = new Report(reportPlayer, targetPlayer, reason);
					reportHandler.addActiveReport(report);
					player.sendMessage("§7You have reported §6" + target.getName() + "§7 for §6" + reason);
					for (Player online : Bukkit.getOnlinePlayers())
					{
						if (online.hasPermission("reportplus.notify.receive"))
							online.sendMessage(ReportPlusPlugin.PREFIX + "§6" + target.getName() + "§7 has been reported by §6" + player.getName() + "§7 for §6" + reason);
					}
				} else
				{
					// Invalid player.
					player.sendMessage(ChatColor.RED + "Could not find player: " + args[0]);
					return false;
				}
			}
		} else if (command.getName().equalsIgnoreCase("reports"))
		{
			// Checks permission
			if (!player.hasPermission("reportplus.reports.view"))
			{
				player.sendMessage(Messages.INVALID_PERMISSION);
				return false;
			}

			// Opens active reports inventory
			if (reportHandler.getActiveReports().size() != 0)
				inventoryHandler.getReportListInventory().openInventory(player, ReportListInventory.ACTIVE_REPORTS);
			else
			{
				player.sendMessage(Messages.CURRENTLY_NO_REPORTS);
				return false;
			}
		}

		return false;
	}

	public void saveReportsConfig()
	{
		try
		{
			reportsConfig.save(reportsFile);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void saveArchiveConfig()
	{
		try
		{
			archiveConfig.save(archiveFile);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
