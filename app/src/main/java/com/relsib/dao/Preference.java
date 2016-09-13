package com.relsib.dao;

/**
 * Created by S1M on 07.08.2016.
 */
public class Preference {
    private Long id;
    private Long profile_id;
    private String degreesType;
    private String lastDeviceName;
    private String lastDeviceMac;
    private Integer lastDeviceAutoconnect;

    public Preference(Long profile_id, String degreesType, String lastDeviceName, String lastDeviceMac, Integer autoconnect) {
        this.id = TablePreferences.UNSAVED_ID;
        this.profile_id = profile_id;
        this.degreesType = degreesType;
        this.lastDeviceName = lastDeviceName;
        this.lastDeviceMac = lastDeviceMac;
        this.lastDeviceAutoconnect = autoconnect;
    }

    public Preference() {

    }

    public static Preference Parse(Long id, Long profile_id, String degreesType, String lastDeviceName, String lastDeviceMac, Integer autoconnect) {
        Preference preference = new Preference();
        preference.setId(id);
        preference.setProfile_id(profile_id);
        preference.setDegreesType(degreesType);
        preference.setLastDeviceName(lastDeviceName);
        preference.setLastDeviceMac(lastDeviceMac);
        preference.setLastDeviceAutoconnect(autoconnect);
        return preference;
    }

    public Integer getLastDeviceAutoconnect() {
        return lastDeviceAutoconnect;
    }

    public void setLastDeviceAutoconnect(Integer lastDeviceAutoconnect) {
        this.lastDeviceAutoconnect = lastDeviceAutoconnect;
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

    public String getDegreesType() {
        return degreesType;
    }

    public void setDegreesType(String degreesType) {
        this.degreesType = degreesType;
    }

    public String getLastDeviceName() {
        return lastDeviceName;
    }

    public void setLastDeviceName(String lastDeviceName) {
        this.lastDeviceName = lastDeviceName;
    }

    public String getLastDeviceMac() {
        return lastDeviceMac;
    }

    public void setLastDeviceMac(String lastDeviceMac) {
        this.lastDeviceMac = lastDeviceMac;
    }

    public String toString() {
        return this.getId() + this.getProfile_id() + this.getDegreesType() + this.getLastDeviceName() + this.getLastDeviceMac() + this.getLastDeviceAutoconnect();
    }

}
