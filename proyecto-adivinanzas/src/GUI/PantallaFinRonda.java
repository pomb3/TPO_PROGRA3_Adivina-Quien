package GUI;

import FunctionalThings.SesionPartida;
import Things.Personaje;

import javax.swing.*;
import java.awt.*;

/**
 * Se muestra al terminar cada ronda. Revela los tres secretos y, segun si la
 * SESION ya llego a las rondas necesarias, ofrece "jugar otra ronda" o
 * cierra la partida completa.
 *
 * El registro en la SesionPartida y en el Marcador historico ya lo hizo
 * VentanaPrincipal.mostrarFinRonda ANTES de crear esta pantalla, asi que
 * aca solo se lee el estado ya actualizado.
 */
public class PantallaFinRonda extends JPanel {

    public PantallaFinRonda(VentanaPrincipal ventana, String ganadorRonda, int turnos,
                             Personaje secretoHumano, Personaje secretoM1, Personaje secretoM2,
                             String nombreM1, String nombreM2) {

        setLayout(new GridBagLayout());
        setBackground(Estilos.FONDO);

        SesionPartida sesion = ventana.getSesionActual();
        boolean partidaTerminada = sesion.partidaTerminada();

        JPanel caja = new JPanel();
        caja.setOpaque(false);
        caja.setLayout(new BoxLayout(caja, BoxLayout.Y_AXIS));
        caja.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        JLabel titulo = new JLabel(partidaTerminada ? "Fin de la partida" : "Fin de la ronda");
        titulo.setFont(Estilos.FONT_TITULO.deriveFont(24f));
        titulo.setForeground(Estilos.NAVY_OSCURO);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        caja.add(titulo);
        caja.add(Box.createVerticalStrut(14));

        JLabel ganador = new JLabel("Gano esta ronda: " + ganadorRonda + "  (" + turnos + " turno(s))");
        ganador.setFont(Estilos.FONT_SECCION);
        ganador.setAlignmentX(Component.CENTER_ALIGNMENT);
        caja.add(ganador);
        caja.add(Box.createVerticalStrut(6));

        caja.add(centrado("Tu personaje era: " + secretoHumano.getNombreCompleto()));
        caja.add(centrado("El de la " + nombreM1 + " era: " + secretoM1.getNombreCompleto()));
        caja.add(centrado("El de la " + nombreM2 + " era: " + secretoM2.getNombreCompleto()));
        caja.add(Box.createVerticalStrut(16));

        JLabel marcadorSesion = new JLabel("Marcador de la partida: " + sesion.marcadorTexto());
        marcadorSesion.setFont(Estilos.FONT_SECCION);
        marcadorSesion.setAlignmentX(Component.CENTER_ALIGNMENT);
        caja.add(marcadorSesion);
        caja.add(Box.createVerticalStrut(18));

        if (partidaTerminada) {
            boolean ganoHumano = sesion.ganoElHumanoLaPartida();
            JLabel resultado = new JLabel(ganoHumano
                    ? "GANASTE LA PARTIDA"
                    : "PERDISTE LA PARTIDA");
            resultado.setFont(Estilos.FONT_TITULO.deriveFont(22f));
            resultado.setForeground(ganoHumano ? Estilos.VERDE : Estilos.ROJO);
            resultado.setAlignmentX(Component.CENTER_ALIGNMENT);
            caja.add(resultado);
            if (ganoHumano) {
                JLabel nota = new JLabel("Se sumo al marcador historico.");
                nota.setFont(Estilos.FONT_NOTA);
                nota.setForeground(Estilos.GRIS);
                nota.setAlignmentX(Component.CENTER_ALIGNMENT);
                caja.add(nota);
            }
            caja.add(Box.createVerticalStrut(20));

            JButton btnInicio = Estilos.botonPrincipal("Volver al inicio");
            btnInicio.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnInicio.setMaximumSize(new Dimension(220, 42));
            btnInicio.addActionListener(e -> ventana.mostrarInicio());
            caja.add(btnInicio);
        } else {
            JButton btnOtraRonda = Estilos.botonPrincipal("Jugar otra ronda");
            btnOtraRonda.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnOtraRonda.setMaximumSize(new Dimension(220, 42));
            btnOtraRonda.addActionListener(e -> ventana.irASeleccionPersonaje());
            caja.add(btnOtraRonda);
            caja.add(Box.createVerticalStrut(8));

            JButton btnGuardarSalir = Estilos.botonSecundario("Guardar y salir");
            btnGuardarSalir.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnGuardarSalir.setMaximumSize(new Dimension(220, 36));
            btnGuardarSalir.addActionListener(e -> ventana.guardarYVolverAlInicio());
            caja.add(btnGuardarSalir);
        }

        add(caja);
    }

    private JLabel centrado(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(Estilos.FONT_TEXTO);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }
}
