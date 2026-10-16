package GUI;

import FunctionalThings.GestorPersonaje;
import FunctionalThings.GuardadoPartidaArchivo;
import FunctionalThings.MarcadorArchivo;
import FunctionalThings.SesionPartida;
import Interfaz.GuardadoPartida;
import Interfaz.Marcador;
import Things.Personaje;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Dimension;

/**
 * Ventana unica de toda la aplicacion. No tiene logica de juego propia: es
 * un controlador que crea cada "pantalla" (un JPanel) y la muestra dentro de
 * si misma, reemplazando el contenido anterior.
 *
 * Mantiene el ESTADO COMPARTIDO entre pantallas (el gestor de personajes, el
 * marcador historico, el guardado de sesion y la sesion en curso), para que
 * cada pantalla no tenga que reconstruirlo.
 *
 * Igual que en la version de consola, esta clase (y las pantallas) son la
 * UNICA parte del programa que conoce Scanner-equivalentes (aca, listeners
 * de Swing). El motor del juego (GestorPersonaje, Tablero, JugadorMaquina,
 * JugadorHumano, EstrategiaMaquina...) no cambia en absoluto: la GUI es una
 * capa de presentacion nueva sobre el mismo motor ya probado en consola.
 */
public class VentanaPrincipal extends JFrame {

    private static final int ANCHO = 980;
    private static final int ALTO = 680;

    private final GestorPersonaje gestor = new GestorPersonaje();
    private final Marcador marcador = new MarcadorArchivo();
    private final GuardadoPartida guardadoPartida = new GuardadoPartidaArchivo();

    private SesionPartida sesionActual;
    private boolean verRazonamiento;

    public VentanaPrincipal() {
        super("Adivina Quien");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(ANCHO, ALTO);
        setMinimumSize(new Dimension(820, 560));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        mostrarInicio();
    }

    // ---------- getters que usan las pantallas ----------
    public GestorPersonaje getGestor() { return gestor; }
    public Marcador getMarcador() { return marcador; }
    public GuardadoPartida getGuardadoPartida() { return guardadoPartida; }
    public SesionPartida getSesionActual() { return sesionActual; }
    public boolean isVerRazonamiento() { return verRazonamiento; }

    /** Reemplaza la pantalla visible por otra. Lo usa cada pantalla para navegar. */
    public void mostrar(javax.swing.JPanel pantalla) {
        getContentPane().removeAll();
        getContentPane().add(pantalla, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    // ---------- transiciones entre pantallas ----------

    public void mostrarInicio() {
        mostrar(new PantallaInicio(this));
    }

    /** Desde "Nueva partida": pide nombre y preferencia de razonamiento visible. */
    public void irAConfiguracionNueva() {
        mostrar(new PantallaConfiguracion(this));
    }

    /** La configuracion ya eligio nombre/verboso: arranca la sesion en cero. */
    public void iniciarSesionNueva(String nombreJugador, boolean verboso) {
        this.sesionActual = new SesionPartida(nombreJugador);
        this.verRazonamiento = verboso;
        irASeleccionPersonaje();
    }

    /** Desde "Cargar partida guardada". */
    public void cargarPartidaGuardada() {
        SesionPartida cargada = guardadoPartida.cargar();
        if (cargada == null) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo leer la partida guardada (no existe o esta daniada).",
                    "Error al cargar", JOptionPane.ERROR_MESSAGE);
            mostrarInicio();
            return;
        }
        this.sesionActual = cargada;
        this.verRazonamiento = false;
        irASeleccionPersonaje();
    }

    public boolean hayPartidaGuardada() {
        return guardadoPartida.existeGuardada();
    }

    public void irASeleccionPersonaje() {
        mostrar(new PantallaSeleccionPersonaje(this));
    }

    /** El humano ya eligio su secreto: arma los jugadores y arranca la ronda. */
    public void iniciarRonda(Personaje secretoHumano) {
        mostrar(new PantallaJuego(this, secretoHumano));
    }

    public void mostrarFinRonda(String ganadorRonda, int turnos, Personaje secretoHumano,
                                 Personaje secretoM1, Personaje secretoM2, boolean ganoElHumano,
                                 String nombreM1, String nombreM2) {
        sesionActual.registrarResultadoRonda(ganoElHumano);
        if (sesionActual.partidaTerminada() && sesionActual.ganoElHumanoLaPartida()) {
            marcador.registrarVictoria(sesionActual.getNombreJugador());
            guardadoPartida.borrar();
        } else if (sesionActual.partidaTerminada()) {
            guardadoPartida.borrar();
        }
        mostrar(new PantallaFinRonda(this, ganadorRonda, turnos, secretoHumano,
                secretoM1, secretoM2, nombreM1, nombreM2));
    }

    public void guardarYVolverAlInicio() {
        guardadoPartida.guardar(sesionActual);
        JOptionPane.showMessageDialog(this, "Partida guardada. Marcador de sesion: "
                + sesionActual.marcadorTexto(), "Guardado", JOptionPane.INFORMATION_MESSAGE);
        mostrarInicio();
    }

    public void irAMarcador() {
        mostrar(new PantallaMarcador(this));
    }

    public void irAMaquinaVsMaquina() {
        mostrar(new PantallaMaquinaVsMaquina(this));
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // si no hay look and feel del sistema, se usa el por defecto de Swing
        }
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}
