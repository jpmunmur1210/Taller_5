/**
 * Prueba por consola: ejecuta los 5 algoritmos con ambos criterios, verifica que la
 * lista quede ordenada y muestra los tiempos.
 * Uso: java -cp out Benchmark [archivo] [maxRegistros]
 */
public class Benchmark {

    public static void main(String[] args) {
        String archivo = args.length > 0 ? args[0] : Archivo.buscarDatos();
        int max = args.length > 1 ? Integer.parseInt(args[1]) : Integer.MAX_VALUE;
        Documento.obtenerLista(archivo, max);
        int n = Documento.documentos.length;
        System.out.println("Registros: " + n);

        String[] nombres = {"Burbuja", "Rapido", "Insercion", "Seleccion", "Mezcla"};
        for (int criterio = 0; criterio < 2; criterio++) {
            System.out.println("\nCriterio " + (criterio == 0 ? "Nombre - Documento" : "Documento - Nombre"));
            for (int a = 0; a < nombres.length; a++) {
                Documento.restaurarOriginal();
                String t;
                switch (a) {
                    case 0: t = Documento.ordenarBurbuja(criterio); break;
                    case 1: t = Documento.ordenarRapido(criterio); break;
                    case 2: t = Documento.ordenarInsercion(criterio); break;
                    case 3: t = Documento.ordenarSeleccion(criterio); break;
                    default: t = Documento.ordenarMezcla(criterio);
                }
                System.out.printf("  %-10s %s  ordenada=%s%n", nombres[a], t, Documento.estaOrdenada(criterio));
            }
        }
    }
}
