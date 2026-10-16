package Interfaz;

import FunctionalThings.SesionPartida;

/**
 * Persistencia del progreso de UNA SESION (partida), no de una ronda a medio
 * jugar. Misma idea que Marcador: el resto del programa no sabe si atras hay
 * un archivo de texto, JSON o una base de datos.
 */
public interface GuardadoPartida {
    void guardar(SesionPartida sesion);
    SesionPartida cargar();
    boolean existeGuardada();
    void borrar();
}
