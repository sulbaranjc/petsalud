package com.example.petsalud.model;

public class DashboardStats {

    private long citasHoy;
    private long veterinariosActivos;
    private long mascotasRegistradas;
    private long propietarios;

    public DashboardStats() {}

    public long getCitasHoy()                            { return citasHoy; }
    public void setCitasHoy(long citasHoy)               { this.citasHoy = citasHoy; }

    public long getVeterinariosActivos()                              { return veterinariosActivos; }
    public void setVeterinariosActivos(long veterinariosActivos)      { this.veterinariosActivos = veterinariosActivos; }

    public long getMascotasRegistradas()                              { return mascotasRegistradas; }
    public void setMascotasRegistradas(long mascotasRegistradas)      { this.mascotasRegistradas = mascotasRegistradas; }

    public long getPropietarios()                        { return propietarios; }
    public void setPropietarios(long propietarios)       { this.propietarios = propietarios; }
}
