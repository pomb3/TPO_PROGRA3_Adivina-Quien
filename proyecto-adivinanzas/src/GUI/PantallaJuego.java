package GUI;

import FunctionalThings.*;
import Things.Personaje;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * La pantalla central del juego: una ronda completa.
 *
 * Reproduce EXACTAMENTE la misma logica que ModoHumanoVsMaquinas (mismo
 * motor: Tablero, JugadorMaquina, JugadorHumano, EstrategiaCautelosa,
 * EstrategiaArriesgada, HistorialPreguntas), solo que en vez de un bucle
 * bloqueante con Scanner, cada accion del humano dispara un listener de
 * Swing que resuelve el turno completo (humano -> Maquina 1 -> Maquina 2) y
 * despues vuelve a habilitar los controles para el siguiente turno.
 */
public class PantallaJuego extends JPanel {

    private final VentanaPrincipal ventana;

    private final Personaje secretoHumano;
    private final Personaje secretoM1;
    private final Personaje secretoM2;

    private final JugadorMaquina m1;
    private final JugadorMaquina m2;
    private final JugadorHumano humano;
    private final Tablero tableroHumano;
    private final List<Pregunta> preguntasHumano;
    private final HistorialPreguntas historialHumano;
    private final boolean verboso;

    private int turno = 1;

    private final JLabel encabezado = new JLabel();
    private final JLabel contadorVivos = new JLabel();
    private final DefaultTableModel modeloCandidatos;
    private final JTable tablaCandidatos;
    private final JTextArea log = new JTextArea();
    private final List<JButton> botonesPregunta = new ArrayList<>();
    private final JButton btnArriesgar = Estilos.botonPrincipal("Arriesgar el seleccionado");

