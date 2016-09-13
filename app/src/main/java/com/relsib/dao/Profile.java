package com.relsib.dao;

/**
 * Created by S1M on 07.08.2016.
 */
public class Profile {
    //Определение переменных
    public static final long UNSAVED_ID = -1;
    public Long id; //readonly
    public String name;
    public String password;
    public Integer age;

    //создает, но не записывает в базу
    public Profile(String name, Integer age, String password) {
        this.id = Profile.UNSAVED_ID;
        this.name = name;
        this.password = password;
        this.age = age;
    }

    public static Profile Parse(Long id, String name, Integer age, String password) {
        Profile profile = new Profile(name, age, password);
        profile.setId(id);
        return profile;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String toString() {
        return this.getId() + " " + this.getName() + " " + this.getAge() + " " + this.getPassword();
    }
}
