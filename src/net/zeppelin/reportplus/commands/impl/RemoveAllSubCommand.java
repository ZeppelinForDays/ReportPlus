package net.zeppelin.reportplus.commands.impl;

import net.zeppelin.reportplus.commands.SubCommand;
import net.zeppelin.reportplus.reports.Report;
import net.zeppelin.reportplus.reports.ReportHandler;
import net.zeppelin.reportplus.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RemoveAllSubCommand extends SubCommand
{
    private ReportHandler reportHandler;

    public RemoveAllSubCommand(ReportHandler reportHandler)
    {
        super("removeall", "reportplus.reports.remove");
        this.reportHandler = reportHandler;
    }

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        if (args.length >= 2)
        {
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
                    sender.sendMessage("§6" + deleteCounter + "§7 report(s) has been removed for §6" + target.getName());
                } else
                {
                    // No reports for this user
                    sender.sendMessage(Messages.INVALID_TARGET_REPORT);
                }
            } else
            {
                // Could not find player
                sender.sendMessage(ChatColor.RED + "Could not find player: " + args[1]);
            }
        } else
        {
            sender.sendMessage(Messages.INVALID_ARGUMENTS);
        }
    }
}
