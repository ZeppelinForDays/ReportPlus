package net.zeppelin.reportplus.utils;

import org.bukkit.ChatColor;

public class Messages
{
	public static final String INVALID_PERMISSION = ChatColor.RED + "You don't have permission to do that.";
	public static final String INVALID_SENDER = ChatColor.RED + "You must be a player to use this command.";
	public static final String INVALID_TARGET_REPORT = ChatColor.RED + "That player does not have any active reports against them.";
	public static final String REPORT_CLAIMED = ChatColor.RED + "This report is already claimed.";
	public static final String CURRENTLY_NO_REPORTS = ChatColor.RED + "There are currently no reports available.";
	public static final String REPORT_SELF = ChatColor.RED + "You cannot report yourself.";
	public static final String PLAYER_KICKED = ChatColor.RED + "You have been kicked from this server.";
	public static final String PLAYER_BANNED = ChatColor.RED + "You have been banned from this server.";
	public static final String REPORT_LIMIT = ChatColor.RED + "You have reached the limit of reports you can create.";
	public static final String INVALID_ARGUMENTS = ChatColor.RED + "Invalid arguments.";
}
