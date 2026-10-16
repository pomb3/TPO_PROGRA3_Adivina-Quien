package GUI;

import FunctionalThings.EstrategiaArriesgada;
import FunctionalThings.EstrategiaCautelosa;
import FunctionalThings.Jugada;
import FunctionalThings.JugadorMaquina;
import Things.Personaje;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Random;

/**
 * Modo espectador: las dos maquinas compiten por el mismo personaje secreto,
 * elegido al azar y oculto para ambas (igual que ModoMaquinaVsMaquina en
 * consola). Cada columna muestra el razonamiento de una maquina; se puede
 * avanzar turno a turno o dejar que juegue sola con un Timer.
 */
public class PantallaMaquinaVsMaquina extends JPanel {

    private final JugadorMaquina m1;
    private final JugadorMaquina m2;
    private final Personaje secreto;

    private final JTextArea logM1 = new JTextArea();
    private final JTextArea logM2 = new JTextArea();
    private final JLabel contadorM1 = new JLabel();
    private final JLabel contadorM2 = new JLabel();
    private final JLabel resultadoLabel = new JLabel(" ");
    private final JButton btnPaso = Estilos.botonPrincipal("Siguiente paso");
    private final JButton btnAuto = Estilos.botonSecundario("Auto-jugar");
    private final javax.swing.Timer timer;

    private int turno = 1;
    private boolean esperandoM1 = true;
    private boolean terminado = false;

    public PantallaMaquinaVsMaquina(VentanaPrincipal ventana) {
        List<Personaje> todos = ventana.getGestor().getPersonajes();
        this.secreto = todos.get(new Random().nextInt(todos.size()));
        this.m1 = new JugadorMaquina(new EstrategiaCautelosa(), todos);
        this.m2 = new JugadorMaquina(new EstrategiaArriesgada(), todos);
        // Nota: a diferencia del modo Humano vs Maquinas, aca ninguna de las
        // dos tiene "secretoPropio": las dos persiguen el MISMO objetivo
        // externo, elegido por el sistema. Es la misma logica que en consola.

        setLayout(new BorderLayout(8, 8));
        setBackground(Estilos.FONDO);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel encabezado = new JPanel(new GridLayout(2, 1));
        encabezado.setOpaque(false);
        JLabel titulo = Estilos.etiquetaSeccion("Maquina vs Maquina (modo espectador)");
        JLabel infoSecreto = new JLabel("Personaje secreto (oculto para las dos): "
                + secreto.getNombreCompleto() + "   |   Juegan: " + m1.getNombre() + "  vs  " + m2.getNombre());
        infoSecreto.setFont(Estilos.FONT_NOTA);
        infoSecreto.setForeground(Estilos.GRIS);
        encabezado.add(titulo);
        encabezado.add(infoSecreto);
        add(encabezado, BorderLayout.NORTH);

        JPanel columnas = new JPanel(new GridLayout(1, 2, 10, 0));
        columnas.setOpaque(false);
        columnas.add(columna(m1.getNombre(), contadorM1, logM1));
        columnas.add(columna(m2.getNombre(), contadorM2, logM2));
        add(columnas, BorderLayout.CENTER);

        JPanel pie = new JPanel();
        pie.setOpaque(false);
        pie.setLayout(new BoxLayout(pie, BoxLayout.Y_AXIS));

        resultadoLabel.setFont(Estilos.FONT_SECCION);
        resultadoLabel.setForeground(Estilos.NAVY_OSCURO);
        resultadoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        pie.add(resultadoLabel);
        pie.add(Box.createVerticalStrut(8));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        botones.setOpaque(false);
        btnPaso.addActionListener(e -> siguientePaso());
        timer = new javax.swing.Timer(900, e -> siguientePaso());
        btnAuto.addActionListener(e -> {
            if (timer.isRunning()) {
                timer.stop();
                btnAuto.setText("Auto-jugar");
                btnPaso.setEnabled(true);
            } else {
                timer.start();
                btnAuto.setText("Pausar");
                btnPaso.setEnabled(false);
            }
        });
        JButton btnVolver = Estilos.botonSecundario("Volver al inicio");
        btnVolver.addActionListener(e -> {
            timer.stop();
            ventana.mostrarInicio();
        });
        botones.add(btnPaso);
        botones.add(btnAuto);
        botones.add(btnVolver);
        pie.add(botones);
        add(pie, BorderLayout.SOUTH);

        actualizarContadores();
    }

