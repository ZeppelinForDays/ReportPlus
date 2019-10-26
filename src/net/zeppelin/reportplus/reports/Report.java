package net.zeppelin.reportplus.reports;

import net.minecraft.server.v1_14_R1.Vector3f;
import net.zeppelin.reportplus.player.ReportPlayer;
import org.bukkit.Location;

public class Report
{
	private ReportPlayer reportPlayer;
	private ReportPlayer targetPlayer;
	private String reason;
	private boolean isClaimed = false;
	private ReportPlayer claimer;
	private Location location;

	public Report(ReportPlayer reportPlayer, ReportPlayer targetPlayer, String reason)
	{
		this.reportPlayer = reportPlayer;
		this.targetPlayer = targetPlayer;
		this.reason = reason;
		this.location = null;
	}

	public Report(ReportPlayer reportPlayer, ReportPlayer targetPlayer, String reason, Location location)
	{
		this.reportPlayer = reportPlayer;
		this.targetPlayer = targetPlayer;
		this.reason = reason;
        this.location = location;
	}

	public void claim(ReportPlayer claimer)
	{
		this.isClaimed = true;
		this.claimer = claimer;
	}

	public void unClaim()
	{
		this.isClaimed = false;
		this.claimer = null;
	}

	public ReportPlayer getReportPlayer()
	{
		return reportPlayer;
	}

	public void setReportPlayer(ReportPlayer reportPlayer)
	{
		this.reportPlayer = reportPlayer;
	}

	public ReportPlayer getTargetPlayer()
	{
		return targetPlayer;
	}

	public void setTargetPlayer(ReportPlayer targetPlayer)
	{
		this.targetPlayer = targetPlayer;
	}

	public String getReason()
	{
		return reason;
	}

	public void setReason(String reason)
	{
		this.reason = reason;
	}

	public boolean isClaimed()
	{
		return isClaimed;
	}

	public void setClaimed(boolean isClaimed)
	{
		this.isClaimed = isClaimed;
	}

	public ReportPlayer getClaimer()
	{
		return claimer;
	}

	public void setClaimer(ReportPlayer claimer)
	{
		this.claimer = claimer;
	}

    public Location getLocation()
    {
        return location;
    }

    public void setLocation(Location location)
    {
        this.location = location;
    }
}
