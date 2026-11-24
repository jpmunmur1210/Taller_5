import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.util.function.IntFunction;
import javax.swing.*;

/**
 * Formulario principal: carga el archivo plano, permite elegir el criterio de
 * ordenamiento y el algoritmo, y muestra el tiempo que demora cada ejecucion.
 */
public class FrmOrdenamiento extends JFrame {

    private static final String[] CRITERIOS = {"Nombre - Documento", "Documento - Nombre"};
    private static final String[] REGISTROS = {"1.000", "5.000", "10.000", "25.000", "50.000", "Todos"};
    private static final int[] REGISTROS_VALOR = {1000, 5000, 10000, 25000, 50000, Integer.MAX_VALUE};

    private final JTable tblDatos = new JTable();
    private final JComboBox<String> cmbCriterio = new JComboBox<>(CRITERIOS);
    private final JComboBox<String> cmbRegistros = new JComboBox<>(REGISTROS);
    private final JTextField txtTiempo = new JTextField(14);
    private final JLabel lblEstado = new JLabel(" ");
    private final JButton btnOrdenar = new JButton("Burbuja");
    private final JButton btnOrdenarRapido = new JButton("Rapido");
    private final JButton btnInsercion = new JButton("Insercion");
    private final JButton btnSeleccion = new JButton("Seleccion");
    private final JButton btnMezcla = new JButton("Mezcla");
    private final JButton btnComparar = new JButton("Comparar todos");
    private final JButton btnRecargar = new JButton("Recargar");
    private String nombreArchivo;

