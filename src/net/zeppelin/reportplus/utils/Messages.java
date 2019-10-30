package net.zeppelin.reportplus.utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class Messages
{
	public static String INVALID_PERMISSION = ChatColor.RED + "You don't have permission to do that.";
	public static String INVALID_SENDER = ChatColor.RED + "You must be a player to use this command.";
	public static String INVALID_TARGET_REPORT = ChatColor.RED + "That player does not have any active reports against them.";
	public static String REPORT_CLAIMED = ChatColor.RED + "This report is already claimed.";
	public static String CURRENTLY_NO_REPORTS = ChatColor.RED + "There are currently no reports available.";
	public static String REPORT_SELF = ChatColor.RED + "You cannot report yourself.";
	public static String PLAYER_KICKED = ChatColor.RED + "You have been kicked from this server.";
	public static String PLAYER_BANNED = ChatColor.RED + "You have been banned from this server.";
	public static String REPORT_LIMIT = ChatColor.RED + "You have reached the limit of reports you can create.";
	public static String INVALID_ARGUMENTS = ChatColor.RED + "Invalid arguments.";

	public static void loadMessages(FileConfiguration config)
	{
		String location = "messages.";

		String invalidPermission = config.getString(location + "invalid-permission");
		String invalidSender = config.getString(location + "invalid.sender");
		String invalidTargetReport = config.getString(location + "invalid-target-report");
		String reportClaimed = config.getString(location + "report-claimed");
		String currentlyNoReports = config.getString(location + "currently-no-reports");
		String reportSelf = config.getString(location + "report-self");
		String playerKicked = config.getString(location + "player-kicked");
		String playerBanned = config.getString(location + "player-banned");
		String reportLimit = config.getString(location + "report-limit");
		String invalidArguments = config.getString(location + "invalid-arguments");

		if (invalidPermission != null)
			INVALID_PERMISSION = ChatColor.translateAlternateColorCodes('&', invalidPermission);
		if (invalidSender != null)
            INVALID_SENDER = ChatColor.translateAlternateColorCodes('&', invalidSender);
		if (invalidTargetReport != null)
			INVALID_TARGET_REPORT = ChatColor.translateAlternateColorCodes('&', invalidTargetReport);
		if (reportClaimed != null)
			REPORT_CLAIMED = ChatColor.translateAlternateColorCodes('&', reportClaimed);
		if (currentlyNoReports != null)
			CURRENTLY_NO_REPORTS = ChatColor.translateAlternateColorCodes('&', currentlyNoReports);
		if (reportSelf != null)
			REPORT_SELF = ChatColor.translateAlternateColorCodes('&', reportSelf);
		if (playerKicked != null)
			PLAYER_KICKED = ChatColor.translateAlternateColorCodes('&', playerKicked);
		if (playerBanned != null)
			PLAYER_BANNED = ChatColor.translateAlternateColorCodes('&', playerBanned);
		if (reportLimit != null)
			REPORT_LIMIT = ChatColor.translateAlternateColorCodes('&', reportLimit);
		if (invalidArguments != null)
			INVALID_ARGUMENTS = ChatColor.translateAlternateColorCodes('&', invalidArguments);
	}
}
