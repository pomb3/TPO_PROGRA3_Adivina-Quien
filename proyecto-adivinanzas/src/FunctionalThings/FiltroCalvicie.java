package FunctionalThings;

import Interfaz.Filtro;
import Things.Personaje;
import java.util.ArrayList;
import java.util.List;

public class FiltroCalvicie implements Filtro {

    private final boolean esCalvo;

    public FiltroCalvicie(boolean esCalvo) {
        this.esCalvo = esCalvo;
    }

    @Override
    public List<Personaje> aplicar(List<Personaje> candidatos) {
        List<Personaje> resultado = new ArrayList<>();
        for (Personaje p : candidatos) {
            if (p.esCalvo() == esCalvo) {
                resultado.add(p);
            }
        }
        return resultado;
    }
}
