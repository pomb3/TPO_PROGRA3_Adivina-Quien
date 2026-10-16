package FunctionalThings;

import Interfaz.Marcador;
import Interfaz.ModoDeJuego;
import Things.Personaje;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * MODO 1: el humano juega contra las dos maquinas.
 *
 * Reglas implementadas:
 *  - Los tres jugadores eligen un personaje distinto y ya no lo pueden cambiar.
 *  - El humano tiene que adivinar el personaje de la Maquina 1.
 *  - Las dos maquinas tienen que adivinar el personaje del humano.
 *  - La Maquina 2 juega despues de la Maquina 1 y, antes de decidir, incorpora
 *    el historial de preguntas de la Maquina 1 (ventaja pedida en el enunciado).
 *  - No hay limite de preguntas. La partida termina cuando alguien acierta.
 *  - Si gana el humano, se le suma una victoria al marcador persistente.
 */
public class ModoHumanoVsMaquinas implements ModoDeJuego {

    private final Scanner scanner;
    private final GestorPersonaje gestor;
    private final Marcador marcador;

    public ModoHumanoVsMaquinas(Scanner scanner, GestorPersonaje gestor, Marcador marcador) {
        this.scanner = scanner;
        this.gestor = gestor;
        this.marcador = marcador;
    }

    @Override
    public void jugar() {
        List<Personaje> todos = gestor.getPersonajes();

        System.out.print("\nTu nombre: ");
        String nombreJugador = scanner.nextLine().trim();
        if (nombreJugador.isEmpty()) nombreJugador = "Jugador";

        System.out.println("\nHola " + nombreJugador + ". Tenes " + marcador.victoriasDe(nombreJugador)
                + " victoria(s) acumulada(s).");

        gestor.imprimirTablero();

        // --- Eleccion de personajes ---
        Personaje secretoHumano = pedirPersonaje(
                "Elegi TU personaje (id 1 a " + todos.size() + "). No lo vas a poder cambiar: ");
        System.out.println("Elegiste a " + secretoHumano.getNombreCompleto() + ". Queda fijo.");

        List<Personaje> disponibles = new ArrayList<>(todos);
        disponibles.remove(secretoHumano);
        Collections.shuffle(disponibles);
        Personaje secretoM1 = disponibles.get(0);
        Personaje secretoM2 = disponibles.get(1);

        JugadorMaquina m1 = new JugadorMaquina(new EstrategiaCautelosa(), todos);
        JugadorMaquina m2 = new JugadorMaquina(new EstrategiaArriesgada(), todos);
        m1.setSecretoPropio(secretoM1);
        m2.setSecretoPropio(secretoM2);

        // El humano tambien queda representado como un jugador con su propio
        // secreto encapsulado, igual que las maquinas (ver JugadorHumano).
        JugadorHumano humano = new JugadorHumano(secretoHumano);

        Tablero tableroHumano = new Tablero(todos);
        List<Pregunta> preguntasHumano = FiltrosBusqueda.catalogo();
        HistorialPreguntas historialHumano = new HistorialPreguntas();

        System.out.println("\nLas dos maquinas ya eligieron su personaje (distinto al tuyo y entre si).");
        System.out.println("Tu objetivo: descubrir el personaje de la " + m1.getNombre() + ".");
        System.out.println("Objetivo de las dos maquinas: descubrir el tuyo.");
        System.out.println("Gana el primero que acierte. No hay limite de preguntas.");

        System.out.print("\nQueres ver el razonamiento completo de las maquinas? (s/n): ");
        boolean verboso = scanner.nextLine().trim().equalsIgnoreCase("s");

        ResultadoPartida resultado = null;
        int turno = 1;

        while (resultado == null) {
            System.out.println("\n############### TURNO " + turno + " ###############");

            // ---------- Turno del humano ----------
            if (turnoHumano(tableroHumano, preguntasHumano, historialHumano, m1)) {
                resultado = new ResultadoPartida(nombreJugador, turno, secretoM1, true);
                break;
            }

            // ---------- Turno de la Maquina 1 ----------
            if (turnoMaquina(m1, humano, verboso)) {
                resultado = new ResultadoPartida(m1.getNombre(), turno, humano.revelarSecreto(), false);
                break;
            }

            // ---------- Turno de la Maquina 2 (con la ventaja) ----------
            int recorte = m2.incorporarHistorialAjeno(m1.getHistorial());
            if (recorte > 0) {
                System.out.println("\n[" + m2.getNombre() + "] Aprovecho las preguntas de la "
                        + m1.getNombre() + ": descarto " + recorte + " candidato(s) sin gastar turno.");
            }
            if (turnoMaquina(m2, humano, verboso)) {
                resultado = new ResultadoPartida(m2.getNombre(), turno, humano.revelarSecreto(), false);
                break;
            }

            turno++;
        }

        resultado.imprimir();
        System.out.println(" Tu personaje era: " + humano.revelarSecreto().getNombreCompleto());
        System.out.println(" El de la " + m1.getNombre() + " era: " + m1.getSecretoPropio().getNombreCompleto());
        System.out.println(" El de la " + m2.getNombre() + " era: " + m2.getSecretoPropio().getNombreCompleto());

        if (resultado.ganoElHumano()) {
            marcador.registrarVictoria(nombreJugador);
            System.out.println(" Marcador actualizado: " + nombreJugador + " lleva "
                    + marcador.victoriasDe(nombreJugador) + " victoria(s).");
        }
    }