    public FrmOrdenamiento() {
        super("Taller 5 - Ordenamiento de documentos");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        construirInterfaz();

        nombreArchivo = Archivo.buscarDatos();
        if (nombreArchivo == null) {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Seleccione el archivo Datos.csv");
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                nombreArchivo = fc.getSelectedFile().getPath();
            }
        }
        cmbRegistros.setSelectedIndex(REGISTROS.length - 1);
        cargarDatos();
    }

    private void construirInterfaz() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        barra.add(btnOrdenar);
        barra.add(btnOrdenarRapido);
        barra.add(btnInsercion);
        barra.add(btnSeleccion);
        barra.add(btnMezcla);
        barra.add(new JLabel(" Criterio:"));
        barra.add(cmbCriterio);
        barra.add(new JLabel(" Tiempo:"));
        txtTiempo.setEditable(false);
        txtTiempo.setHorizontalAlignment(JTextField.CENTER);
        barra.add(txtTiempo);

        JPanel barra2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        barra2.add(new JLabel("Registros a cargar:"));
        barra2.add(cmbRegistros);
        barra2.add(btnRecargar);
        barra2.add(btnComparar);

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(barra, BorderLayout.NORTH);
        norte.add(barra2, BorderLayout.SOUTH);

        tblDatos.setAutoCreateColumnsFromModel(true);
        add(norte, BorderLayout.NORTH);
        add(new JScrollPane(tblDatos), BorderLayout.CENTER);
        add(lblEstado, BorderLayout.SOUTH);

        btnOrdenar.setToolTipText("Ordenar con Burbuja (no recursivo)");
        btnOrdenarRapido.setToolTipText("Ordenar con Quicksort (recursivo)");
        btnInsercion.setToolTipText("Ordenar por Insercion (no recursivo)");
        btnSeleccion.setToolTipText("Ordenar por Seleccion (no recursivo)");
        btnMezcla.setToolTipText("Ordenar por Mezcla / Mergesort (recursivo)");

        btnOrdenar.addActionListener(e -> ejecutar("Burbuja", Documento::ordenarBurbuja));
        btnOrdenarRapido.addActionListener(e -> ejecutar("Rapido", Documento::ordenarRapido));
        btnInsercion.addActionListener(e -> ejecutar("Insercion", Documento::ordenarInsercion));
        btnSeleccion.addActionListener(e -> ejecutar("Seleccion", Documento::ordenarSeleccion));
        btnMezcla.addActionListener(e -> ejecutar("Mezcla", Documento::ordenarMezcla));
        btnRecargar.addActionListener(e -> cargarDatos());
        btnComparar.addActionListener(e -> compararTodos());

        setPreferredSize(new Dimension(900, 600));
        pack();
        setLocationRelativeTo(null);
    }

    private void cargarDatos() {
        if (nombreArchivo == null) {
            return;
        }
        Documento.obtenerLista(nombreArchivo, REGISTROS_VALOR[cmbRegistros.getSelectedIndex()]);
        Documento.mostrarLista(tblDatos);
        txtTiempo.setText("");
        lblEstado.setText(" " + Documento.documentos.length + " registros cargados (orden original)");
    }

    private void habilitar(boolean on) {
        for (JButton b : new JButton[]{btnOrdenar, btnOrdenarRapido, btnInsercion, btnSeleccion,
            btnMezcla, btnComparar, btnRecargar}) {
            b.setEnabled(on);
        }
        cmbCriterio.setEnabled(on);
        cmbRegistros.setEnabled(on);
    }

    /** Ejecuta un algoritmo en segundo plano para no congelar la ventana. */
    private void ejecutar(String nombre, IntFunction<String> algoritmo) {
        if (cmbCriterio.getSelectedIndex() < 0 || Documento.documentos == null) {
            return;
        }
        final int criterio = cmbCriterio.getSelectedIndex();
        habilitar(false);
        txtTiempo.setText("Ordenando...");
        lblEstado.setText(" Ordenando con " + nombre + " (" + CRITERIOS[criterio] + ")...");
        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                return algoritmo.apply(criterio);
            }

            @Override
            protected void done() {
                try {
                    txtTiempo.setText(get());
                    Documento.mostrarLista(tblDatos);
                    lblEstado.setText(" " + nombre + " terminado - " + Documento.documentos.length
                            + " registros - criterio " + CRITERIOS[criterio]);
                } catch (Exception ex) {
                    txtTiempo.setText("Error");
                    lblEstado.setText(" Error: " + ex);
                }
                habilitar(true);
            }
        }.execute();
    }

    /** Ejecuta los 5 algoritmos sobre la misma lista original y muestra la comparacion. */
    private void compararTodos() {
        if (Documento.documentos == null) {
            return;
        }
        final int criterio = cmbCriterio.getSelectedIndex();
        habilitar(false);
        new SwingWorker<Object[][], String>() {
            @Override
            protected Object[][] doInBackground() {
                String[] nombres = {"Burbuja", "Rapido (Quicksort)", "Insercion", "Seleccion", "Mezcla (Mergesort)"};
                String[] tipos = {"No recursivo", "Recursivo", "No recursivo", "No recursivo", "Recursivo"};
                IntFunction<String>[] algs = new IntFunction[]{
                    (IntFunction<String>) Documento::ordenarBurbuja,
                    (IntFunction<String>) Documento::ordenarRapido,
                    (IntFunction<String>) Documento::ordenarInsercion,
                    (IntFunction<String>) Documento::ordenarSeleccion,
                    (IntFunction<String>) Documento::ordenarMezcla};
                Object[][] filas = new Object[nombres.length][4];
                for (int i = 0; i < nombres.length; i++) {
                    publish("Probando " + nombres[i] + "...");
                    Documento.restaurarOriginal();
                    String t = algs[i].apply(criterio);
                    filas[i] = new Object[]{nombres[i], tipos[i], t, Util.obtenerMilisegundos()};
                }
                return filas;
            }

            @Override
            protected void process(java.util.List<String> msgs) {
                lblEstado.setText(" " + msgs.get(msgs.size() - 1));
            }

            @Override
            protected void done() {
                try {
                    Object[][] filas = get();
                    Documento.mostrarLista(tblDatos);
                    JTable t = new JTable(filas, new String[]{"Algoritmo", "Tipo", "Tiempo", "ms"});
                    JScrollPane sp = new JScrollPane(t);
                    sp.setPreferredSize(new Dimension(520, 130));
                    JOptionPane.showMessageDialog(FrmOrdenamiento.this, sp,
                            "Comparacion - " + Documento.documentos.length + " registros - " + CRITERIOS[criterio],
                            JOptionPane.INFORMATION_MESSAGE);
                    lblEstado.setText(" Comparacion terminada");
                } catch (Exception ex) {
                    lblEstado.setText(" Error: " + ex);
                }
                habilitar(true);
            }
        }.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FrmOrdenamiento().setVisible(true));
    }
}
