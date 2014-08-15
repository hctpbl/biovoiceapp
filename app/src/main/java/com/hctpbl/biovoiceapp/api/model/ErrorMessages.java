package com.hctpbl.biovoiceapp.api.model;

import java.util.List;

public class ErrorMessages {
    private List<String> username;
    private List<String> first_name;
    private List<String> surname;
    private List<String> email;

    public List<String> getUsername() {
        return username;
    }

    public void setUsername(List<String> username) {
        this.username = username;
    }

    public List<String> getFirst_name() {
        return first_name;
    }

    public void setFirst_name(List<String> first_name) {
        this.first_name = first_name;
    }

    public List<String> getSurname() {
        return surname;
    }

    public void setSurname(List<String> surname) {
        this.surname = surname;
    }

    public List<String> getEmail() {
        return email;
    }

    public void setEmail(List<String> email) {
        this.email = email;
    }
}
