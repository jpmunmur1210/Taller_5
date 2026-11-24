import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

/**
 * Representa un documento (carta) solicitado por un cliente del banco.
 *
 * Propiedades:
 *  - nombre:        nombre del cliente
 *  - apellido1:     primer apellido del cliente
 *  - apellido2:     segundo apellido del cliente
 *  - tipoDocumento: tipo de documento suministrado
 *
 * La clase tambien contiene (como atributos y metodos estaticos) la lista de
 * documentos cargados y los algoritmos de ordenamiento:
 *  Burbuja, Rapido (Quicksort), Insercion, Seleccion y Mezcla (Mergesort).
 */
public class Documento {

    /** Criterio 0: primero Nombre completo y luego Tipo de documento. */
    public static final int NOMBRE_DOCUMENTO = 0;
    /** Criterio 1: primero Tipo de documento y luego Nombre completo. */
    public static final int DOCUMENTO_NOMBRE = 1;

    private String nombre;
    private String apellido1;
    private String apellido2;
    private String tipoDocumento;
    private String nombreCompleto; // se calcula una sola vez (evita concatenar en cada comparacion)

    public Documento() {
        this("", "", "", "");
    }

    public Documento(String apellido1, String apellido2, String nombre, String tipoDocumento) {
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.tipoDocumento = tipoDocumento;
        this.nombreCompleto = construirNombreCompleto();
    }

    private String construirNombreCompleto() {
        StringBuilder sb = new StringBuilder();
        for (String parte : new String[]{apellido1, apellido2, nombre}) {
            if (parte != null && !parte.trim().isEmpty()) {
                if (sb.length() > 0) {
                    sb.append(' ');
                }
                sb.append(parte.trim());
            }
        }
        return sb.toString();
    }

    /** Apellido1 + Apellido2 + Nombre concatenados. */
    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    //********** Atributos y Metodos estaticos **********
    /** Almacena la lista de documentos con la que se trabaja (la que se ordena). */
    public static Documento[] documentos;

    /** Copia de la lista en el orden original del archivo (para repetir pruebas justas). */
    private static Documento[] original;

    /** Agrega un documento a la lista (redimensionando el arreglo). */
    public static void agregar(Documento d) {
        if (documentos == null) {
            documentos = new Documento[1];
        } else {
            documentos = (Documento[]) Util.redimensionar(documentos, documentos.length + 1);
        }
        documentos[documentos.length - 1] = d;
    }

    /** Llena la lista con TODOS los datos provenientes del archivo plano. */
    public static void obtenerLista(String nombreArchivo) {
        obtenerLista(nombreArchivo, Integer.MAX_VALUE);
    }

    /**
     * Llena la lista con los datos del archivo plano (maximo 'limite' registros).
     * Se acumulan en una lista dinamica y se pasan al arreglo una sola vez, porque
     * llamar agregar() 100.000 veces copiaria el arreglo 100.000 veces.
     */
    public static void obtenerLista(String nombreArchivo, int limite) {
        List<Documento> lista = new ArrayList<>();
        try (BufferedReader br = Archivo.abrirArchivo(nombreArchivo)) {
            String linea = br.readLine(); // encabezado (se descarta)
            linea = br.readLine();
            while (linea != null && lista.size() < limite) {
                String[] textos = linea.split(";");
                if (textos.length >= 4) {
                    lista.add(new Documento(textos[0], textos[1], textos[2], textos[3]));
                }
                linea = br.readLine();
            }
        } catch (Exception ex) {
            System.err.println("No se pudo leer el archivo: " + ex.getMessage());
        }
        documentos = lista.toArray(new Documento[0]);
        original = documentos.clone();
    }

    /** Devuelve la lista al orden en que fue leida del archivo. */
    public static void restaurarOriginal() {
        if (original != null) {
            documentos = original.clone();
        }
    }

    /** Despliega en un JTable la lista de documentos cargados. */
    public static void mostrarLista(JTable tbl) {
        final String[] encabezados = {"Nombre", "Tipo Documento"};
        final Documento[] lista = documentos == null ? new Documento[0] : documentos;
        tbl.setModel(new AbstractTableModel() {
            @Override
            public int getRowCount() {
                return lista.length;
            }

            @Override
            public int getColumnCount() {
                return encabezados.length;
            }

            @Override
            public String getColumnName(int c) {
                return encabezados[c];
            }

            @Override
            public Object getValueAt(int fila, int col) {
                return col == 0 ? lista[fila].getNombreCompleto() : lista[fila].getTipoDocumento();
            }
        });
    }

    /** Intercambia los documentos de dos posiciones. */
    private static void intercambiar(int origen, int destino) {
        Documento t = documentos[origen];
        documentos[origen] = documentos[destino];
        documentos[destino] = t;
    }

    /** Compara dos documentos segun el criterio: negativo, 0 o positivo (como compareTo). */
    private static int comparar(Documento dI, Documento dJ, int criterio) {
        int c;
        if (criterio == NOMBRE_DOCUMENTO) {
            c = dI.getNombreCompleto().compareTo(dJ.getNombreCompleto());
            if (c == 0) {
                c = dI.getTipoDocumento().compareTo(dJ.getTipoDocumento());
            }
        } else {
            c = dI.getTipoDocumento().compareTo(dJ.getTipoDocumento());
            if (c == 0) {
                c = dI.getNombreCompleto().compareTo(dJ.getNombreCompleto());
            }
        }
        return c;
    }

    /** Verifica si un documento es mayor que otro segun el criterio de ordenacion. */
    private static boolean esMayor(Documento dI, Documento dJ, int criterio) {
        return comparar(dI, dJ, criterio) > 0;
    }