    private JPanel columna(String nombreMaquina, JLabel contador, JTextArea log) {
        JPanel panel = new JPanel(new BorderLayout(4, 4));
        panel.setOpaque(false);
        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setOpaque(false);
        JLabel nombre = new JLabel(nombreMaquina);
        nombre.setFont(Estilos.FONT_SECCION.deriveFont(15f));
        nombre.setForeground(Estilos.NAVY);
        contador.setFont(Estilos.FONT_NOTA);
        contador.setForeground(Estilos.GRIS);
        cabecera.add(nombre, BorderLayout.WEST);
        cabecera.add(contador, BorderLayout.EAST);
        panel.add(cabecera, BorderLayout.NORTH);

        log.setEditable(false);
        log.setFont(Estilos.FONT_MONO);
        log.setLineWrap(true);
        log.setWrapStyleWord(true);
        panel.add(new JScrollPane(log), BorderLayout.CENTER);
        return panel;
    }

    private void siguientePaso() {
        if (terminado) return;

        if (esperandoM1) {
            append(logM1, "--- Turno " + turno + " ---");
            boolean gano = jugarTurno(m1, logM1);
            esperandoM1 = false;
            actualizarContadores();
            if (gano) { finalizar(m1); return; }
        } else {
            int recorte = m2.incorporarHistorialAjeno(m1.getHistorial());
            if (recorte > 0) {
                append(logM2, ">>> Ventaja: descarta " + recorte
                        + " candidato(s) del historial de " + m1.getNombre() + ", sin preguntar.");
            }
            append(logM2, "--- Turno " + turno + " ---");
            boolean gano = jugarTurno(m2, logM2);
            esperandoM1 = true;
            turno++;
            actualizarContadores();
            if (gano) { finalizar(m2); return; }
        }
    }

    /** Juega un turno de una maquina contra el secreto compartido. Devuelve true si acerto. */
    private boolean jugarTurno(JugadorMaquina maquina, JTextArea destino) {
        Jugada jugada = maquina.pensarJugada();
        for (String linea : jugada.getAnalisis()) {
            append(destino, "   " + linea);
        }
        if (jugada.esPregunta()) {
            boolean respuesta = jugada.getPregunta().cumple(secreto);
            append(destino, "PREGUNTA: \"" + jugada.getPregunta().getTexto() + "\" -> " + (respuesta ? "SI" : "NO"));
            maquina.procesarRespuesta(jugada.getPregunta(), respuesta);
            append(destino, "Quedan " + maquina.getTablero().cantidadCandidatos() + " candidatos.");
            return false;
        }
        append(destino, "ARRIESGA: " + jugada.getApuesta().getNombreCompleto());
        if (jugada.getApuesta().getId() == secreto.getId()) {
            append(destino, "ACERTO.");
            return true;
        }
        append(destino, "Fallo, descarta ese candidato.");
        maquina.descartar(jugada.getApuesta());
        return false;
    }

    private void finalizar(JugadorMaquina ganadora) {
        terminado = true;
        timer.stop();
        btnPaso.setEnabled(false);
        btnAuto.setEnabled(false);
        resultadoLabel.setText("Gano " + ganadora.getNombre() + " en " + turno + " turno(s). El secreto era "
                + secreto.getNombreCompleto() + ".");
    }

    private void actualizarContadores() {
        contadorM1.setText(m1.getTablero().cantidadCandidatos() + " candidatos");
        contadorM2.setText(m2.getTablero().cantidadCandidatos() + " candidatos");
    }

    private void append(JTextArea area, String texto) {
        area.append(texto + "\n");
        area.setCaretPosition(area.getDocument().getLength());
    }
}
