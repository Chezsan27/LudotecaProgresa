package principal.controlador;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import principal.modelo.Alquiler;
import principal.modelo.Juego;

import java.sql.SQLException;
import java.util.ArrayList;

public class ConsultaController {

    @FXML
    private TableView<Alquiler> tvAlquiler;
    @FXML
    private TableColumn colIdAlquiler;
    @FXML
    private TableColumn colDNI;
    @FXML
    private TableColumn colJuego;
    @FXML
    private TableColumn colFechaEntrega;
    @FXML
    private TableColumn colEntregado;
    @FXML
    private Button btnCargar;
    @FXML
    private ComboBox cbJuegos;

    private ObservableList<String> contenidoListaJuego = FXCollections.observableArrayList();
    private ObservableList<Alquiler> contenidoListaAlquiler = FXCollections.observableArrayList();
    private ArrayList<Juego> juegos;
    private ArrayList<Alquiler> alquileres;
    private int idJuegoSeleccionado;

    public void inicializaDatos() {
        try {
            this.juegos = BaseDatosController.getArrayJuegos(BaseDatosController.getConnection());
            this.alquileres = BaseDatosController.getArrayAlquileres(BaseDatosController.getConnection());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        setTablaJuegos();
        rellenaListaJuegos();
        refreshTablaJuegos();
    }

    /**
     * Establece los parámetros de la clase que mostrará la tabla
     */
    private void setTablaJuegos() {
        this.colIdAlquiler.setCellValueFactory(new PropertyValueFactory<>("idAlquiler"));
        this.colDNI.setCellValueFactory(new PropertyValueFactory<>("idAlumno"));
        this.colJuego.setCellValueFactory(new PropertyValueFactory<>("idJuego"));
        this.colFechaEntrega.setCellValueFactory(new PropertyValueFactory<>("fechaEntrega"));
        this.colEntregado.setCellValueFactory(new PropertyValueFactory<>("entregado"));
    }

    private void rellenaListaJuegos() {
        contenidoListaJuego.clear();
        for (Juego j : this.juegos
        ) {
            contenidoListaJuego.add(j.getNombre() + "");
        }
        cbJuegos.setItems(contenidoListaJuego);
    }

    public void refreshTablaJuegos() {
        this.contenidoListaAlquiler.clear();
        this.contenidoListaAlquiler.addAll(this.alquileres);
        this.tvAlquiler.setItems(contenidoListaAlquiler);
    }

    public void cargarAlquiler(MouseEvent mouseEvent) {
        try {
            if (cbJuegos.getValue() != null) {
                this.idJuegoSeleccionado = getIdJuego();
                this.alquileres = BaseDatosController.getArrayAlquileresJuego(BaseDatosController.getConnection(), this.idJuegoSeleccionado);
                refreshTablaJuegos();
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private int getIdJuego() {
        int id = 0;
        for (Juego j : juegos
        ) {
            if (j.getNombre().equalsIgnoreCase(cbJuegos.getValue() + "")) {
                return j.getId();
            }
        }
        return id;
    }

    public void vaciarConsulta(MouseEvent mouseEvent) {

        try {
            this.alquileres = BaseDatosController.getArrayAlquileres(BaseDatosController.getConnection());
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        refreshTablaJuegos();

    }
}
