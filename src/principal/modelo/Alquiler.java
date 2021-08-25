package principal.modelo;

import principal.controlador.BaseDatosController;

import java.sql.SQLException;
import java.time.LocalDate;

public class Alquiler {
    private int idAlquiler;
    private String idAlumno;
    private int idJuego;
    private LocalDate fechaEntrega;
    private boolean entregado;

    public Alquiler() {
    }

    public Alquiler(int idAlquiler, String idAlumno, int idJuego, LocalDate fechaEntrega, boolean entregado) {
        this.idAlquiler = idAlquiler;
        this.idAlumno = idAlumno;
        this.idJuego = idJuego;
        this.fechaEntrega = fechaEntrega;
        this.entregado = entregado;
    }

    public int getIdAlquiler() {
        return idAlquiler;
    }

    public void setIdAlquiler(int idAlquiler) {
        this.idAlquiler = idAlquiler;
    }

    public String getIdAlumno() {
        return idAlumno;
    }

    public void setIdAlumno(String idAlumno) {
        this.idAlumno = idAlumno;
    }

    public int getIdJuego() {
        return idJuego;
    }

    public void setIdJuego(int idJuego) {
        this.idJuego = idJuego;
    }

    public LocalDate getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(LocalDate fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public boolean isEntregado() {
        return entregado;
    }

    public void setEntregado(boolean entregado) {
        this.entregado = entregado;
    }
    public String codificaAlquiler() throws SQLException {
        Alumno alumno = BaseDatosController.getAlumno(BaseDatosController.getConnection(), idAlumno);
        Juego juego = BaseDatosController.getJuego(BaseDatosController.getConnection(), idJuego);
        return alumno.getNombre()+";"+alumno.getApellido()+";"+juego.getNombre()+";"+getFechaEntrega();
    }
}
