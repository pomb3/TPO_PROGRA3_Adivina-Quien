package FunctionalThings;

import Interfaz.Filtro;
import Things.Personaje;
import java.util.ArrayList;
import java.util.List;

public class FiltroGenero implements Filtro {

    private final boolean esMasculino;

    public FiltroGenero(boolean esMasculino) {
        this.esMasculino = esMasculino;
    }

    @Override
    public List<Personaje> aplicar(List<Personaje> candidatos) {
        List<Personaje> resultado = new ArrayList<>();
        for (Personaje p : candidatos) {
            if (p.esMasculino() == esMasculino) {
                resultado.add(p);
            }
        }
        return resultado;
    }
}
