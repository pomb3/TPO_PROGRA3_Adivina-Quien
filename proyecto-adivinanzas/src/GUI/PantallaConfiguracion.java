package GUI;

import javax.swing.*;
import java.awt.*;

/**
 * Solo se muestra al arrancar una partida NUEVA (no al cargar una guardada,
 * que ya trae el nombre). Equivale a lo que en consola eran las preguntas
 * "Tu nombre:" y "Queres ver el razonamiento completo de las maquinas? (s/n)"
 * de ModoHumanoVsMaquinas.
 */
public class PantallaConfiguracion extends JPanel {

    public PantallaConfiguracion(VentanaPrincipal ventana) {
        setLayout(new GridBagLayout());
        setBackground(Estilos.FONDO);

        JPanel caja = new JPanel();
        caja.setOpaque(false);
        caja.setLayout(new BoxLayout(caja, BoxLayout.Y_AXIS));
        caja.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titulo = Estilos.etiquetaSeccion("Nueva partida");
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        caja.add(titulo);
        caja.add(Box.createVerticalStrut(20));

        JLabel etiquetaNombre = new JLabel("Tu nombre:");
        etiquetaNombre.setFont(Estilos.FONT_TEXTO);
        etiquetaNombre.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextField campoNombre = new JTextField("Jugador");
        campoNombre.setFont(Estilos.FONT_TEXTO);
        campoNombre.setMaximumSize(new Dimension(260, 30));
        campoNombre.setAlignmentX(Component.CENTER_ALIGNMENT);

        JCheckBox chkVerboso = new JCheckBox("Ver el razonamiento completo de las maquinas");
        chkVerboso.setOpaque(false);
        chkVerboso.setFont(Estilos.FONT_TEXTO);
        chkVerboso.setAlignmentX(Component.CENTER_ALIGNMENT);
        chkVerboso.setSelected(true);

        JLabel notaRondas = new JLabel("Se juega al mejor de varias rondas (gana quien llegue a "
                + FunctionalThings.SesionPartida.RONDAS_PARA_GANAR + ").");
        notaRondas.setFont(Estilos.FONT_NOTA);
        notaRondas.setForeground(Estilos.GRIS);
        notaRondas.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnContinuar = Estilos.botonPrincipal("Continuar");
        btnContinuar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnContinuar.setMaximumSize(new Dimension(200, 40));
        btnContinuar.addActionListener(e -> {
            String nombre = campoNombre.getText().trim();
            if (nombre.isEmpty()) nombre = "Jugador";
            ventana.iniciarSesionNueva(nombre, chkVerboso.isSelected());
        });

        JButton btnVolver = Estilos.botonSecundario("Volver");
        btnVolver.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnVolver.addActionListener(e -> ventana.mostrarInicio());

        caja.add(etiquetaNombre);
        caja.add(Box.createVerticalStrut(6));
        caja.add(campoNombre);
        caja.add(Box.createVerticalStrut(16));
        caja.add(chkVerboso);
        caja.add(Box.createVerticalStrut(10));
        caja.add(notaRondas);
        caja.add(Box.createVerticalStrut(24));
        caja.add(btnContinuar);
        caja.add(Box.createVerticalStrut(8));
        caja.add(btnVolver);

        add(caja);
    }
}
