package net.zeppelin.reportplus.commands.impl;

import net.zeppelin.reportplus.commands.SubCommand;
import net.zeppelin.reportplus.reports.Report;
import net.zeppelin.reportplus.reports.ReportHandler;
import net.zeppelin.reportplus.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

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
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

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
            sender.sendMessage(Messages.INVALID_ARGUMENTS);
        }
    }
}
