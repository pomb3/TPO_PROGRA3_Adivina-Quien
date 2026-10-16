package GUI;

import javax.swing.*;
import java.awt.*;

/**
 * Pantalla de inicio. Cuatro caminos posibles, mas salir.
 */
public class PantallaInicio extends JPanel {

    public PantallaInicio(VentanaPrincipal ventana) {
        setLayout(new GridBagLayout());
        setBackground(Estilos.FONDO);

        JPanel caja = new JPanel();
        caja.setOpaque(false);
        caja.setLayout(new BoxLayout(caja, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Adivina Quien");
        titulo.setFont(Estilos.FONT_TITULO);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel subtitulo = new JLabel("Diseno y Analisis de Algoritmos");
        subtitulo.setFont(Estilos.FONT_SUBTITULO);
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitulo.setForeground(Estilos.GRIS);

        caja.add(titulo);
        caja.add(subtitulo);
        caja.add(Box.createVerticalStrut(30));

        JButton btnNueva = Estilos.botonPrincipal("Nueva partida");
        btnNueva.addActionListener(e -> ventana.irAConfiguracionNueva());

        JButton btnCargar = Estilos.botonPrincipal("Cargar partida guardada");
        btnCargar.setEnabled(ventana.hayPartidaGuardada());
        btnCargar.addActionListener(e -> ventana.cargarPartidaGuardada());
        if (!ventana.hayPartidaGuardada()) {
            btnCargar.setToolTipText("No hay ninguna partida guardada todavia.");
        }

        JButton btnMaquinaVsMaquina = Estilos.botonSecundario("Ver partida Maquina vs Maquina");
        btnMaquinaVsMaquina.addActionListener(e -> ventana.irAMaquinaVsMaquina());

        JButton btnMarcador = Estilos.botonSecundario("Ver marcador");
        btnMarcador.addActionListener(e -> ventana.irAMarcador());

        JButton btnSalir = Estilos.botonSecundario("Salir");
        btnSalir.addActionListener(e -> System.exit(0));

        for (JButton b : new JButton[]{btnNueva, btnCargar, btnMaquinaVsMaquina, btnMarcador, btnSalir}) {
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            b.setMaximumSize(new Dimension(320, 42));
            caja.add(Box.createVerticalStrut(10));
            caja.add(b);
        }

        JLabel nota = new JLabel("Una partida se gana con " + FunctionalThings.SesionPartida.RONDAS_PARA_GANAR + " rondas.");
        nota.setFont(Estilos.FONT_NOTA);
        nota.setForeground(Estilos.GRIS);
        nota.setAlignmentX(Component.CENTER_ALIGNMENT);
        caja.add(Box.createVerticalStrut(20));
        caja.add(nota);

        add(caja);
    }
}
