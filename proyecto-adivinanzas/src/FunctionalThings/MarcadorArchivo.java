package FunctionalThings;

import Interfaz.Marcador;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Persistencia del marcador record en un archivo de texto plano.
 * Formato: una linea por jugador -> nombre;victorias
 *
 * Se elige archivo de texto por simplicidad y porque no requiere librerias
 * externas ni base de datos. Al estar detras de la interfaz Marcador, se puede
 * cambiar la implementacion sin tocar el resto del programa.
 */
public class MarcadorArchivo implements Marcador {

    private static final String ARCHIVO = "marcador.txt";
    private final Map<String, Integer> victorias = new LinkedHashMap<>();

    public MarcadorArchivo() {
        cargar();
    }

    private void cargar() {
        File f = new File(ARCHIVO);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                String[] partes = linea.split(";");
                if (partes.length == 2) {
                    try {
                        victorias.put(partes[0], Integer.parseInt(partes[1]));
                    } catch (NumberFormatException e) {
                        // linea corrupta: se ignora
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("No se pudo leer el marcador: " + e.getMessage());
        }
    }

    private void guardar() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO))) {
            for (Map.Entry<String, Integer> e : victorias.entrySet()) {
                bw.write(e.getKey() + ";" + e.getValue());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("No se pudo guardar el marcador: " + e.getMessage());
        }
    }

    @Override
    public void registrarVictoria(String jugador) {
        victorias.put(jugador, victoriasDe(jugador) + 1);
        guardar();
    }

    @Override
    public int victoriasDe(String jugador) {
        Integer v = victorias.get(jugador);
        return (v == null) ? 0 : v;
    }

    @Override
    public List<String> ranking() {
        List<Map.Entry<String, Integer>> entradas = new ArrayList<>(victorias.entrySet());
        entradas.sort(Comparator.comparing(Map.Entry<String, Integer>::getValue).reversed());
        List<String> lineas = new ArrayList<>();
        int puesto = 1;
        for (Map.Entry<String, Integer> e : entradas) {
            lineas.add(String.format("%2d. %-20s %d victoria(s)", puesto, e.getKey(), e.getValue()));
            puesto++;
        }
        return lineas;
    }
}
