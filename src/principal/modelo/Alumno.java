package principal.modelo;


import java.time.LocalDate;
import java.util.ArrayList;

public class Alumno {
    private String dni;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private boolean sancion;
    private ArrayList<Juego> juegos;
    private LocalDate fechaFinSancion;

    public Alumno() {
        this.juegos = new ArrayList<Juego>();
    }

    public Alumno(String dni, String nombre, String apellido, String email, String telefono, LocalDate fechaFinSancion) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.telefono = telefono;
        this.juegos = new ArrayList<Juego>();
        this.sancion = false;
        this.fechaFinSancion = fechaFinSancion;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public boolean isSancion() {
        return sancion;
    }

    public void setSancion(boolean sancion) {
        this.sancion = sancion;
    }

    public ArrayList<Juego> getJuegos() {
        return juegos;
    }

    public void setJuegos(ArrayList<Juego> juegos) {
        this.juegos = juegos;
    }

    public LocalDate getFechaFinSancion() {
        return fechaFinSancion;
    }

    public void setFechaFinSancion(LocalDate fechaFinSancion) {
        this.fechaFinSancion = fechaFinSancion;
    }
    public int getCantidadJuegos(){
        return juegos.size();
    }
}
