package com.example.ta;

public class About {
    private String Name;
    private String Nip;
    private String Email;

    public About() {
    }

    public About(String name, String nip, String email) {
        Name = name;
        Nip = nip;
        Email = email;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getNip() {
        return Nip;
    }

    public void setNip(String nip) {
        Nip = nip;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}
