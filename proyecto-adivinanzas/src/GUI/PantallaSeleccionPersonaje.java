package GUI;

import Things.Personaje;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Elegis TU personaje secreto entre los 23 del tablero. Se usa tanto para
 * arrancar una ronda nueva como para cada ronda siguiente de la misma
 * sesion (el tablero se resetea entero cada ronda).
 */
public class PantallaSeleccionPersonaje extends JPanel {

    public PantallaSeleccionPersonaje(VentanaPrincipal ventana) {
        setLayout(new BorderLayout(10, 10));
        setBackground(Estilos.FONDO);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        List<Personaje> personajes = ventana.getGestor().getPersonajes();

        JLabel titulo = Estilos.etiquetaSeccion("Elegi tu personaje secreto");
        JLabel ayuda = new JLabel("Las maquinas van a tratar de adivinarlo. No lo vas a poder cambiar despues.");
        ayuda.setFont(Estilos.FONT_NOTA);
        ayuda.setForeground(Estilos.GRIS);

        JPanel encabezado = new JPanel(new GridLayout(2, 1));
        encabezado.setOpaque(false);
        encabezado.add(titulo);
        encabezado.add(ayuda);
        add(encabezado, BorderLayout.NORTH);

        String[] columnas = {"Id", "Nombre", "Genero", "Calvicie", "Lentes", "Color de pelo"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        for (Personaje p : personajes) {
            modelo.addRow(new Object[]{
                    p.getId(), p.getNombreCompleto(),
                    p.esMasculino() ? "Varon" : "Mujer",
                    p.esCalvo() ? "Calvo/a" : "Con pelo",
                    p.usaLentes() ? "Lentes" : "Sin lentes",
                    p.getColorCabello()
            });
        }
        JTable tabla = new JTable(modelo);
        tabla.setFont(Estilos.FONT_TEXTO);
        tabla.setRowHeight(24);
        tabla.getTableHeader().setFont(Estilos.FONT_TEXTO.deriveFont(Font.BOLD));
        tabla.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tabla);
        add(scroll, BorderLayout.CENTER);

        JButton btnConfirmar = Estilos.botonPrincipal("Confirmar seleccion");
        btnConfirmar.setEnabled(false);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                btnConfirmar.setEnabled(tabla.getSelectedRow() >= 0);
            }
        });
        btnConfirmar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) return;
            int id = (int) modelo.getValueAt(fila, 0);
            Personaje elegido = ventana.getGestor().buscarPersonajePorID(id);
            ventana.iniciarRonda(elegido);
        });

        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pie.setOpaque(false);
        pie.add(btnConfirmar);
        add(pie, BorderLayout.SOUTH);
    }
}
