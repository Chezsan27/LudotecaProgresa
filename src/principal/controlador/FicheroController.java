package principal.controlador;

import principal.modelo.Alquiler;

import java.io.*;

public class FicheroController {
    private static final File copiaAlquiler;
    private static BufferedReader lectorBuffer = null;
    public static FileReader lector = null;
    public static FileWriter escritor = null;
    private static PrintWriter escritorBuffer = null;

    static {
        copiaAlquiler = new File("alquileres.txt");
    }

    public static void agregarAlquiler(String alquiler) throws IOException {
        escritor = new FileWriter(copiaAlquiler, true);
        escritorBuffer = new PrintWriter(escritor);
        escritorBuffer.println(alquiler);
        if (escritor != null) {
            escritor.close();
        }

    }

    /**
     * Leerá la información del fichero "alquileres.txt" y reescribirá la información
     * en un fichero auxiliar si no coincide con el nombre del juego pasado como parametro
     * una vez terminada la leectura borrará el fichero existente y renombrará el fichero
     * auxiliar con el nombre del primero
     * @param nombreJuego-> nombre del juego para eliminar la linea del alquiler devuelto
     * @throws IOException
     */
    public static void eliminarAlquiler(String nombreJuego) throws IOException {
        lector = new FileReader(copiaAlquiler);
        lectorBuffer = new BufferedReader(lector);
        File auxiliarFile = new File("auxiliar.txt");
        String lineaAlquiler;
        while ((lineaAlquiler = lectorBuffer.readLine()) != null) {
            String[] trozos = lineaAlquiler.split(";");
            if (!trozos[2].equalsIgnoreCase(nombreJuego)) {
                escritor = new FileWriter(auxiliarFile, true);
                escritorBuffer = new PrintWriter(escritor);
                escritorBuffer.println(lineaAlquiler);
            } else {
                System.out.println(lineaAlquiler);

            }
        }

        if (lector != null) {
            lector.close();
        }
        if (escritor != null) {
            escritor.close();
        }

        copiaAlquiler.delete();
        auxiliarFile.renameTo(copiaAlquiler);
    }
}