    public PantallaJuego(VentanaPrincipal ventana, Personaje secretoHumano) {
        this.ventana = ventana;
        this.secretoHumano = secretoHumano;
        this.verboso = ventana.isVerRazonamiento();

        List<Personaje> todos = ventana.getGestor().getPersonajes();
        List<Personaje> disponibles = new ArrayList<>(todos);
        disponibles.remove(secretoHumano);
        Collections.shuffle(disponibles);
        this.secretoM1 = disponibles.get(0);
        this.secretoM2 = disponibles.get(1);

        this.m1 = new JugadorMaquina(new EstrategiaCautelosa(), todos);
        this.m2 = new JugadorMaquina(new EstrategiaArriesgada(), todos);
        m1.setSecretoPropio(secretoM1);
        m2.setSecretoPropio(secretoM2);

        this.humano = new JugadorHumano(secretoHumano);
        this.tableroHumano = new Tablero(todos);
        this.preguntasHumano = FiltrosBusqueda.catalogo();
        this.historialHumano = new HistorialPreguntas();

        setLayout(new BorderLayout(8, 8));
        setBackground(Estilos.FONDO);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        encabezado.setFont(Estilos.FONT_SECCION);
        encabezado.setForeground(Estilos.NAVY_OSCURO);
        add(encabezado, BorderLayout.NORTH);

        String[] columnas = {"Id", "Nombre", "Genero", "Calvicie", "Lentes", "Color"};
        modeloCandidatos = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaCandidatos = new JTable(modeloCandidatos);
        tablaCandidatos.setFont(Estilos.FONT_TEXTO);
        tablaCandidatos.setRowHeight(22);
        tablaCandidatos.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        // Se muestran los 23 personajes SIEMPRE (como el tablero fisico real):
        // los descartados se pintan en gris en vez de desaparecer de la tabla.
        tablaCandidatos.setDefaultRenderer(Object.class, new RenderizadorCandidato());
        tablaCandidatos.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int fila = tablaCandidatos.getSelectedRow();
            btnArriesgar.setEnabled(fila >= 0 && esCandidatoVivo(fila));
        });
        JPanel panelCandidatos = new JPanel(new BorderLayout(4, 4));
        panelCandidatos.setOpaque(false);
        JPanel encabezadoTablero = new JPanel(new BorderLayout());
        encabezadoTablero.setOpaque(false);
        encabezadoTablero.add(Estilos.etiquetaSeccion("Tablero (el secreto de la Maquina 1)"), BorderLayout.WEST);
        encabezadoTablero.add(contadorVivos, BorderLayout.EAST);
        panelCandidatos.add(encabezadoTablero, BorderLayout.NORTH);
        panelCandidatos.add(new JScrollPane(tablaCandidatos), BorderLayout.CENTER);

        JPanel panelAcciones = new JPanel();
        panelAcciones.setOpaque(false);
        panelAcciones.setLayout(new BoxLayout(panelAcciones, BoxLayout.Y_AXIS));
        panelAcciones.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        panelAcciones.add(Estilos.etiquetaSeccion("Preguntar"));
        panelAcciones.add(Box.createVerticalStrut(6));
        for (Pregunta p : preguntasHumano) {
            JButton b = Estilos.botonSecundario(p.getTexto());
            b.setAlignmentX(Component.LEFT_ALIGNMENT);
            b.setMaximumSize(new Dimension(260, 32));
            b.addActionListener(e -> procesarPreguntaHumano(p));
            botonesPregunta.add(b);
            panelAcciones.add(b);
            panelAcciones.add(Box.createVerticalStrut(4));
        }
        panelAcciones.add(Box.createVerticalStrut(14));
        panelAcciones.add(Estilos.etiquetaSeccion("...o arriesgar"));
        panelAcciones.add(Box.createVerticalStrut(6));
        JLabel ayudaArriesgar = new JLabel("<html>Selecciona una fila en la tabla<br>de candidatos y presiona:</html>");
        ayudaArriesgar.setFont(Estilos.FONT_NOTA);
        ayudaArriesgar.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelAcciones.add(ayudaArriesgar);
        panelAcciones.add(Box.createVerticalStrut(6));
        btnArriesgar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnArriesgar.setMaximumSize(new Dimension(260, 36));
        btnArriesgar.setEnabled(false);
        btnArriesgar.addActionListener(e -> {
            int fila = tablaCandidatos.getSelectedRow();
            if (fila < 0) return;
            int id = (int) modeloCandidatos.getValueAt(fila, 0);
            Personaje apuesta = ventana.getGestor().buscarPersonajePorID(id);
            procesarArriesgoHumano(apuesta);
        });
        panelAcciones.add(btnArriesgar);

        JSplitPane splitCentral = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelCandidatos, panelAcciones);
        splitCentral.setResizeWeight(0.62);
        splitCentral.setOpaque(false);
        splitCentral.setBorder(null);
        add(splitCentral, BorderLayout.CENTER);

        log.setEditable(false);
        log.setFont(Estilos.FONT_MONO);
        log.setLineWrap(true);
        log.setWrapStyleWord(true);
        JScrollPane scrollLog = new JScrollPane(log);
        scrollLog.setPreferredSize(new Dimension(100, 170));
        scrollLog.setBorder(BorderFactory.createTitledBorder("Que fue pasando"));
        add(scrollLog, BorderLayout.SOUTH);

        log("Empieza la ronda. Vos buscas a la " + m1.getNombre() + " y a la " + m2.getNombre()
                + ". Ellas te buscan a vos. Sin limite de preguntas.");
        actualizarCandidatos();
        actualizarEncabezado();
        habilitarControlesHumano(true);
    }

    // ---------- acciones del humano ----------

    private void procesarPreguntaHumano(Pregunta elegida) {
        boolean respuesta = m1.responder(elegida);
        historialHumano.registrar(elegida, respuesta);
        tableroHumano.aplicarFiltro(elegida.filtroSegun(respuesta));
        log("Preguntaste: \"" + elegida.getTexto() + "\" -> " + (respuesta ? "SI" : "NO"));
        log("Te quedan " + tableroHumano.cantidadCandidatos() + " candidatos.");
        actualizarCandidatos();
        continuarConTurnosDeLasMaquinas();
    }

    private void procesarArriesgoHumano(Personaje apuesta) {
        if (m1.esElegido(apuesta)) {
            log("Arriesgaste: " + apuesta.getNombreCompleto() + " -> ACERTASTE.");
            terminarRonda(ventana.getSesionActual().getNombreJugador(), true);
            return;
        }
        log("Arriesgaste: " + apuesta.getNombreCompleto() + " -> no era. Lo descartas de tu tablero.");
        tableroHumano.descartar(apuesta);
        actualizarCandidatos();
        continuarConTurnosDeLasMaquinas();
    }

    // ---------- turnos de las maquinas (automaticos) ----------

    private void continuarConTurnosDeLasMaquinas() {
        habilitarControlesHumano(false);

        if (turnoMaquina(m1)) return;

        int recorte = m2.incorporarHistorialAjeno(m1.getHistorial());
        if (recorte > 0) {
            log("[" + m2.getNombre() + "] aprovecha el historial de " + m1.getNombre()
                    + ": descarta " + recorte + " candidato(s) sin preguntar.");
        }
        if (turnoMaquina(m2)) return;

        turno++;
        actualizarEncabezado();
        habilitarControlesHumano(true);
    }

    /** Devuelve true si esta maquina gano la ronda (y ya se transiciono a fin de ronda). */
    private boolean turnoMaquina(JugadorMaquina maquina) {
        Jugada jugada = maquina.pensarJugada();
        if (verboso) {
            for (String linea : jugada.getAnalisis()) log("   " + linea);
        }
        if (jugada.esPregunta()) {
            boolean respuesta = humano.responder(jugada.getPregunta());
            log("[" + maquina.getNombre() + "] pregunta: \"" + jugada.getPregunta().getTexto()
                    + "\" -> " + (respuesta ? "SI" : "NO"));
            maquina.procesarRespuesta(jugada.getPregunta(), respuesta);
            log("   Le quedan " + maquina.getTablero().cantidadCandidatos() + " candidatos.");
            return false;
        }
        log("[" + maquina.getNombre() + "] arriesga: " + jugada.getApuesta().getNombreCompleto());
        if (humano.esElegido(jugada.getApuesta())) {
            log("   Y ACERTO.");
            terminarRonda(maquina.getNombre(), false);
            return true;
        }
        log("   Fallo, descarta ese candidato.");
        maquina.descartar(jugada.getApuesta());
        return false;
    }

    private void terminarRonda(String ganador, boolean ganoElHumano) {
        ventana.mostrarFinRonda(ganador, turno, secretoHumano, secretoM1, secretoM2,
                ganoElHumano, m1.getNombre(), m2.getNombre());
    }

    // ---------- refrescos de UI ----------

    private void actualizarCandidatos() {
        tableroHumano.ordenarPorId();
        modeloCandidatos.setRowCount(0);
        for (Personaje p : ventana.getGestor().getPersonajes()) {
            modeloCandidatos.addRow(new Object[]{
                    p.getId(), p.getNombreCompleto(),
                    p.esMasculino() ? "Varon" : "Mujer",
                    p.esCalvo() ? "Calvo/a" : "Con pelo",
                    p.usaLentes() ? "Lentes" : "Sin lentes",
                    p.getColorCabello()
            });
        }
        btnArriesgar.setEnabled(false);
        contadorVivos.setFont(Estilos.FONT_NOTA);
        contadorVivos.setForeground(Estilos.GRIS);
        contadorVivos.setText("Vivos: " + tableroHumano.cantidadCandidatos() + " de "
                + ventana.getGestor().cantidad());
    }

    /** True si el personaje de esa fila (por id) sigue vivo en el tablero del humano. */
    private boolean esCandidatoVivo(int fila) {
        int id = (int) modeloCandidatos.getValueAt(fila, 0);
        for (Personaje p : tableroHumano.getCandidatos()) {
            if (p.getId() == id) return true;
        }
        return false;
    }

    /** Pinta en gris (y sin negrita) las filas de personajes ya descartados. */
    private class RenderizadorCandidato extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionada,
                                                         boolean foco, int fila, int columna) {
            Component c = super.getTableCellRendererComponent(tabla, valor, seleccionada, foco, fila, columna);
            boolean vivo = esCandidatoVivo(fila);
            if (!vivo) {
                c.setForeground(seleccionada ? Color.WHITE : new Color(0xAA, 0xAA, 0xAA));
                c.setFont(c.getFont().deriveFont(Font.ITALIC));
            } else {
                c.setForeground(seleccionada ? Color.WHITE : Color.BLACK);
                c.setFont(c.getFont().deriveFont(Font.PLAIN));
            }
            return c;
        }
    }

    private void actualizarEncabezado() {
        encabezado.setText("Turno " + turno + "   |   Sesion: " + ventana.getSesionActual().marcadorTexto());
    }

    private void habilitarControlesHumano(boolean habilitado) {
        for (int i = 0; i < botonesPregunta.size(); i++) {
            Pregunta p = preguntasHumano.get(i);
            botonesPregunta.get(i).setEnabled(habilitado && !historialHumano.yaSePregunto(p));
        }
        tablaCandidatos.setEnabled(habilitado);
        if (!habilitado) btnArriesgar.setEnabled(false);
    }

    private void log(String texto) {
        log.append(texto + "\n");
        log.setCaretPosition(log.getDocument().getLength());
    }
}
