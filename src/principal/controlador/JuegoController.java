package principal.controlador;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import principal.modelo.Juego;

import java.sql.SQLException;
import java.util.ArrayList;

public class JuegoController {

    @FXML
    ComboBox cbListaJuegos;
    @FXML
    TextField txtNombre;
    @FXML
    TextField txtJugadores;
    @FXML
    TextField txtTematica;
    @FXML
    TextField txtTipo;
    @FXML
    TextField txtDisponible;


    private ObservableList<String> contenidoLista = FXCollections.observableArrayList();
    private Juego juego;
    private ArrayList<Juego> juegos;

    public void inicializaDatos() {
        try {
            this.juegos = BaseDatosController.getArrayJuegos(BaseDatosController.getConnection());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        rellenaListaJuegos();

        this.juego = new Juego();
        txtDisponible.setText("true");
        txtDisponible.setEditable(false);
    }

    private void rellenaListaJuegos() {
        contenidoLista.clear();
        for (Juego j : this.juegos
        ) {
            contenidoLista.add(j.getId() + "");
        }
        cbListaJuegos.setItems(contenidoLista);
    }

    public void cargarInfoJuego(MouseEvent mouseEvent) {
        System.out.println(cbListaJuegos.getValue());

        if (cbListaJuegos.getValue() != null) {
            try {
                this.juego = BaseDatosController.getJuego(BaseDatosController.getConnection(), Integer.parseInt((String) cbListaJuegos.getValue()));
                txtNombre.setText(juego.getNombre());
                txtJugadores.setText("" + juego.getNum_Jugadores());
                txtTematica.setText(juego.getTematica());
                txtTipo.setText(juego.getTipo());
                txtDisponible.setText("" + juego.isDisponibilidad());
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }

    /**
     * Eliminará el juego siempre y cuando este esté disponible (sin alquileres activos)
     *
     * @param mouseEvent
     */
    public void eliminarJuego(MouseEvent mouseEvent) {
        if (cbListaJuegos.getValue() != null && this.juego.isDisponibilidad()) {
            try {
                System.out.println(this.juego.getId());
                if (BaseDatosController.deleteJuego(BaseDatosController.getConnection(), this.juego.getId())) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Juego eliminado");
                    alert.setHeaderText("El juego se ha eliminado con exito");
                    alert.showAndWait();
                    this.juegos.removeIf(n -> (n.getId() == this.juego.getId()));
                    System.out.println(this.juegos.size());
                    rellenaListaJuegos();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Juego Eliminado");
                    alert.setHeaderText("No se ha eliminado el juego");
                    alert.showAndWait();
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Juego no seleccionado o no Disponible");
            alert.setHeaderText("Se debe elegir un juego de la base de datos para poder eliminar y estar Disponible");
            alert.showAndWait();
        }
    }

    public void nuevoJuego(MouseEvent mouseEvent) {
        limpiar();
    }

    private void limpiar() {
        txtDisponible.setText("true");
        txtTipo.clear();
        txtTematica.clear();
        txtJugadores.clear();
        txtNombre.clear();
        cbListaJuegos.setValue(null);
    }

    private void crearJuego() {
        this.juego.setTipo(txtTipo.getText());
        this.juego.setTematica(txtTematica.getText());
        this.juego.setNum_Jugadores(Integer.parseInt(txtJugadores.getText()));
        this.juego.setNombre(txtNombre.getText());
        this.juego.setDisponibilidad(Boolean.parseBoolean(txtDisponible.getText()));
    }

    /**
     * Si existe un juego seleccionado se actualizará la información en la base de datos
     * en caso contrario se creará uno nuevo
     *
     * @param mouseEvent
     */
    public void guardarJuego(MouseEvent mouseEvent) {
        if (cbListaJuegos.getValue() != null) {
            if (compruebaCampos()) {
                try {
                    int id = this.juego.getId();
                    crearJuego();
                    if (BaseDatosController.updateJuego(BaseDatosController.getConnection(), this.juego, id)) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Juego modificado");
                        alert.setHeaderText("Juego Modificado con exito");
                        alert.showAndWait();
                        this.juegos.removeIf(n -> (n.getId() == this.juego.getId()));
                        this.juegos.add(BaseDatosController.getJuego(BaseDatosController.getConnection(), this.juego.getId()));
                        rellenaListaJuegos();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Juego Modificado");
                        alert.setHeaderText("Error el juego no se ha Modificado");
                        alert.showAndWait();
                    }
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
                limpiar();
            }
        } else {
            if (compruebaCampos()) {
                try {
                    crearJuego();
                    if (!BaseDatosController.crearNuevoJuego(BaseDatosController.getConnection(), this.juego)) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Juego Creado");
                        alert.setHeaderText("Juego Creado con exito");
                        alert.showAndWait();
                        this.juegos.add(BaseDatosController.getJuego(BaseDatosController.getConnection(), this.juego.getId()));
                        rellenaListaJuegos();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Juego Creado");
                        alert.setHeaderText("Error el juego no se ha Creado");
                        alert.showAndWait();
                    }
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }

            }
        }
        limpiar();
    }

    /**
     * Función que comprueba que el campo id sea un número y no letras
     *
     * @return->true si la conversión a string se ha realizado con exito
     *        ->false en caso contrario
     */
    private boolean compruebaCampos() {
        if (!contenidoLista.contains(txtNombre)) {
            try {
                int numero = Integer.parseInt(txtJugadores.getText());
                return true;
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error en el campo Jugadores");
                alert.setHeaderText("Debes introducir un numero entre 1-10");
                alert.showAndWait();
            }

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error en campo Nombre");
            alert.setHeaderText("El juego ya existe en la base de datos");
            alert.showAndWait();
        }

        return false;
    }
}
