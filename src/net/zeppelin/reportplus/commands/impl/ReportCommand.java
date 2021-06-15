package net.zeppelin.reportplus.commands.impl;

import net.zeppelin.reportplus.commands.BaseCommand;
import net.zeppelin.reportplus.main.ReportPlusPlugin;
import net.zeppelin.reportplus.player.PlayerHandler;
import net.zeppelin.reportplus.player.ReportPlayer;
import net.zeppelin.reportplus.reports.Report;
import net.zeppelin.reportplus.reports.ReportHandler;
import net.zeppelin.reportplus.utils.InventoryHandler;
import net.zeppelin.reportplus.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReportCommand extends BaseCommand
{
    private PlayerHandler playerHandler;
    private ReportHandler reportHandler;
    private InventoryHandler inventoryHandler;

    public ReportCommand(PlayerHandler playerHandler, ReportHandler reportHandler, InventoryHandler inventoryHandler, ReportPlusPlugin plugin)
    {
        super("report", null);
        this.playerHandler = playerHandler;
        this.reportHandler = reportHandler;
        this.inventoryHandler = inventoryHandler;
        addSubCommand(new RemoveAllSubCommand(reportHandler));
        addSubCommand(new ReloadSubCommand(plugin));
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

        if (args.length <= 0)
        {
            // Checks permission
            if (!player.hasPermission("reportplus.reports.manage"))
            {
                player.sendMessage(Messages.INVALID_PERMISSION);
                return;
            }

            // Opens report inventory
            inventoryHandler.getMainInventory().openInventory(player);
        } else if (args.length == 1)
        {
            // Checks permission
            if (!player.hasPermission("reportplus.report"))
            {
                player.sendMessage(Messages.INVALID_PERMISSION);
                return;
            }

            player.sendMessage(Messages.NO_REASON);
        } else if (args.length >= 2)
        {
            // Checks permission
            if (!player.hasPermission("reportplus.report"))
            {
                player.sendMessage(Messages.INVALID_PERMISSION);
                return;
            }

            // Check if player has reached the limit reports.
            if (ReportPlusPlugin.LIMIT_REPORTS)
            {
                if (reportHandler.getReportsCreatedForPlayer(player.getUniqueId()) >= ReportPlusPlugin.REPORT_LIMIT)
                {
                    player.sendMessage(Messages.REPORT_LIMIT);
                    return;
                }
            }
            ReportPlayer reportPlayer = playerHandler.getReportPlayerFromId(player.getUniqueId());

            // Continue reporting command
            Player target = Bukkit.getPlayer(args[0]);

            if (target != null)
            {
                if (target.hasPermission("reportplus.reports.exempt"))
                {
                    player.sendMessage(Messages.PLAYER_EXEMPT);
                    return;
                }

                ReportPlayer targetPlayer = playerHandler.getReportPlayerFromId(target.getUniqueId());
                String reason = "";

                // Check if player is reporting themselves.
                if (target.getUniqueId().equals(player.getUniqueId()))
                {
                    player.sendMessage(Messages.REPORT_SELF);
                    return;
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
                float x = (float) player.getLocation().getX();
                float y = (float) player.getLocation().getY();
                float z = (float) player.getLocation().getZ();
                Report report = new Report(reportPlayer, targetPlayer, reason, player.getLocation());
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
            }
        }
    }
}
