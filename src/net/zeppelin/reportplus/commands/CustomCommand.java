package net.zeppelin.reportplus.commands;

import net.zeppelin.reportplus.utils.Messages;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class CustomCommand implements CommandExecutor
{
    private String name;
    private List<SubCommand> subCommands = new ArrayList<>();
    private String permissionNode;

    public CustomCommand(String name, String permissionNode)
    {
        this.name = name;
        this.permissionNode = permissionNode;
    }

    public abstract void execute(CommandSender sender, String[] args);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (command.getName().equalsIgnoreCase(name))
        {
            // Check if sender has permissions to run this command.
            if (permissionNode != null)
                if (!sender.hasPermission(permissionNode))
                {
                    sender.sendMessage(Messages.INVALID_PERMISSION);
                    return false;
                }

            if (args.length > 0)
            {
                for (SubCommand tempCommand : subCommands)
                {
                    if (args[0].equalsIgnoreCase(tempCommand.getName()))
                    {
                        // Check permission for sub-command.
                        if (tempCommand.getPermissionNode() != null)
                            if (!sender.hasPermission(tempCommand.getPermissionNode()))
                            {
                                sender.sendMessage(Messages.INVALID_PERMISSION);
                                return false;
                            }

                        tempCommand.execute(sender, args);
                        return false;
                    }
                }
            }

            execute(sender, args);
        }
        return false;
    }

    public void addSubCommand(SubCommand subCommand)
    {
        this.subCommands.add(subCommand);
    }

    public void removeSubCommand(SubCommand subCommand)
    {
        this.subCommands.remove(subCommand);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPermissionNode()
    {
        return permissionNode;
    }

    public void setPermissionNode(String permissionNode)
    {
        this.permissionNode = permissionNode;
    }
}
