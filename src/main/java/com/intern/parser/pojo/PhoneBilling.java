package com.intern.parser.pojo;

import java.util.Date;

public class PhoneBilling {
    private boolean isIncoming;
    private Date startTime;
    private Date endTime;
    private String tariff;
    private String phoneNumber;

    public boolean isIncoming() {
        return isIncoming;
    }

    public void setIncoming(boolean incoming) {
        isIncoming = incoming;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getTariff() {
        return tariff;
    }

    public void setTariff(String tariff) {
        this.tariff = tariff;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "PhoneBilling{" +
                "isIncoming=" + isIncoming + "\n" +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", tariff='" + tariff + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
