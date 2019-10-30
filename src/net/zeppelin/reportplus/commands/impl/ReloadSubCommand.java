package net.zeppelin.reportplus.commands.impl;

import net.zeppelin.reportplus.commands.SubCommand;
import net.zeppelin.reportplus.main.ReportPlusPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ReloadSubCommand extends SubCommand
{
    private ReportPlusPlugin plugin;

    public ReloadSubCommand(ReportPlusPlugin plugin)
    {
        super("reload", "reportplus.reload");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        plugin.reloadPlugin();
        sender.sendMessage(ChatColor.GREEN + "Configuration file has been successfully reloaded.");
    }
}
