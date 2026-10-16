package FunctionalThings;

import Interfaz.Filtro;
import Things.Personaje;
import java.util.ArrayList;
import java.util.List;

public class FiltroLentes implements Filtro {

    private final boolean tieneLentes;

    public FiltroLentes(boolean tieneLentes) {
        this.tieneLentes = tieneLentes;
    }

    @Override
    public List<Personaje> aplicar(List<Personaje> candidatos) {
        List<Personaje> resultado = new ArrayList<>();
        for (Personaje p : candidatos) {
            if (p.usaLentes() == tieneLentes) {
                resultado.add(p);
            }
        }
        return resultado;
    }
}
