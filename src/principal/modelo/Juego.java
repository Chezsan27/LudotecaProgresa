package principal.modelo;

import java.time.LocalDate;
import java.util.ArrayList;

public class Juego {
    private int id;
    private String nombre;
    private int num_Jugadores;
    private String tematica;
    private String tipo;
    private boolean disponibilidad;
    private LocalDate fechaEntrega;

    public Juego() {
    }

    public Juego(int id, String nombre, int num_Jugadores, String tematica, String tipo, boolean disponibilidad) {
        this.id = id;
        this.nombre = nombre;
        this.num_Jugadores = num_Jugadores;
        this.tematica = tematica;
        this.tipo = tipo;
        this.disponibilidad = disponibilidad;
    }

    public Juego(int id, String nombre, int num_Jugadores, String tematica, String tipo, boolean disponibilidad, LocalDate fechaEntrega) {
        this.id = id;
        this.nombre = nombre;
        this.num_Jugadores = num_Jugadores;
        this.tematica = tematica;
        this.tipo = tipo;
        this.disponibilidad = disponibilidad;
        this.fechaEntrega = fechaEntrega;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getNum_Jugadores() {
        return num_Jugadores;
    }

    public void setNum_Jugadores(int num_Jugadores) {
        this.num_Jugadores = num_Jugadores;
    }

    public String getTematica() {
        return tematica;
    }

    public void setTematica(String tematica) {
        this.tematica = tematica;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public boolean isDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(boolean disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

    public LocalDate getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(LocalDate fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

}
