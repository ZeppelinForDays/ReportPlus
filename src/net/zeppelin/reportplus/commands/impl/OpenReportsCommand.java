package net.zeppelin.reportplus.commands.impl;

import net.zeppelin.reportplus.commands.BaseCommand;
import net.zeppelin.reportplus.inventories.ReportListInventory;
import net.zeppelin.reportplus.reports.ReportHandler;
import net.zeppelin.reportplus.utils.InventoryHandler;
import net.zeppelin.reportplus.utils.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpenReportsCommand extends BaseCommand
{
    private ReportHandler reportHandler;
    private InventoryHandler inventoryHandler;

    public OpenReportsCommand(ReportHandler reportHandler, InventoryHandler inventoryHandler)
    {
        super("reports", "reportplus.reports.manage");
        this.reportHandler = reportHandler;
        this.inventoryHandler = inventoryHandler;
    }

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        if (!(sender instanceof Player))
        {
            sender.sendMessage(Messages.INVALID_SENDER);
            return;
        }

        Player player = (Player) sender;

        // Opens active reports inventory
        if (reportHandler.getActiveReports().size() != 0)
            inventoryHandler.getReportListInventory().openInventory(player, ReportListInventory.ACTIVE_REPORTS);
        else
        {
            player.sendMessage(Messages.CURRENTLY_NO_REPORTS);
        }
    }
}
