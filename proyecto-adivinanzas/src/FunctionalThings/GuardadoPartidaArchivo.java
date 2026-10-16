package FunctionalThings;

import Interfaz.GuardadoPartida;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class GuardadoPartidaArchivo implements GuardadoPartida {

    private static final String ARCHIVO = "sesion.txt";

    @Override
    public void guardar(SesionPartida sesion) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO))) {
            bw.write(sesion.getNombreJugador() + ";"
                    + sesion.getVictoriasHumano() + ";"
                    + sesion.getVictoriasMaquinas());
        } catch (IOException e) {
            System.out.println("No se pudo guardar la sesion: " + e.getMessage());
        }
    }

    @Override
    public SesionPartida cargar() {
        File f = new File(ARCHIVO);
        if (!f.exists()) return null;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea = br.readLine();
            if (linea == null) return null;
            String[] partes = linea.trim().split(";");
            if (partes.length != 3) return null;
            return new SesionPartida(partes[0], Integer.parseInt(partes[1]), Integer.parseInt(partes[2]));
        } catch (IOException | NumberFormatException e) {
            return null;
        }
    }

    @Override
    public boolean existeGuardada() {
        return new File(ARCHIVO).exists();
    }

    @Override
    public void borrar() {
        new File(ARCHIVO).delete();
    }
}
