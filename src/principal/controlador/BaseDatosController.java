package principal.controlador;

import principal.modelo.Alquiler;
import principal.modelo.Alumno;
import principal.modelo.Juego;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.TimeZone;

public class BaseDatosController {
    private static final String URL ="jdbc:mysql://localhost/ludoteca_progresa?serverTimezone=";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL + TimeZone.getDefault().getID(),
                "root", "");
    }

    public static ArrayList<Juego> getArrayJuegos(Connection connection) throws SQLException {
        ArrayList<Juego> juegos = new ArrayList<>();
        PreparedStatement preparedStatement = connection.prepareStatement("select * from juego");
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            Juego juego = new Juego();
            juego.setId(resultSet.getInt(1));
            juego.setNombre(resultSet.getString(2));
            juego.setNum_Jugadores(resultSet.getInt(3));
            juego.setTematica(resultSet.getString(4));
            juego.setTipo(resultSet.getString(5));
            juego.setDisponibilidad(resultSet.getBoolean(6));

            juegos.add(juego);
        }
        return juegos;
    }
    public static ArrayList<Juego> getArrayJuegosDisponibles(Connection connection) throws SQLException {
        ArrayList<Juego> juegos = new ArrayList<>();
        PreparedStatement preparedStatement = connection.prepareStatement("select * from juego where disponibilidad = true");
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            Juego juego = new Juego();
            juego.setId(resultSet.getInt(1));
            juego.setNombre(resultSet.getString(2));
            juego.setNum_Jugadores(resultSet.getInt(3));
            juego.setTematica(resultSet.getString(4));
            juego.setTipo(resultSet.getString(5));
            juego.setDisponibilidad(resultSet.getBoolean(6));

            juegos.add(juego);
        }
        return juegos;
    }
    public static Juego getJuego(Connection connection, int nombreJuego) throws SQLException {
        Juego juego = new Juego();
        PreparedStatement preparedStatement = connection.prepareStatement("select * from juego where id_Juego=?");
        preparedStatement.setInt(1, nombreJuego);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()){
            juego.setId(resultSet.getInt(1));
            juego.setNombre(resultSet.getString(2));
            juego.setNum_Jugadores(resultSet.getInt(3));
            juego.setTematica(resultSet.getString(4));
            juego.setTipo(resultSet.getString(5));
            juego.setDisponibilidad(resultSet.getBoolean(6));
        }
        return juego;
    }
    public static boolean deleteJuego(Connection connection, int idJuego) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("delete from juego where id_Juego = ?");
        preparedStatement.setInt(1, idJuego);
        int affectedRows = preparedStatement.executeUpdate();
        return affectedRows > 0;
    }
    public static boolean crearNuevoJuego(Connection connection, Juego juego) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("{call crear_Juego(?,?,?,?)}");
        callableStatement.setString(1, juego.getNombre());
        callableStatement.setInt(2,juego.getNum_Jugadores());
        callableStatement.setString(3,juego.getTematica());
        callableStatement.setString(4, juego.getTipo());
        return callableStatement.execute();

    }
    public static boolean updateJuego(Connection connection, Juego juegoUp, int id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("update juego set nombre = ?, num_Jugadores = ?, tematica = ?, tipo = ? where id_Juego = ?");

        preparedStatement.setString(1, juegoUp.getNombre());
        preparedStatement.setInt(2,juegoUp.getNum_Jugadores());
        preparedStatement.setString(3, juegoUp.getTematica());
        preparedStatement.setString(4, juegoUp.getTipo());
        preparedStatement.setInt(5, id);
        int affectedRows = preparedStatement.executeUpdate();
        return affectedRows>0;
    }
    public static ArrayList<Alumno> getArrayAlumnos(Connection connection) throws SQLException {
        ArrayList<Alumno> alumnos = new ArrayList<>();
        PreparedStatement preparedStatement = connection.prepareStatement("select * from alumno");
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            Alumno alumno = new Alumno();
            alumno.setDni(resultSet.getString(1));
            alumno.setNombre(resultSet.getString(2));
            alumno.setApellido(resultSet.getString(3));
            alumno.setEmail(resultSet.getString(4));
            alumno.setTelefono(resultSet.getString(5));
            alumno.setSancion(resultSet.getBoolean(6));
            alumno.setFechaFinSancion(resultSet.getDate(7).toLocalDate());



            alumnos.add(alumno);
        }
        for (Alumno a:alumnos
             ) {
            a.setJuegos(getJuegosAlquiladosAlumno(connection, a.getDni()));
            levantaSancion(a);
            ponerSancion(a);
        }
        return alumnos;
    }

    /**
     * Actualizará la sanción del alumno si alguno de los juegos alquilados
     * tiene demora en la fecha de entrega
     * @param alumno
     */
    private static void ponerSancion(Alumno alumno) {
        for (Juego j: alumno.getJuegos()
             ) {
            if (j.getFechaEntrega().isBefore(LocalDate.now())){
                alumno.setSancion(true);
                try {
                    BaseDatosController.updateSancion(BaseDatosController.getConnection(),alumno.getDni(),alumno.isSancion());
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    /**
     * Actualizará la sanción del alumno si la fecha de fin de sanción
     * a expirado
     * @param alumno
     */
    private static void levantaSancion(Alumno alumno) {
        if(alumno.getFechaFinSancion().isBefore(LocalDate.now())){
            alumno.setSancion(false);
            try {
                BaseDatosController.updateSancion(BaseDatosController.getConnection(),alumno.getDni(),alumno.isSancion());
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }
    public static Boolean updateSancion(Connection connection, String dni, boolean sancion) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("update alumno set sancion = ? where dni = ?;");
        preparedStatement.setBoolean(1, sancion);
        preparedStatement.setString(2, dni);
        return preparedStatement.executeUpdate()>0;
    }

    /**
     * Devolverá la información de los juegos en la tabla alquiler que no estén entregados
     * @param connection
     * @param idAlumno
     * @return -> Array de juegos por entregar del alumno
     * @throws SQLException
     */
    public static ArrayList<Juego> getJuegosAlquiladosAlumno(Connection connection, String idAlumno) throws SQLException {

        ArrayList<Juego> juegos = new ArrayList<>();
        PreparedStatement preparedStatement = connection.prepareStatement("select j.*, a.fecha_Entrega from alquiler a join juego j on j.id_Juego = a.id_Juego and a.id_Alumno=? and a.entregado=false");
        preparedStatement.setString(1, idAlumno);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            Juego juego = new Juego();
            juego.setId(resultSet.getInt(1));
            juego.setNombre(resultSet.getString(2));
            juego.setNum_Jugadores(resultSet.getInt(3));
            juego.setTematica(resultSet.getString(4));
            juego.setTipo(resultSet.getString(5));
            juego.setDisponibilidad(resultSet.getBoolean(6));
            juego.setFechaEntrega(resultSet.getDate(7).toLocalDate());
            juegos.add(juego);

        }
        resultSet.close();
        preparedStatement.close();
        return juegos;
    }
    public static Boolean crearNuevoAlumno(Connection connection, Alumno alumno) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("{call crear_Alumno(?,?,?,?,?)}");
        callableStatement.setString(1, alumno.getDni());
        callableStatement.setString(2,alumno.getNombre());
        callableStatement.setString(3,alumno.getApellido());
        callableStatement.setString(4, alumno.getEmail());
        callableStatement.setString(5, alumno.getTelefono());
        return callableStatement.execute();

    }
    public static Alumno getAlumno(Connection connection, String dni) throws SQLException {
        Alumno alumno = new Alumno();
        PreparedStatement preparedStatement = connection.prepareStatement("select * from alumno where dni = ?");
        preparedStatement.setString(1, dni);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()){
            alumno.setDni(resultSet.getString(1));
            alumno.setNombre(resultSet.getString(2));
            alumno.setApellido(resultSet.getString(3));
            alumno.setEmail(resultSet.getString(4));
            alumno.setTelefono(resultSet.getString(5));
            alumno.setSancion(resultSet.getBoolean(6));
            alumno.setFechaFinSancion(resultSet.getDate(7).toLocalDate());
        }
        return alumno;
    }
    public static boolean updateAlumno(Connection connection, Alumno alumno, String dniAlumnoUp) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("update alumno set dni = ?, nombre = ?, apellido = ?, email = ?, telefono = ? where dni = ?");
        preparedStatement.setString(1, alumno.getDni());
        preparedStatement.setString(2, alumno.getNombre());
        preparedStatement.setString(3, alumno.getApellido());
        preparedStatement.setString(4, alumno.getEmail());
        preparedStatement.setString(5, alumno.getTelefono());
        preparedStatement.setString(6, dniAlumnoUp);
        int affectedRows = preparedStatement.executeUpdate();
        return affectedRows > 0;

    }
    public static Boolean deleteAlumno(Connection connection , String dniAlumno) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("delete from alumno where dni = ?");
        preparedStatement.setString(1, dniAlumno);
        int affectedRows = preparedStatement.executeUpdate();
        return affectedRows > 0;
    }
    public static Boolean realizaAlquiler(Connection connection, Alumno alumno, Juego juego) throws SQLException {
        int error = 0;
        CallableStatement callableStatement = connection.prepareCall("{call crear_Alquiler(?,?,?)}");
        callableStatement.setString(1, alumno.getDni());
        callableStatement.setInt(2, juego.getId());
        callableStatement.registerOutParameter(3, error);
        callableStatement.execute();
        return error==0;
    }
    public static Boolean ampliarFechaEntrega(Connection connection, Alumno alumno, Juego juego) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("{call actualiza_Entrega(?,?)}");
        callableStatement.setString(1,alumno.getDni());
        callableStatement.setInt(2, juego.getId());
        return callableStatement.execute();
    }
    public static Boolean devolverJuego(Connection connection, Alumno alumno, Juego juego) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("{call devuelve_Juego(?, ?)}");
        callableStatement.setString(1, alumno.getDni());
        callableStatement.setInt(2, juego.getId());
        return callableStatement.execute();
    }
    public static Boolean updateFechaSancion(Connection connection, Alumno alumno) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("update alumno set fecha_Fin_Sancion = ? where dni = ?");
        preparedStatement.setDate(1, Date.valueOf(alumno.getFechaFinSancion()));
        preparedStatement.setString(2, alumno.getDni());
        return preparedStatement.executeUpdate()>0;
    }

    public static ArrayList<Alquiler> getArrayAlquileres(Connection connection) throws SQLException {
        ArrayList<Alquiler> alquilers = new ArrayList<>();
        PreparedStatement preparedStatement = connection.prepareStatement("select * from alquiler");
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            Alquiler alquiler = new Alquiler();
            alquiler.setIdAlquiler(resultSet.getInt(1));
            alquiler.setIdAlumno(resultSet.getString(2));
            alquiler.setIdJuego(resultSet.getInt(3));
            alquiler.setFechaEntrega(resultSet.getDate(4).toLocalDate());
            alquiler.setEntregado(resultSet.getBoolean(5));
            alquilers.add(alquiler);
        }
        return alquilers;
    }

    public static ArrayList<Alquiler> getArrayAlquileresJuego(Connection connection, int idJuego) throws SQLException {
        ArrayList<Alquiler> alquilers = new ArrayList<>();
        PreparedStatement preparedStatement = connection.prepareStatement("select * from alquiler where id_Juego = ?");
        preparedStatement.setInt(1, idJuego);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            Alquiler alquiler = new Alquiler();
            alquiler.setIdAlquiler(resultSet.getInt(1));
            alquiler.setIdAlumno(resultSet.getString(2));
            alquiler.setIdJuego(resultSet.getInt(3));
            alquiler.setFechaEntrega(resultSet.getDate(4).toLocalDate());
            alquiler.setEntregado(resultSet.getBoolean(5));
            alquilers.add(alquiler);
        }
        return alquilers;
    }
    public static Alquiler getOneAlquilerJuego(Connection connection, int idJuego, String dniAlumno) throws SQLException {
        Alquiler alquiler = new Alquiler();
        PreparedStatement preparedStatement = connection.prepareStatement("select * from alquiler where id_Juego = ? and id_Alumno = ? and entregado = false");
        preparedStatement.setInt(1, idJuego);
        preparedStatement.setString(2, dniAlumno);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()){
            alquiler.setIdAlquiler(resultSet.getInt(1));
            alquiler.setIdAlumno(resultSet.getString(2));
            alquiler.setIdJuego(resultSet.getInt(3));
            alquiler.setFechaEntrega(resultSet.getDate(4).toLocalDate());
            alquiler.setEntregado(resultSet.getBoolean(5));

        }
        return alquiler;
    }
}
