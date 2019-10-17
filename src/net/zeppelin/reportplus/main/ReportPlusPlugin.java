package net.zeppelin.reportplus.main;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import net.zeppelin.reportplus.commands.impl.OpenReportsCommand;
import net.zeppelin.reportplus.commands.impl.ReportCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.zeppelin.reportplus.player.PlayerHandler;
import net.zeppelin.reportplus.player.ReportPlayer;
import net.zeppelin.reportplus.reports.Report;
import net.zeppelin.reportplus.reports.ReportHandler;
import net.zeppelin.reportplus.utils.InventoryHandler;

public class ReportPlusPlugin extends JavaPlugin
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

	public static boolean LIMIT_REPORTS;
	public static int REPORT_LIMIT;

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
	 * - reportplus.notify.receive: Allows a player to be notified when a new report is created.
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

		this.getConfig().options().copyDefaults(true);
		LIMIT_REPORTS = getConfig().getBoolean("limitReports");
		REPORT_LIMIT = getConfig().getInt("reportLimit");
		this.saveConfig();

		// Load reports from configuration file.
		long startTime = System.currentTimeMillis();
		// Load active reports
		int totalReportsLoaded = 0;
		int counter = 0;
		while (reportsConfig.getString(String.valueOf(counter)) != null)
		{
			String reporterIdString = reportsConfig.getString(counter + ".reporter");
			String targetIdString = reportsConfig.getString(counter + ".target");
			String reason = reportsConfig.getString(counter + ".reason");

			if (reporterIdString != null && targetIdString != null && reason != null)
			{
				Player reporter = Bukkit.getPlayer(UUID.fromString(reporterIdString));
				Player target = Bukkit.getPlayer(UUID.fromString(targetIdString));

				if (reporter != null && target != null)
				{
					ReportPlayer reportPlayer = new ReportPlayer(reporter.getUniqueId(), reporter.getName());
					ReportPlayer targetPlayer = new ReportPlayer(target.getUniqueId(), target.getName());

					Report report = new Report(reportPlayer, targetPlayer, reason);
					reportHandler.addActiveReport(report);

					totalReportsLoaded++;
					counter++;
				}
			}
		}

		// Load archived reports
		counter = 0;
		while (archiveConfig.getString(String.valueOf(counter)) != null)
		{
			String reporterIdString = archiveConfig.getString(counter + ".reporter");
			String targetIdString = archiveConfig.getString(counter + ".target");
			String reason = archiveConfig.getString(counter + ".reason");

			if (reporterIdString != null && targetIdString != null && reason != null)
			{
				Player reporter = Bukkit.getPlayer(UUID.fromString(reporterIdString));
				Player target = Bukkit.getPlayer(UUID.fromString(targetIdString));

				if (reporter != null && target != null)
				{
					ReportPlayer reportPlayer = new ReportPlayer(reporter.getUniqueId(), reporter.getName());
					ReportPlayer targetPlayer = new ReportPlayer(target.getUniqueId(), target.getName());

					Report report = new Report(reportPlayer, targetPlayer, reason);
					reportHandler.addArchivedReport(report);

					totalReportsLoaded++;
					counter++;
				}
			}
		}
		int elapsedTime = (int) (System.currentTimeMillis() - startTime);
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "" + totalReportsLoaded + " report(s) has been loaded successfully, took " + elapsedTime + "ms.");

		// Register listeners
		getServer().getPluginManager().registerEvents(playerHandler, this);
		getServer().getPluginManager().registerEvents(inventoryHandler, this);

		// Commands
		OpenReportsCommand openReportsCommand = new OpenReportsCommand(reportHandler, inventoryHandler);
		ReportCommand reportCommand = new ReportCommand(playerHandler, reportHandler, inventoryHandler);

		getCommand(openReportsCommand.getName()).setExecutor(openReportsCommand);
		getCommand(reportCommand.getName()).setExecutor(reportCommand);

		// Register Commands
//		getCommand("report").setExecutor(this);
//		getCommand("reports").setExecutor(this);
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
