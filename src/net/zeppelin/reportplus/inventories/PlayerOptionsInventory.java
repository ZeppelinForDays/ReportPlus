package net.zeppelin.reportplus.inventories;

import net.zeppelin.reportplus.reports.Report;
import net.zeppelin.reportplus.utils.InventoryHandler;
import net.zeppelin.reportplus.utils.ItemUtils;
import net.zeppelin.reportplus.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Collections;

public class PlayerOptionsInventory extends ReportInventory
{
	Report report;
	
	public PlayerOptionsInventory(InventoryHandler inventoryHandler, int id)
	{
		super(inventoryHandler, id, true);
	}

	@Override
	public void loadContents()
	{
		if (report == null) return;
		
		inventory = Bukkit.createInventory(null, 27, "Player Options");

		inventory.setItem(11, ItemUtils.createItem(Material.ENCHANTED_BOOK, "§cBan Player IP", Collections.singletonList("§7Ban reported player from joining your server again.")));
		inventory.setItem(13, ItemUtils.createItem(Material.ENCHANTED_BOOK, "§cKick Player", Collections.singletonList("§7Kick the reported player from the server.")));
		inventory.setItem(15, ItemUtils.createItem(Material.ENCHANTED_BOOK, "§aTeleport", Collections.singletonList("§7Teleport to the reported player.")));
		inventory.setItem(18, ItemUtils.createItem(Material.ARROW, "§cBack", null));
	}

	@Override
	public void onInventoryClickEvent(InventoryClickEvent event)
	{
		Player player = (Player) event.getWhoClicked();
		Player target = report.getTargetPlayer().getHandler();
		int slot = event.getSlot();

		if (slot == 11) // Ban Player
		{
			Bukkit.banIP(target.getAddress().toString());
			target.kickPlayer(Messages.PLAYER_BANNED);
			player.closeInventory();
			player.sendMessage("§7You banned §6" + target.getName() + "§7 from the server.");
		}
		else if (slot == 13) // Kick Player
		{
			target.kickPlayer(Messages.PLAYER_KICKED);
			player.closeInventory();
			player.sendMessage("§7You kicked §6" + target.getName() + "§7 from the server.");
		}
		else if (slot == 15) // Teleport
		{
			player.teleport(target.getLocation());
			player.closeInventory();
			player.sendMessage("§7Teleported you to §6" + target.getName());
		}
		else if (slot == 18)
		{
			inventoryHandler.getClickedReportInventory().openInventoryFromReport(player, report);
		}
	}
	
	public void openInventoryFromReport(Player player, Report report)
	{
		this.report = report;
		super.openInventory(player);
	}
}
