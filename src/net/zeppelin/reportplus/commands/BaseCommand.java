package net.zeppelin.reportplus.commands;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseCommand
{
    private String name;
    private List<SubCommand> subCommands = new ArrayList<>();
    private String permissionNode;

    public BaseCommand(String name, String permissionNode)
    {
        this.name = name;
        this.permissionNode = permissionNode;
    }

    public abstract void execute(CommandSender sender, String[] args);

    public void addSubCommand(SubCommand subCommand)
    {
        this.subCommands.add(subCommand);
    }

    public void removeSubCommand(SubCommand subCommand)
    {
        this.subCommands.remove(subCommand);
    }

    public List<SubCommand> getSubCommands()
    {
        return subCommands;
    }

    public void setSubCommands(List<SubCommand> subCommands)
    {
        this.subCommands = subCommands;
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
