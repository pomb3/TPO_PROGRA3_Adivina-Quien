package GUI;

import javax.swing.*;
import java.awt.*;

/**
 * Constantes visuales compartidas por todas las pantallas, para que no cada
 * una defina sus propios colores y fuentes sueltos. No tiene logica de
 * juego: es pura presentacion.
 */
public final class Estilos {

    private Estilos() { }

    public static final Color FONDO = new Color(0xF4, 0xF1, 0xEA);
    public static final Color NAVY = new Color(0x1F, 0x3A, 0x5F);
    public static final Color NAVY_OSCURO = new Color(0x16, 0x28, 0x3F);
    public static final Color GOLD = new Color(0xB0, 0x86, 0x42);
    public static final Color GRIS = new Color(0x5A, 0x5A, 0x5A);
    public static final Color VERDE = new Color(0x2E, 0x7D, 0x32);
    public static final Color ROJO = new Color(0xB0, 0x2A, 0x2A);

    public static final Font FONT_TITULO = new Font("SansSerif", Font.BOLD, 30);
    public static final Font FONT_SUBTITULO = new Font("SansSerif", Font.PLAIN, 15);
    public static final Font FONT_SECCION = new Font("SansSerif", Font.BOLD, 18);
    public static final Font FONT_TEXTO = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONT_NOTA = new Font("SansSerif", Font.ITALIC, 12);
    public static final Font FONT_MONO = new Font("Monospaced", Font.PLAIN, 13);

    public static JButton botonPrincipal(String texto) {
        JButton b = new JButton(texto);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setBackground(NAVY);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setBorderPainted(false);
        // El Look & Feel por defecto no atenua automaticamente un boton con
        // color de fondo propio al deshabilitarlo, asi que lo hacemos a mano.
        b.addPropertyChangeListener("enabled", e -> {
            boolean habilitado = (boolean) e.getNewValue();
            b.setBackground(habilitado ? NAVY : new Color(0xB9, 0xC0, 0xC9));
            b.setForeground(habilitado ? Color.WHITE : new Color(0x7A, 0x7A, 0x7A));
        });
        return b;
    }

    public static JButton botonSecundario(String texto) {
        JButton b = new JButton(texto);
        b.setFont(new Font("SansSerif", Font.PLAIN, 13));
        b.setBackground(Color.WHITE);
        b.setForeground(NAVY_OSCURO);
        b.setFocusPainted(false);
        b.addPropertyChangeListener("enabled", e -> {
            boolean habilitado = (boolean) e.getNewValue();
            b.setForeground(habilitado ? NAVY_OSCURO : new Color(0xB0, 0xB0, 0xB0));
        });
        return b;
    }

    public static JLabel etiquetaSeccion(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(FONT_SECCION);
        l.setForeground(NAVY_OSCURO);
        return l;
    }
}
