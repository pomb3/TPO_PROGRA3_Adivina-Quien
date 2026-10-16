package FunctionalThings;

import Interfaz.Filtro;
import Things.Personaje;
import Things.Personaje.ColorCabello;
import java.util.ArrayList;
import java.util.List;

/**
 * incluir = true  -> deja solo los de ese color.
 * incluir = false -> deja solo los que NO son de ese color (respuesta "no").
 */
public class FiltroColorCabello implements Filtro {

    private final ColorCabello color;
    private final boolean incluir;

    public FiltroColorCabello(ColorCabello color, boolean incluir) {
        this.color = color;
        this.incluir = incluir;
    }

    @Override
    public List<Personaje> aplicar(List<Personaje> candidatos) {
        List<Personaje> resultado = new ArrayList<>();
        for (Personaje p : candidatos) {
            boolean coincide = (p.getColorCabello() == color);
            if (coincide == incluir) {
                resultado.add(p);
            }
        }
        return resultado;
    }
}
