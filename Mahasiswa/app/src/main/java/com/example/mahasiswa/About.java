package com.example.mahasiswa;

public class About {
    private String Name;
    private String Nim;
    private String Email;

    public About() {
    }

    public About(String name, String nim, String email) {
        Name = name;
        Nim = nim;
        Email = email;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getNim() {
        return Nim;
    }

    public void setNim(String nim) {
        Nim = nim;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}
