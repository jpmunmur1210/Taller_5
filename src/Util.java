import java.lang.reflect.Array;

/**
 * Utilidades generales: redimensionar arreglos y cronometrar ejecuciones.
 */
public class Util {

    private static long inicioNanos;
    private static long duracionMs;

    /** Crea un arreglo nuevo (del mismo tipo) con el tamano indicado, copiando lo que quepa. */
    public static Object redimensionar(Object arreglo, int nuevoTamano) {
        int tamanoActual = Array.getLength(arreglo);
        Class<?> tipo = arreglo.getClass().getComponentType();
        Object nuevo = Array.newInstance(tipo, nuevoTamano);
        System.arraycopy(arreglo, 0, nuevo, 0, Math.min(tamanoActual, nuevoTamano));
        return nuevo;
    }

    public static void iniciarCronometro() {
        inicioNanos = System.nanoTime();
    }

    /** Detiene el cronometro y devuelve el tiempo con formato HH:MM:SS.mmm */
    public static String obtenerTextoTiempoCronometro() {
        duracionMs = (System.nanoTime() - inicioNanos) / 1_000_000L;
        return formatear(duracionMs);
    }

    /** Duracion (en milisegundos) de la ultima medicion realizada. */
    public static long obtenerMilisegundos() {
        return duracionMs;
    }

    public static String formatear(long ms) {
        long h = ms / 3_600_000L;
        long m = (ms / 60_000L) % 60;
        long s = (ms / 1000L) % 60;
        long milis = ms % 1000L;
        return String.format("%02d:%02d:%02d.%03d", h, m, s, milis);
    }
}
