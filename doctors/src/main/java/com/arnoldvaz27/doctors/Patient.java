package com.arnoldvaz27.doctors;

public class Patient {
    public String name,doctorAppointed;

    public Patient()
    {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDoctorAppointed() {
        return doctorAppointed;
    }

    public void setDoctorAppointed(String doctorAppointed) {
        this.doctorAppointed = doctorAppointed;
    }
}
