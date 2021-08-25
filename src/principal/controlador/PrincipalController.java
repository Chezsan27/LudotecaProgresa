package principal.controlador;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import principal.modelo.Alumno;
import principal.modelo.Juego;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class PrincipalController implements Initializable {
    //Paneles con información
    @FXML
    private AnchorPane panelAlumno;
    //Imagenes con función
    @FXML
    private ImageView imgAlumno;
    @FXML
    private ImageView imgJuego;
    @FXML
    private ImageView imgAlquiler;
    @FXML
    private ImageView imgSalir;

    //Panel Alumno
    @FXML
    private ComboBox cbAlumnos;
    @FXML
    private TextField txtDni;
    @FXML
    private TextField txtApellido;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtSancion;
    @FXML
    private TextField txtFechaFinSancion;
    @FXML
    private TextField txtTelefono;
    @FXML
    private Label lbSancionActiva;
    @FXML
    private Label lbFechaFinSancion;
    @FXML
    private TableView<Juego> tvJuegos;
    @FXML
    private TableColumn colId;
    @FXML
    private TableColumn colNombre;
    @FXML
    private TableColumn colFechaEntrega;
    @FXML
    private Button btnDevolver;
    @FXML
    private Button btnAlquilar;
    @FXML
    private Button btnAmpliarAlquiler;


    private ObservableList<String> contenidoListaAlumnos = FXCollections.observableArrayList();
    private ObservableList<Juego> contenidoListaJuegosAlumno = FXCollections.observableArrayList();

    public ArrayList<Alumno> alumnos;
    private Alumno alumno;
    private Juego juegoSeleccionado;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargaDatosBD();
        rellenaListaAlumnos();
        isVisibleGrupoSancion(false);
        setTablaJuegos();
        isVisibleGroupJuegos(false);
        this.alumno = new Alumno();

    }

    private void cargaDatosBD() {
        try {
            this.alumnos = BaseDatosController.getArrayAlumnos(BaseDatosController.getConnection());
        } catch (SQLException exception) {
            exception.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No se ha cargado la info de Alumnos");
            alert.setHeaderText(null);
            alert.showAndWait();
        }
    }

    public void isVisibleGroupJuegos(boolean b) {
        this.tvJuegos.setVisible(b);
        this.btnAlquilar.setVisible(b);
        this.btnDevolver.setVisible(b);
        this.btnAmpliarAlquiler.setVisible(b);

    }

    private void setTablaJuegos() {
        this.colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        this.colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        this.colFechaEntrega.setCellValueFactory(new PropertyValueFactory<>("fechaEntrega"));
    }

    private void isVisibleGrupoSancion(boolean b) {
        txtSancion.setVisible(b);
        txtFechaFinSancion.setVisible(b);
        lbSancionActiva.setVisible(b);
        lbFechaFinSancion.setVisible(b);
    }

    public void nuevoAlumno(MouseEvent mouseEvent) {
        limpiar();
    }

    private void limpiar() {
        cbAlumnos.setValue(null);
        txtDni.clear();
        txtSancion.clear();
        txtNombre.clear();
        txtEmail.clear();
        txtApellido.clear();
        txtTelefono.clear();
        txtFechaFinSancion.clear();
        isVisibleGrupoSancion(false);
        isVisibleGroupJuegos(false);
        this.alumno = new Alumno();
    }

    private void rellenaListaAlumnos() {
        contenidoListaAlumnos.clear();
        for (Alumno a : this.alumnos
        ) {
            contenidoListaAlumnos.add(a.getDni());
            System.out.println(a.getNombre());
        }
        cbAlumnos.setItems(contenidoListaAlumnos);
    }

    public void onExitButtonClick() {
        Platform.exit();
        System.exit(0);
    }

    public void muestraPanelJuego(MouseEvent mouseEvent) {
        //Mostrará la vista del juego para su creación edicion o borrado
        FXMLLoader loader = new FXMLLoader();//Cargar vista
        loader.setLocation(getClass().getResource("../vista/juego.fxml"));

        Scene scene = null;
        try {
            scene = new Scene(loader.load());
            Stage ventana = new Stage();
            ventana.setTitle("Ventana Juegos");
            ventana.setScene(scene);
            ventana.setResizable(false);
            ventana.show();

            JuegoController ventanaJuego = loader.getController();
            ventanaJuego.inicializaDatos();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void mostrarConsultaJuegos(MouseEvent mouseEvent) {
        FXMLLoader loader = new FXMLLoader();//Cargar vista
        loader.setLocation(getClass().getResource("../vista/consulta.fxml"));

        Scene scene = null;
        try {
            scene = new Scene(loader.load());
            Stage ventana = new Stage();
            ventana.setTitle("Ventana Consulta");
            ventana.setScene(scene);
            ventana.setResizable(false);
            ventana.show();

            ConsultaController ventanaJuego = loader.getController();
            ventanaJuego.inicializaDatos();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void cargarAlumno(MouseEvent mouseEvent) {
        if (cbAlumnos.getValue() != null) {
            for (Alumno a : alumnos
            ) {
                if (a.getDni() == cbAlumnos.getValue()) {
                    this.alumno = a;
                    break;
                }
            }
            //Cargar la informacion del alumno en los text box
            txtDni.setText(alumno.getDni());
            txtApellido.setText(alumno.getApellido());
            txtNombre.setText(alumno.getNombre());
            txtEmail.setText(alumno.getEmail());
            txtTelefono.setText(alumno.getTelefono());
            System.out.println(alumno.isSancion());
            if (alumno.isSancion()) {
                isVisibleGrupoSancion(true);
                txtSancion.setText(alumno.isSancion() + "");
                txtFechaFinSancion.setText(alumno.getFechaFinSancion().isBefore(LocalDate.now()) ? "Fecha por determinar" : alumno.getFechaFinSancion() + "");
            } else {
                isVisibleGrupoSancion(false);
            }
            isVisibleGroupJuegos(true);
            refreshTablaJuegos();

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Cargar alumno");
            alert.setHeaderText("Error debes seleccionar un alumno de la base de datos");
            alert.showAndWait();
        }
    }

    public void refreshTablaJuegos() {
        this.contenidoListaJuegosAlumno.clear();
        this.contenidoListaJuegosAlumno.addAll(this.alumno.getJuegos());
        this.tvJuegos.setItems(contenidoListaJuegosAlumno);
    }

    public void guardarAlumno(MouseEvent mouseEvent) {
        if (cbAlumnos.getValue() != null) {
            //Actualiza
            try {
                String dniAlumno = this.alumno.getDni();
                crearAlumnoActualizado();
                if (BaseDatosController.updateAlumno(BaseDatosController.getConnection(), this.alumno, dniAlumno)) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Alumno modificado");
                    alert.setHeaderText("Alumno Modificado con exito");
                    alert.showAndWait();
                    limpiar();
                    this.alumnos.removeIf(n -> (n.getDni().equals(dniAlumno)));
                    this.alumnos.add(this.alumno);
                    rellenaListaAlumnos();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error actualizando alumno");
                    alert.setHeaderText("El alumno no se ha modificado en la base de datos");
                    alert.showAndWait();
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        } else {
            //Crear nuevo alumno
            crearNuevoAlumno();
            System.out.println(this.alumno);
            try {
                if (!contenidoListaAlumnos.contains(alumno.getDni()) && comprobarCamposAlumno()) {
                    if (!BaseDatosController.crearNuevoAlumno(BaseDatosController.getConnection(), alumno)) {
                        this.alumno = BaseDatosController.getAlumno(BaseDatosController.getConnection(), this.alumno.getDni());
                        alumnos.add(this.alumno);
                        for (Alumno a : alumnos
                        ) {
                            System.out.println(a.getNombre());
                        }
                        rellenaListaAlumnos();
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error en campo DNI");
                    alert.setHeaderText("El DNI del alumno ya existe en la base de datos");
                    alert.showAndWait();
                    txtDni.clear();
                }

            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }

    private void crearNuevoAlumno() {
        this.alumno = new Alumno();
        this.alumno.setDni(txtDni.getText());
        this.alumno.setNombre(txtNombre.getText());
        this.alumno.setTelefono(txtTelefono.getText());
        this.alumno.setApellido(txtApellido.getText());
        this.alumno.setEmail(txtEmail.getText());
    }

    private void crearAlumnoActualizado() {
        this.alumno.setDni(txtDni.getText());
        this.alumno.setNombre(txtNombre.getText());
        this.alumno.setTelefono(txtTelefono.getText());
        this.alumno.setApellido(txtApellido.getText());
        this.alumno.setEmail(txtEmail.getText());
    }

    private boolean comprobarCamposAlumno() {
        try {
            int dni = Integer.parseInt(this.alumno.getDni());
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error en campo DNI");
            alert.setHeaderText("El dni deben ser numeros");
            alert.showAndWait();
            txtDni.clear();
            return false;

        }
        try {
            int telefono = Integer.parseInt(this.alumno.getTelefono());
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error en campo DNI");
            alert.setHeaderText("El dni deben ser numeros");
            alert.showAndWait();
            txtTelefono.clear();
            return false;
        }
        return true;

    }

    public void eliminarAlumno(MouseEvent mouseEvent) {
        if (cbAlumnos.getValue() != null) {
            try {
                if (BaseDatosController.deleteAlumno(BaseDatosController.getConnection(), this.alumno.getDni())) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Alumno eliminado");
                    alert.setHeaderText("Alumno eliminado con exito");
                    alert.showAndWait();
                    alumnos.removeIf(n -> (n.getDni() == this.alumno.getDni()));
                    rellenaListaAlumnos();
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error en la conexion");
                alert.setHeaderText("Ocurrió un error en la conexion");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error al eliminar");
            alert.setHeaderText("Debes seleccionar un alumno de la base de datos para eliminar");
            alert.showAndWait();
        }
    }

    public void alquilarNuevoJuego(MouseEvent mouseEvent) {
        if (this.alumno.isSancion()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error al alquilar");
            alert.setHeaderText("El alumno tiene una sanción activa por demora");
            alert.showAndWait();
        } else {
            if (this.alumno.getCantidadJuegos() == 3) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error al alquilar");
                alert.setHeaderText("El alumno tiene 3 juegos alquilados");
                alert.showAndWait();
            } else {
                FXMLLoader loader = new FXMLLoader();//Cargar vista
                loader.setLocation(getClass().getResource("../vista/alquilerJuego.fxml"));

                Scene scene = null;
                try {
                    scene = new Scene(loader.load());
                    Stage ventana = new Stage();
                    ventana.setTitle("Ventana Juegos");
                    ventana.setScene(scene);
                    ventana.setResizable(false);
                    AlquilerJuegoController ventanaJuego = loader.getController();
                    ventanaJuego.inicializaDatos(this.alumno);
                    ventana.showAndWait();


                    refreshTablaJuegos();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void seleccionarJuego(MouseEvent mouseEvent) {
        this.juegoSeleccionado = this.tvJuegos.getSelectionModel().getSelectedItem();

    }

    public void ampliarFechaAlquiler(MouseEvent mouseEvent) {
        if (!this.alumno.isSancion()) {
            if (this.juegoSeleccionado != null) {
                try {
                    BaseDatosController.ampliarFechaEntrega(BaseDatosController.getConnection(), this.alumno, this.juegoSeleccionado);
                    this.alumno.setJuegos(BaseDatosController.getJuegosAlquiladosAlumno(BaseDatosController.getConnection(), this.alumno.getDni()));
                    refreshTablaJuegos();
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Ampliar fecha entrega");
                    alert.setHeaderText("La fecha de entrega se a ampliado con exito para " + this.alumno.getNombre() + " en el juego " + this.juegoSeleccionado.getNombre());
                    alert.showAndWait();
                    this.juegoSeleccionado = null;

                } catch (SQLException exception) {
                    exception.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("ERROR en la conexion");
                    alert.setHeaderText("No se ha actualizado la ampliación");
                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR al realizar ampliacion alquier");
                alert.setHeaderText("Debes seleccionar un juego para ampliar");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR al realizar ampliacion alquier");
            alert.setHeaderText("El alumno tiene una sanción activa y no puede ampliar su fecha de entrega");
            alert.showAndWait();
        }

    }

    /**
     * Actualiza la devolución del juego seleccionado en el alumno cargadodo de la base de datos
     * Borrará la información del alquiler en el fichero
     * @param mouseEvent
     */
    public void devolverJuego(MouseEvent mouseEvent) {
        if (this.juegoSeleccionado != null) {
            try {
                BaseDatosController.devolverJuego(BaseDatosController.getConnection(), this.alumno, juegoSeleccionado);
                FicheroController.eliminarAlquiler(juegoSeleccionado.getNombre());
            } catch (SQLException exception) {
                exception.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR en la conexion");
                alert.setHeaderText("No se ha actualizado la devolucion");
                alert.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    FicheroController.lector.close();
                    FicheroController.escritor.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
            compruebaFechaSancion();
            txtFechaFinSancion.setText(this.alumno.getFechaFinSancion() + "");
            this.alumno.getJuegos().removeIf(n -> (n.getId() == juegoSeleccionado.getId()));
            refreshTablaJuegos();

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR al realizar devolución");
            alert.setHeaderText("Debes seleccionar un juego para devolver");
            alert.showAndWait();
        }
    }

    /**
     * Si la fecha está por determinar y la sanción está activa calculará la nueva fecha de sanción
     * siendo el doble de la diferencia de la demora
     * Si existe fecha de sanción por demora añidira el calcula de la nueva demora a la fecha existente
     * ampliandola
     */
    private void compruebaFechaSancion() {
        if (this.alumno.isSancion()) {
            Period nuevaFecha = Period.between(juegoSeleccionado.getFechaEntrega(), LocalDate.now());

            if (txtFechaFinSancion.getText().equalsIgnoreCase("Fecha por determinar")) {
                this.alumno.setFechaFinSancion(LocalDate.now().plusDays(nuevaFecha.getDays() * 2L));
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Devolución");
                alert.setHeaderText("Se ha establecido una nueva fecha de sanción");
                alert.showAndWait();
            } else {
                this.alumno.setFechaFinSancion(this.alumno.getFechaFinSancion().plusDays(nuevaFecha.getDays() * 2L));
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Devolución");
                alert.setHeaderText("Se ha ampliado una fecha de sanción");
                alert.showAndWait();
            }

            try {
                BaseDatosController.updateFechaSancion(BaseDatosController.getConnection(), this.alumno);
            } catch (SQLException exception) {
                exception.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR en la conexion");
                alert.setHeaderText("No se ha actualizado la devolucion");
                alert.showAndWait();
            }
        }
    }

}
