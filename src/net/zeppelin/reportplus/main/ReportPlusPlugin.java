package net.zeppelin.reportplus.main;

import net.zeppelin.reportplus.commands.BaseCommand;
import net.zeppelin.reportplus.commands.SubCommand;
import net.zeppelin.reportplus.commands.impl.OpenReportsCommand;
import net.zeppelin.reportplus.commands.impl.ReportCommand;
import net.zeppelin.reportplus.player.PlayerHandler;
import net.zeppelin.reportplus.player.ReportPlayer;
import net.zeppelin.reportplus.reports.Report;
import net.zeppelin.reportplus.reports.ReportHandler;
import net.zeppelin.reportplus.utils.InventoryHandler;
import net.zeppelin.reportplus.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

	public static boolean LIMIT_REPORTS;
	public static int REPORT_LIMIT;

	private List<BaseCommand> commands = new ArrayList<>();

	/*
	 * Permissions:
	 *
	 * - reportplus.report: Allows a player to report someone.
	 * - reportplus.reports.manage: Allows a player to open the report manager menu.
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

        this.saveConfig();
		LIMIT_REPORTS = getConfig().getBoolean("limitReports");
		REPORT_LIMIT = getConfig().getInt("reportLimit");
		Messages.loadMessages(getConfig());

		loadReportsFromConfig();

		// Register listeners
		getServer().getPluginManager().registerEvents(playerHandler, this);
		getServer().getPluginManager().registerEvents(inventoryHandler, this);

		// Commands
		registerCommand(new OpenReportsCommand(reportHandler, inventoryHandler));
		registerCommand(new ReportCommand(playerHandler, reportHandler, inventoryHandler, this));
	}

	@Override
	public void onDisable()
	{
		saveReportsToConfig();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		for (BaseCommand baseCommand : commands)
		{
			if (command.getName().equalsIgnoreCase(baseCommand.getName()))
			{
				// Check if sender has permissions to run this command.
				if (baseCommand.getPermissionNode() != null)
					if (!sender.hasPermission(baseCommand.getPermissionNode()))
					{
						sender.sendMessage(Messages.INVALID_PERMISSION);
						return false;
					}

				if (args.length > 0)
				{
					for (SubCommand subCommand : baseCommand.getSubCommands())
					{
						if (args[0].equalsIgnoreCase(subCommand.getName()))
						{
							// Check permission for sub-command.
							if (subCommand.getPermissionNode() != null)
								if (!sender.hasPermission(subCommand.getPermissionNode()))
								{
									sender.sendMessage(Messages.INVALID_PERMISSION);
									return false;
								}

							subCommand.execute(sender, args);
							return false;
						}
					}
				}

				baseCommand.execute(sender, args);
			}
		}

		return false;
	}

	public void saveReportsToConfig()
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
				String target = tempReport.getTargetPlayer().getUniqueId().toString();
				String reason = tempReport.getReason();
				Location location = tempReport.getLocation();

				reportsConfig.set(counter + ".reporter", reporter);
				reportsConfig.set(counter + ".target", target);
				reportsConfig.set(counter + ".reason", reason);
				if (location != null)
				{
					int x = (int) tempReport.getLocation().getX();
					int y = (int) tempReport.getLocation().getY();
					int z = (int) tempReport.getLocation().getZ();
					reportsConfig.set(counter + ".location.x", x);
					reportsConfig.set(counter + ".location.y", y);
					reportsConfig.set(counter + ".location.z", z);
					reportsConfig.set(counter + ".location.world", location.getWorld().getName());
				}
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

	public void loadReportsFromConfig()
	{
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
			Location location = null;
			String worldName = reportsConfig.getString(counter + ".location.world");

			if (reportsConfig.getConfigurationSection(counter + ".location") != null && worldName != null)
			{
				float x = reportsConfig.getInt(counter + ".location.x");
				float y = reportsConfig.getInt(counter + ".location.y");
				float z = reportsConfig.getInt(counter + ".location.z");

				location = new Location(Bukkit.getWorld(worldName), x, y, z);
			}

			if (reporterIdString == null || targetIdString == null || reason == null) break;

			OfflinePlayer reporter = Bukkit.getOfflinePlayer(UUID.fromString(reporterIdString));
			OfflinePlayer target = Bukkit.getOfflinePlayer(UUID.fromString(targetIdString));

			ReportPlayer reportPlayer = new ReportPlayer(reporter.getUniqueId(), reporter.getName());
			ReportPlayer targetPlayer = new ReportPlayer(target.getUniqueId(), target.getName());

			Report report = new Report(reportPlayer, targetPlayer, reason, location);
			reportHandler.addActiveReport(report);

			totalReportsLoaded++;
			counter++;
		}

		// Load archived reports
		counter = 0;
		while (archiveConfig.getString(String.valueOf(counter)) != null)
		{
			String reporterIdString = archiveConfig.getString(counter + ".reporter");
			String targetIdString = archiveConfig.getString(counter + ".target");
			String reason = archiveConfig.getString(counter + ".reason");

			if (reporterIdString == null || targetIdString == null || reason == null) break;

			OfflinePlayer reporter = Bukkit.getOfflinePlayer(UUID.fromString(reporterIdString));
			OfflinePlayer target = Bukkit.getOfflinePlayer(UUID.fromString(targetIdString));

			ReportPlayer reportPlayer = new ReportPlayer(reporter.getUniqueId(), reporter.getName());
			ReportPlayer targetPlayer = new ReportPlayer(target.getUniqueId(), target.getName());

			Report report = new Report(reportPlayer, targetPlayer, reason);
			reportHandler.addArchivedReport(report);

			totalReportsLoaded++;
			counter++;
		}
		int elapsedTime = (int) (System.currentTimeMillis() - startTime);
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "" + totalReportsLoaded + " report(s) has been loaded successfully, took " + elapsedTime + "ms.");
	}

	private void saveReportsConfig()
	{
		try
		{
			reportsConfig.save(reportsFile);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void saveArchiveConfig()
	{
		try
		{
			archiveConfig.save(archiveFile);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void registerCommand(BaseCommand command)
	{
		this.commands.add(command);
		getCommand(command.getName()).setExecutor(this);
	}

	public void reloadPlugin()
	{
		this.reloadConfig();
		Messages.loadMessages(getConfig());
	}
}
