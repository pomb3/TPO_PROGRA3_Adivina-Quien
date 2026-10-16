package Interfaz;

import java.util.List;

/**
 * Marcador record persistente. Se define como interfaz para que el resto del
 * programa no dependa de COMO se guarda (archivo de texto hoy, base de datos manana).
 */
public interface Marcador {
    void registrarVictoria(String jugador);
    int victoriasDe(String jugador);
    List<String> ranking();
}
