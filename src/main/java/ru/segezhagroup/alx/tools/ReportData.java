package ru.segezhagroup.alx.tools;

import java.util.List;

public class ReportData {

    private String issueNumber;
    private String issueType;
    private String summary;
    private String assignee;
    private String reporter;
    private String priority;
    private String status;
    private String resolution;
    private String createDate;
    private String updateDate;
    private String execDate;
    private String exceedDays;
    private String department;
    private String labels;
    private String resolutionDate;
    private List<String> slaInfo;


    public ReportData() {
    }

    public ReportData(String issueNumber, String issueType, String summary, String assignee, String reporter, String priority, String status, String resolution, String createDate, String updateDate, String execDate, String exceedDays, String department, String labels, String resolutionDate, List<String> slaInfo) {
        this.issueNumber = issueNumber;
        this.issueType = issueType;
        this.summary = summary;
        this.assignee = assignee;
        this.reporter = reporter;
        this.priority = priority;
        this.status = status;
        this.resolution = resolution;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.execDate = execDate;
        this.exceedDays = exceedDays;
        this.department = department;
        this.labels = labels;
        this.resolutionDate = resolutionDate;
        this.slaInfo = slaInfo;
    }

    public String getIssueNumber() {
        return issueNumber;
    }

    public void setIssueNumber(String issueNumber) {
        this.issueNumber = issueNumber;
    }

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getExecDate() {
        return execDate;
    }

    public void setExecDate(String execDate) {
        this.execDate = execDate;
    }

    public String getExceedDays() {
        return exceedDays;
    }

    public void setExceedDays(String exceedDays) {
        this.exceedDays = exceedDays;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public String getResolutionDate() {
        return resolutionDate;
    }

    public void setResolutionDate(String resolutionDate) {
        this.resolutionDate = resolutionDate;
    }

    public List<String> getSlaInfo() {
        return slaInfo;
    }

    public void setSlaInfo(List<String> slaInfo) {
        this.slaInfo = slaInfo;
    }
}