    /** Verifica que la lista actual este ordenada segun el criterio (util para pruebas). */
    public static boolean estaOrdenada(int criterio) {
        for (int i = 0; i < documentos.length - 1; i++) {
            if (esMayor(documentos[i], documentos[i + 1], criterio)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Ejecuta un algoritmo en un hilo con una pila grande. Asi la recursion del
     * ordenamiento rapido no desborda la pila aunque la lista ya venga ordenada.
     */
    private static void ejecutarConPilaGrande(Runnable tarea) {
        Thread t = new Thread(null, tarea, "ordenamiento", 512L * 1024 * 1024);
        t.start();
        try {
            t.join();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    //******************** 1. Ordenamiento BURBUJA (no recursivo)
    public static String ordenarBurbuja(int criterio) {
        Util.iniciarCronometro();
        for (int i = 0; i < documentos.length - 1; i++) {
            for (int j = i + 1; j < documentos.length; j++) {
                if (esMayor(documentos[i], documentos[j], criterio)) {
                    intercambiar(i, j);
                }
            }
        }
        return Util.obtenerTextoTiempoCronometro();
    }

    //******************** 2. Ordenamiento RAPIDO - Quicksort (recursivo)
    public static String ordenarRapido(int criterio) {
        Util.iniciarCronometro();
        ejecutarConPilaGrande(() -> _ordenarRapido(0, documentos.length - 1, criterio));
        return Util.obtenerTextoTiempoCronometro();
    }

    // Metodo recursivo: inicio y fin indican sobre que parte de la lista opera
    private static void _ordenarRapido(int inicio, int fin, int criterio) {
        // Punto de finalizacion (caso base)
        if (inicio >= fin) {
            return;
        }
        // Caso recursivo
        int posicionPivote = ubicarPivote(inicio, fin, criterio);
        _ordenarRapido(inicio, posicionPivote - 1, criterio); // ordenar los menores
        _ordenarRapido(posicionPivote + 1, fin, criterio);    // ordenar los mayores
    }

    // Ubica el pivote en su posicion final: antes los menores y despues los mayores
    private static int ubicarPivote(int inicio, int fin, int criterio) {
        // Se toma el elemento central como pivote (se lleva a 'inicio'): evita el peor
        // caso cuando la lista ya esta ordenada.
        int medio = inicio + (fin - inicio) / 2;
        if (medio != inicio) {
            intercambiar(inicio, medio);
        }
        Documento dPivote = documentos[inicio];
        int posicionPivote = inicio;
        for (int i = inicio + 1; i < fin + 1; i++) {
            if (esMayor(dPivote, documentos[i], criterio)) {
                posicionPivote++;
                if (i != posicionPivote) {
                    intercambiar(i, posicionPivote);
                }
            }
        }
        // Pone el pivote al final de los menores
        if (inicio != posicionPivote) {
            intercambiar(inicio, posicionPivote);
        }
        return posicionPivote;
    }

    //******************** 3. Ordenamiento por INSERCION (no recursivo)
    public static String ordenarInsercion(int criterio) {
        Util.iniciarCronometro();
        for (int i = 1; i < documentos.length; i++) {
            Documento actual = documentos[i];
            int j = i - 1;
            // Corre a la derecha los elementos mayores que 'actual'
            while (j >= 0 && esMayor(documentos[j], actual, criterio)) {
                documentos[j + 1] = documentos[j];
                j--;
            }
            documentos[j + 1] = actual; // lo inserta en su posicion
        }
        return Util.obtenerTextoTiempoCronometro();
    }

    //******************** 4. Ordenamiento por SELECCION (no recursivo)
    public static String ordenarSeleccion(int criterio) {
        Util.iniciarCronometro();
        for (int i = 0; i < documentos.length - 1; i++) {
            int menor = i;
            for (int j = i + 1; j < documentos.length; j++) {
                if (esMayor(documentos[menor], documentos[j], criterio)) {
                    menor = j;
                }
            }
            if (menor != i) {
                intercambiar(i, menor);
            }
        }
        return Util.obtenerTextoTiempoCronometro();
    }

    //******************** 5. Ordenamiento por MEZCLA - Mergesort (recursivo)
    private static Documento[] auxiliar;

    public static String ordenarMezcla(int criterio) {
        Util.iniciarCronometro();
        auxiliar = new Documento[documentos.length];
        _ordenarMezcla(0, documentos.length - 1, criterio);
        auxiliar = null;
        return Util.obtenerTextoTiempoCronometro();
    }

    // Divide la lista en dos mitades, ordena cada una (recursivamente) y las mezcla
    private static void _ordenarMezcla(int inicio, int fin, int criterio) {
        if (inicio >= fin) { // caso base: 0 o 1 elemento
            return;
        }
        int medio = inicio + (fin - inicio) / 2;
        _ordenarMezcla(inicio, medio, criterio);
        _ordenarMezcla(medio + 1, fin, criterio);
        mezclar(inicio, medio, fin, criterio);
    }

    // Mezcla dos mitades ya ordenadas [inicio..medio] y [medio+1..fin]
    private static void mezclar(int inicio, int medio, int fin, int criterio) {
        System.arraycopy(documentos, inicio, auxiliar, inicio, fin - inicio + 1);
        int i = inicio;
        int j = medio + 1;
        int k = inicio;
        while (i <= medio && j <= fin) {
            if (esMayor(auxiliar[i], auxiliar[j], criterio)) {
                documentos[k++] = auxiliar[j++];
            } else {
                documentos[k++] = auxiliar[i++];
            }
        }
        while (i <= medio) {
            documentos[k++] = auxiliar[i++];
        }
        while (j <= fin) {
            documentos[k++] = auxiliar[j++];
        }
    }
}
