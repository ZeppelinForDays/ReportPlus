package net.zeppelin.reportplus.reports;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReportHandler
{
	private List<Report> activeReports = new ArrayList<>();
	private List<Report> archivedReports = new ArrayList<>();

	public int getReportsCreatedForPlayer(UUID id)
	{
		int numberReports = 0;
		for (Report tempReport : activeReports)
		{
			if (tempReport.getReportPlayer().getUniqueId().equals(id))
			{
				numberReports++;
			}
		}

		return numberReports;
	}

	public int getReportsAgainstPlayer(UUID id)
	{
		int numberReports = 0;
		for (Report tempReport : activeReports)
		{
			if (tempReport.getTargetPlayer().getUniqueId().equals(id))
			{
				numberReports++;
			}
		}

		return numberReports;
	}

	public void addActiveReport(Report report)
	{
		this.activeReports.add(report);
	}

	public void removeActiveReport(Report report)
	{
		this.activeReports.remove(report);
	}

	public void addArchivedReport(Report report)
	{
		this.archivedReports.add(report);
	}

	public void removeArchivedReport(Report report)
	{
		this.archivedReports.remove(report);
	}

	public List<Report> getActiveReports()
	{
		return activeReports;
	}

	public void setActiveReports(List<Report> activeReports)
	{
		this.activeReports = activeReports;
	}

	public List<Report> getArchivedReports()
	{
		return archivedReports;
	}

	public void setArchivedReports(List<Report> archivedReports)
	{
		this.archivedReports = archivedReports;
	}
}
