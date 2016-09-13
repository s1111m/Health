package com.relsib.dao;

public class Measurment {
    public static final long UNSAVED_ID = -1;
    private Long id;
    private Long profile_id;
    private Long thermometer_id;
    private String date;
    private Double temperature;
    private String notice;

    public Measurment(Long profile_id, String date, Double temperature, String notice) {
        this.id = Measurment.UNSAVED_ID;
        this.profile_id = profile_id;
        this.date = date;
        this.temperature = temperature;
        this.notice = notice;
    }

    public static Measurment MeasurmentFactory(Long id, Long profile_id, Long thermometer_id, String date, Double temperature, String notice) {
        Measurment measurment = new Measurment(profile_id, date, temperature, notice);
        measurment.setId(id);
        measurment.setThermometer_id(thermometer_id);
        return measurment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getProfile_id() {
        return profile_id;
    }

    public void setProfile_id(Long profile_id) {
        this.profile_id = profile_id;
    }

    public void setProfile_id(long profile_id) {
        this.profile_id = profile_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String toString() {
        return this.getId() + " " + this.getProfile_id() + " " + this.getDate() + " " + this.getTemperature() + " " + getNotice();
    }

    public Long getThermometer_id() {
        return thermometer_id;
    }

    public void setThermometer_id(Long thermometer_id) {
        this.thermometer_id = thermometer_id;
    }
}
