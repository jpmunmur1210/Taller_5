import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Utilidades para el manejo de archivos planos.
 */
public class Archivo {

    /** Abre un archivo de texto en UTF-8 (el CSV de datos viene en esa codificacion). */
    public static BufferedReader abrirArchivo(String nombreArchivo) throws Exception {
        return new BufferedReader(new InputStreamReader(
                new FileInputStream(nombreArchivo), StandardCharsets.UTF_8), 1 << 16);
    }

    /** Busca Datos.csv en las ubicaciones habituales del proyecto. Devuelve null si no lo encuentra. */
    public static String buscarDatos() {
        String base = System.getProperty("user.dir");
        String[] candidatos = {
            base + "/src/Datos/Datos.csv",
            base + "/Datos/Datos.csv",
            base + "/Datos.csv",
            base + "/../src/Datos/Datos.csv"
        };
        for (String c : candidatos) {
            File f = new File(c);
            if (f.isFile()) {
                return f.getPath();
            }
        }
        return null;
    }
}
