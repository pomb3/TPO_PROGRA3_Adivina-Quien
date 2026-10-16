package Things;

/**
 * Representa a uno de los 23 personajes del tablero.
 *
 * Decision de diseno: los atributos que definen al personaje son "final".
 * Una vez creado, el personaje no puede mutar, asi ninguna parte del programa
 * (sobre todo las maquinas) puede modificarlo ni marcarlo como "elegido".
 * El personaje secreto lo guarda cada jugador por separado, NO el personaje.
 */
public class Personaje {

    public enum ColorCabello {
        COLORADO, NEGRO, AMARILLO
    }

    private int id;
    private boolean idAsignado = false;

    private final String nombre;
    private final String apellido;
    private final boolean masculino;
    private final boolean calvo;
    private final boolean lentes;
    private final ColorCabello colorCabello;

    public Personaje(String nombre, String apellido, boolean masculino,
                     boolean calvo, boolean lentes, ColorCabello colorCabello) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.masculino = masculino;
        this.calvo = calvo;
        this.lentes = lentes;
        this.colorCabello = colorCabello;
    }

    /** El id lo asigna el gestor una sola vez, al ordenar la lista. */
    public void setId(int id) {
        if (idAsignado) {
            throw new IllegalStateException(
                "El personaje " + nombre + " " + apellido + " ya tiene el id " + this.id + " asignado.");
        }
        this.id = id;
        this.idAsignado = true;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getNombreCompleto() { return nombre + " " + apellido; }
    public boolean esMasculino() { return masculino; }
    public boolean esCalvo() { return calvo; }
    public boolean usaLentes() { return lentes; }
    public ColorCabello getColorCabello() { return colorCabello; }

    /** Linea descriptiva para mostrar el tablero por consola. */
    public String descripcion() {
        return String.format("%2d) %-18s %-6s %-9s %-10s %s",
                id,
                getNombreCompleto(),
                masculino ? "varon" : "mujer",
                calvo ? "calvo" : "con pelo",
                lentes ? "lentes" : "sin lentes",
                colorCabello);
    }

    @Override
    public String toString() {
        return getNombreCompleto() + " (id " + id + ")";
    }
}
