package net.zeppelin.reportplus.reports;

import java.util.ArrayList;
import java.util.List;

public class ReportHandler
{
	private List<Report> activeReports = new ArrayList<Report>();
	private List<Report> archivedReports = new ArrayList<Report>();

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
