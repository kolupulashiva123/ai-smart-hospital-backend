package com.smarthospital.dto;

public class DashboardStats {
    private long totalPatients;
    private long totalDoctors;
    private long totalAppointments;
    private long pending;
    private long confirmed;
    private long completed;
    private long cancelled;
    private long todayAppointments;
    private double revenue;

    public long getTotalPatients() { return totalPatients; }
    public void setTotalPatients(long v) { this.totalPatients = v; }
    public long getTotalDoctors() { return totalDoctors; }
    public void setTotalDoctors(long v) { this.totalDoctors = v; }
    public long getTotalAppointments() { return totalAppointments; }
    public void setTotalAppointments(long v) { this.totalAppointments = v; }
    public long getPending() { return pending; }
    public void setPending(long v) { this.pending = v; }
    public long getConfirmed() { return confirmed; }
    public void setConfirmed(long v) { this.confirmed = v; }
    public long getCompleted() { return completed; }
    public void setCompleted(long v) { this.completed = v; }
    public long getCancelled() { return cancelled; }
    public void setCancelled(long v) { this.cancelled = v; }
    public long getTodayAppointments() { return todayAppointments; }
    public void setTodayAppointments(long v) { this.todayAppointments = v; }
    public double getRevenue() { return revenue; }
    public void setRevenue(double v) { this.revenue = v; }
}
