
package com.mycompany.fixdemo12;

import java.util.Date;

public class Student {
    String ID, name, phone, address, mark;    
    Date dob;

    public Student(String ID, String name, String phone, String address, String mark, Date dob) {
        this.ID = ID;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.dob = dob;
        this.mark = mark;
    }

    public Student() {

    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getMark() {
        return mark;
    }

    public Date getDob() {
        return dob;
    }

}
