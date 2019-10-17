package net.zeppelin.reportplus.commands;

import org.bukkit.command.CommandSender;

public abstract class SubCommand
{
    protected String name;
    protected String permissionNode;

    public SubCommand(String name, String permissionNode)
    {
        this.name = name;
        this.permissionNode = permissionNode;
    }

    public abstract void execute(CommandSender sender, String[] args);

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
