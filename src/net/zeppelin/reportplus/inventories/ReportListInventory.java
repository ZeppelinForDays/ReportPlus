package net.zeppelin.reportplus.inventories;

import java.util.ArrayList;
import java.util.List;

import net.zeppelin.reportplus.player.PlayerHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import net.zeppelin.reportplus.reports.Report;
import net.zeppelin.reportplus.reports.ReportHandler;
import net.zeppelin.reportplus.utils.InventoryHandler;
import net.zeppelin.reportplus.utils.ItemUtils;
import net.zeppelin.reportplus.utils.Messages;

@SuppressWarnings("deprecation")
public class ReportListInventory extends ReportInventory
{
    private ReportHandler reportHandler;
    private int type;

    public static final int ACTIVE_REPORTS = 0;
    public static final int ARCHIVED_REPORTS = 1;

    public ReportListInventory(InventoryHandler inventoryHandler, int id, ReportHandler reportHandler)
    {
        super(inventoryHandler, id, true);
        this.reportHandler = reportHandler;
    }

    @Override
    public void loadContents()
    {
        // Prepare before inventory
        List<Report> reports = new ArrayList<Report>();
        String inventoryName = "";

        if (type == ACTIVE_REPORTS)
        {
            reports = reportHandler.getActiveReports();
            inventoryName = "Active Reports";
        } else if (type == ARCHIVED_REPORTS)
        {
            reports = reportHandler.getArchivedReports();
            inventoryName = "Archived Reports";
        }

        inventory = Bukkit.createInventory(null, 54, inventoryName);

        // Load inventory
        for (int i = 0; i < reports.size(); i++)
        {
            // Makes sure the list of reports don't override any menu options.
            if (i >= 45) break;

            Report tempReport = reports.get(i);
            String reporterName = tempReport.getReportPlayer().getName();
            String targetPlayerName = tempReport.getTargetPlayer().getName();
	    
	    String[] reason = tempReport.getReason().split("(?<=\\G.{15})");
            List<String> lore = new ArrayList<String>();
            lore.add("§6Reported By: §7" + reporterName);
            lore.add("§6Reason: §7" + reaason[0]);
	    lore.addAll(Arrays.copyOfRange(reason, 1, reason.length));
            if (type == ACTIVE_REPORTS)
            {
                if (tempReport.isClaimed())
                {
                    String claimerName = tempReport.getClaimer().getName();
                    lore.add("");
                    lore.add(ChatColor.GREEN + claimerName + " is handling this report.");
                }
            }
            ItemStack item = new ItemStack(Bukkit.getVersion().contains("1.14") ? Material.valueOf("PLAYER_HEAD") : Material.valueOf("SKULL_ITEM"));
            if (!Bukkit.getVersion().contains("1.14"))
            {
                item = new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (byte) SkullType.PLAYER.ordinal());
            }
            SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(tempReport.getTargetPlayer().getUniqueId()));
            item.setItemMeta(skullMeta);
            inventory.setItem(i, ItemUtils.createItem(item, ChatColor.RED + targetPlayerName, lore));
        }
		inventory.setItem(45, ItemUtils.createItem(Material.ARROW, ChatColor.RED + "Back", null));
		inventory.setItem(49, ItemUtils.createItem(Material.PAPER, ChatColor.GREEN + "Refresh", null));
    }

    @Override
    public void onInventoryClickEvent(InventoryClickEvent event)
    {
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();

        if (!player.hasPermission("reportplus.reports.manage"))
        {
            player.sendMessage(Messages.INVALID_PERMISSION);
            player.closeInventory();
            return;
        }

        if (slot == 45)
        {
            inventoryHandler.getMainInventory().openInventory(player);
            return;
        } else if (slot == 49)
        {
        	openInventory(player, type);
        }

        if (type == ACTIVE_REPORTS)
            if (slot < reportHandler.getActiveReports().size() && slot >= 0 && slot <= 44)
            {
                Report clickedReport = reportHandler.getActiveReports().get(slot);
                inventoryHandler.getClickedReportInventory().openInventoryFromReport(player, clickedReport);
            }
    }

    public void openInventory(Player player, int type)
    {
        this.type = type;
        super.openInventory(player);
    }
}