    /** Devuelve true si el humano acerto en este turno. */
    private boolean turnoHumano(Tablero tablero, List<Pregunta> preguntas,
                                HistorialPreguntas historial, JugadorMaquina objetivoMaquina) {
        while (true) {
            System.out.println("\n--- Tu turno --- (te quedan " + tablero.cantidadCandidatos() + " candidatos)");
            System.out.println(" 1) Hacer una pregunta");
            System.out.println(" 2) Arriesgar quien es");
            System.out.println(" 3) Ver mis candidatos");
            System.out.print("Opcion: ");
            String op = scanner.nextLine().trim();

            if (op.equals("3")) {
                tablero.imprimirCandidatos();
                continue;
            }

            if (op.equals("1")) {
                for (int i = 0; i < preguntas.size(); i++) {
                    Pregunta p = preguntas.get(i);
                    String marca = historial.yaSePregunto(p) ? "  (ya la hiciste)" : "";
                    System.out.println(" " + (i + 1) + ") " + p.getTexto() + marca);
                }
                System.out.print("Cual pregunta: ");
                int idx = leerEntero(scanner.nextLine());
                if (idx < 1 || idx > preguntas.size()) {
                    System.out.println("Opcion invalida.");
                    continue;
                }
                Pregunta elegida = preguntas.get(idx - 1);
                boolean respuesta = objetivoMaquina.responder(elegida);
                System.out.println("Respuesta: " + (respuesta ? "SI" : "NO"));
                historial.registrar(elegida, respuesta);
                tablero.aplicarFiltro(elegida.filtroSegun(respuesta));
                System.out.println("Te quedan " + tablero.cantidadCandidatos()
                        + " candidatos: " + tablero.resumenCandidatos());
                return false;
            }

            if (op.equals("2")) {
                System.out.print("Id del personaje que arriesgas: ");
                int id = leerEntero(scanner.nextLine());
                Personaje apuesta = gestor.buscarPersonajePorID(id);
                if (apuesta == null) {
                    System.out.println("Ese id no existe.");
                    continue;
                }
                if (objetivoMaquina.esElegido(apuesta)) {
                    System.out.println("ACERTASTE: era " + apuesta.getNombreCompleto() + ".");
                    return true;
                }
                System.out.println("No, no es " + apuesta.getNombreCompleto() + ". Lo descarto de tu tablero.");
                tablero.descartar(apuesta);
                return false;
            }

            System.out.println("Opcion invalida.");
        }
    }

    /** Devuelve true si la maquina acerto en este turno. */
    private boolean turnoMaquina(JugadorMaquina maquina, JugadorHumano humano, boolean verboso) {
        System.out.println("\n--- Turno de " + maquina.getNombre() + " --- (le quedan "
                + maquina.getTablero().cantidadCandidatos() + " candidatos)");

        Jugada jugada = maquina.pensarJugada();

        if (verboso) {
            for (String linea : jugada.getAnalisis()) {
                System.out.println("   " + linea);
            }
        }

        if (jugada.esPregunta()) {
            boolean respuesta = humano.responder(jugada.getPregunta());
            System.out.println("Pregunta: " + jugada.getPregunta().getTexto()
                    + "  ->  " + (respuesta ? "SI" : "NO"));
            System.out.println("   Razonamiento: " + jugada.getMotivo());
            maquina.procesarRespuesta(jugada.getPregunta(), respuesta);
            System.out.println("   Le quedan " + maquina.getTablero().cantidadCandidatos() + " candidatos.");
            return false;
        }

        System.out.println("Arriesga: " + jugada.getApuesta().getNombreCompleto());
        System.out.println("   Razonamiento: " + jugada.getMotivo());
        if (humano.esElegido(jugada.getApuesta())) {
            System.out.println("   Y ACERTO.");
            return true;
        }
        System.out.println("   Fallo. Descarta ese candidato.");
        maquina.descartar(jugada.getApuesta());
        return false;
    }

    private Personaje pedirPersonaje(String mensaje) {
        while (true) {
            System.out.print("\n" + mensaje);
            int id = leerEntero(scanner.nextLine());
            Personaje p = gestor.buscarPersonajePorID(id);
            if (p != null) return p;
            System.out.println("Id invalido, probá de nuevo.");
        }
    }

    private int leerEntero(String texto) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
