import GUI.VentanaPrincipal;
import javax.swing.SwingUtilities;

/**
 * Punto de entrada de la version grafica. Main.java (consola) queda intacto;
 * este es un entry point alternativo que arranca la misma logica de juego
 * mostrada con Swing en vez de System.out/Scanner.
 */
public class MainGUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}
