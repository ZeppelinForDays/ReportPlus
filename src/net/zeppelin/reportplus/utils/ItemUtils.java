package net.zeppelin.reportplus.utils;

import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtils
{
	public static ItemStack createItem(Material material, @Nullable String displayName, @Nullable List<String> lore)
	{
		ItemStack item = new ItemStack(material);
		ItemMeta itemMeta = item.getItemMeta();

		if (displayName != null)
		{
			itemMeta.setDisplayName(displayName);
		}

		if (lore != null)
		{
			itemMeta.setLore(lore);
		}

		item.setItemMeta(itemMeta);
		return item;
	}

	public static ItemStack createItem(ItemStack item, @Nullable String displayName, @Nullable List<String> lore)
	{
		ItemMeta itemMeta = item.getItemMeta();

		if (displayName != null)
		{
			itemMeta.setDisplayName(displayName);
		}

		if (lore != null)
		{
			itemMeta.setLore(lore);
		}

		item.setItemMeta(itemMeta);
		return item;
	}
}
