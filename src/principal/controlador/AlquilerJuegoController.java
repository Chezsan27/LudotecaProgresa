package principal.controlador;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import principal.modelo.Alquiler;
import principal.modelo.Alumno;
import principal.modelo.Juego;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class AlquilerJuegoController {
    @FXML
    private ComboBox cbJuegosDiponibles;
    @FXML
    private Button btnAlquiler;

    private ObservableList<String> contenidoLista = FXCollections.observableArrayList();
    private ArrayList<Juego> juegosDisponibles;
    private Alumno alumno;
    private Juego juegoSeleccionado;
    private PrincipalController ventanaPrincipal;

    /**
     * Función que carga la información de los juegos disponibles para alquilar
     * para el alumno que se va a gestionar el aquiler
     * @param alumno->alumno para el que se va a gestionar un nuevo alquiler
     */
    public void inicializaDatos(Alumno alumno) {
        getJuegosDisponibles();
        rellenaListaJuegos();
        this.alumno = alumno;
    }

    private void getJuegosDisponibles() {
        try {
            this.juegosDisponibles = BaseDatosController.getArrayJuegosDisponibles(BaseDatosController.getConnection());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private void rellenaListaJuegos() {
        contenidoLista.clear();
        for (Juego j:this.juegosDisponibles
        ) {
            contenidoLista.add(j.getNombre()+"");
            System.out.println(j.getNombre());
        }
        cbJuegosDiponibles.setItems(contenidoLista);
    }

    /**
     * Función que carga la información del nuevo alquiler en la base de datos
     * una vez realizada la actualización escribirá la información en un fichero
     * de texto y cerrará la ventana
     * @param mouseEvent
     */
    public void realizarAlquiler(MouseEvent mouseEvent) {
        if (cbJuegosDiponibles.getValue()!=null){

            this.juegoSeleccionado=setJuegoSeleccinado();

            try {
                if(BaseDatosController.realizaAlquiler(BaseDatosController.getConnection(), this.alumno, this.juegoSeleccionado)){
                    this.alumno.setJuegos(BaseDatosController.getJuegosAlquiladosAlumno(BaseDatosController.getConnection(), this.alumno.getDni()));
                    escribeAlquilerFichero();

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Alquiler");
                    alert.setHeaderText("Alquiler realizado con exito");
                    alert.showAndWait();
                    //Cerrar ventana actual
                    Node source = (Node) mouseEvent.getSource();
                    Stage stage = (Stage) source.getScene().getWindow();
                    stage.close();

                }else{
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("ERROR al realizar alquier");
                    alert.setHeaderText("No se ha podido realizar el alquiler");
                    alert.showAndWait();
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR al realizar alquier");
            alert.setHeaderText("Debes seleccionar un juego para alquilar");
            alert.showAndWait();
        }

    }

    private Juego setJuegoSeleccinado() {
        Juego juego = null;
        for (Juego j:juegosDisponibles
        ) {
            if (j.getNombre().equalsIgnoreCase((String) cbJuegosDiponibles.getValue())){
                juego=j;
            }
        }
        return juego;
    }

    private void escribeAlquilerFichero() {
        Alquiler alquiler = null;
        try {
            alquiler = BaseDatosController.getOneAlquilerJuego(BaseDatosController.getConnection(),juegoSeleccionado.getId(),this.alumno.getDni());
            FicheroController.agregarAlquiler(alquiler.codificaAlquiler());

        } catch (SQLException exception) {
            exception.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR Escribir");
            alert.setHeaderText("No se ha podido realizar la conexión");
            alert.showAndWait();
        }  catch (IOException e) {
            e.printStackTrace();
            try {
                FicheroController.escritor.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR FICHERO");
                alert.setHeaderText("No se ha podido realizar la escritura en el fichero");
                alert.showAndWait();
            }
        }
    }
}
