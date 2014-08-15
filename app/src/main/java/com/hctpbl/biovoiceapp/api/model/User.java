package com.hctpbl.biovoiceapp.api.model;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable{
    private String username;
    private String first_name;
    private String surname;
    private String email;
    private Date time_reg;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Date getTime_reg() {
        return time_reg;
    }

    public void setTime_reg(Date time_reg) {
        this.time_reg = time_reg;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
