# Juego de adivinanzas — Documentación técnica

## 1. Qué resuelve el programa

Un juego tipo "¿Quién es quién?" con 23 personajes. Hay dos modos:

- **Humano vs las dos máquinas.** Los tres jugadores eligen un personaje distinto y no lo pueden cambiar. El humano tiene que descubrir el personaje de la Máquina 1; las dos máquinas tienen que descubrir el del humano. Gana el primero que acierta. No hay límite de preguntas.
- **Máquina vs Máquina.** Las dos máquinas compiten por descubrir el mismo personaje secreto, elegido al azar por el sistema y desconocido para ambas. Se imprime todo el proceso de decisión turno a turno: candidatos vivos, evaluación de cada pregunta posible con su partición y su peor caso, la pregunta elegida y el motivo. Es el escenario que permite comparar las dos estrategias en igualdad de condiciones.

Además hay un marcador record persistente (archivo `marcador.txt`) y una opción de menú que imprime el árbol de decisión completo.

## 2. Los 23 personajes

Los filtros habilitados son: género, calvicie, lentes y tres colores de pelo (colorado, negro, amarillo). Eso da `2 × 2 × 2 × 3 = 24` combinaciones posibles de atributos.

**Decisión de diseño:** los 23 personajes ocupan 23 de esas 24 combinaciones, todas distintas entre sí (queda libre la combinación mujer + calva + lentes + colorado). Consecuencias:

- No existen dos personajes indistinguibles, así que siempre se puede llegar a un único candidato preguntando, sin depender del azar.
- El tablero está perfectamente balanceado, lo que hace que el criterio greedy se luzca: cada pregunta parte el conjunto casi por la mitad.
- El peor caso teórico es `⌈log₂(23)⌉ = 5` preguntas, y el árbol que construye el programa tiene exactamente profundidad 5, o sea alcanza el óptimo.

Quedan 12 varones y 11 mujeres.

### Orden y asignación de ids

El enunciado pide que los personajes empiecen ordenados únicamente según su género y que sea la máquina la que arme la lista con id autoincremental. `GestorPersonaje.crearPersonajes()` hace exactamente eso, en tres pasos:

1. Se cargan los 23 personajes y se mezclan a propósito (`Collections.shuffle`), para que se note que el orden final no viene del código fuente.
2. Se ordena **solo por género** con un **merge sort propio** (`MergeSortPersonajes`), no con `List.sort()` de la librería. Ver sección 4.
3. Recién ahí se recorre la lista y se asigna `id = 1, 2, 3, ...` de forma incremental.

`Personaje.setId()` tira `IllegalStateException` si se intenta asignar un id dos veces: el id es identidad, no un dato mutable.

## 3. Arquitectura

```
Things/          Personaje (+ enum ColorCabello)
Interfaz/        Filtro, ModoDeJuego, EstrategiaMaquina, Marcador
FunctionalThings/
                 GestorPersonaje       crea, ordena y numera los 23 personajes
                 MergeSortPersonajes   merge sort propio (orden inicial por genero)
                 QuickSortPersonajes   quick sort propio (reorden por id)
                 CriteriosOrden        comparadores: por genero, por id
                 Tablero               candidatos vivos de UN jugador
                 Filtro*               los 4 filtros del enunciado
                 Pregunta              pregunta sí/no + sus dos filtros
                 FiltrosBusqueda       catálogo de las 6 preguntas
                 HistorialPreguntas    pares pregunta/respuesta
                 Jugada                preguntar o arriesgar + el porqué
                 EstrategiaBase        criterio greedy común
                 EstrategiaCautelosa   Máquina 1
                 EstrategiaArriesgada  Máquina 2
                 ArbolDecision         divide & conquer explícito
                 JugadorMaquina        motor de una máquina
                 JugadorHumano         secreto del humano, encapsulado
                 ModoHumanoVsMaquinas  modo 1
                 ModoMaquinaVsMaquina  modo 2
                 MarcadorArchivo       persistencia
                 ResultadoPartida      resumen final
Main.java        menú
```

### Patrones de diseño usados

| Patrón | Dónde | Por qué |
|---|---|---|
| **Strategy** | `EstrategiaMaquina` con `EstrategiaCautelosa` y `EstrategiaArriesgada` | El enunciado pide dos máquinas con formas distintas de jugar. En vez de duplicar el motor con `if (esMaquina1)`, el algoritmo de decisión se encapsula en objetos intercambiables. Agregar una tercera máquina es escribir una clase nueva, sin tocar nada existente. |
| **Strategy (2)** | `ModoDeJuego` con los dos modos | El `Main` no sabe cómo se juega cada modo, solo llama a `jugar()`. |
| **Template Method** | `EstrategiaBase.evaluar()` | Las dos máquinas comparten el criterio greedy de elección de pregunta; lo único que cambia es *cuándo dejan de preguntar y arriesgan*. La parte común vive en la clase base. |
| **Command / objeto-valor** | `Jugada` | El turno devuelve un objeto que describe la decisión (preguntar o arriesgar) más el motivo y el análisis. Así el modo de juego puede imprimir el razonamiento sin acoplarse a la estrategia. |
| **Factory simple** | `FiltrosBusqueda.catalogo()` | Centraliza la creación de las preguntas. Cada jugador recibe su propia lista. |
| **Inversión de dependencias** | `Marcador` como interfaz, `MarcadorArchivo` como implementación | El juego no sabe si el marcador es un `.txt`, un JSON o una base. Se puede cambiar sin tocar el resto. |
| **Strategy (3)** | `Ordenador` con `MergeSortPersonajes` y `QuickSortPersonajes` | El `GestorPersonaje` pide "ordename esto con este criterio" sin saber qué algoritmo hay atrás. Además el criterio de comparación (`CriteriosOrden`) está separado del algoritmo: el mismo merge sort sirve para ordenar por género o por id. |

### Encapsulamiento del secreto (importante para la defensa)

El enunciado prohíbe que la máquina acceda a la variable del personaje elegido por el humano. En esta implementación:

- `Personaje` es **inmutable** en sus atributos (`final`) y **no tiene** ninguna bandera tipo `elegido`. Si el objeto compartido guardara "yo soy el elegido", cualquiera con la lista podría leerlo: sería una fuga de información.
- Cada jugador guarda su secreto en su propio campo privado.
- `JugadorMaquina` no recibe nunca el `Personaje` objetivo. Lo único que entra desde afuera es un `boolean` como respuesta a una pregunta o el resultado de una apuesta. Toda su información vive en su `Tablero` y su `HistorialPreguntas`.
- El humano también está representado como un jugador con secreto encapsulado: la clase `JugadorHumano` guarda su `Personaje` en privado y sólo lo expone mediante `responder(Pregunta): boolean` y `esElegido(Personaje): boolean` — la misma forma que ya tenía `JugadorMaquina` (que ahora expone los mismos dos métodos). Antes, `ModoHumanoVsMaquinas` llamaba directamente `jugada.getPregunta().cumple(objetivo)` sobre una variable suelta; la regla ya se cumplía (ninguna estrategia recibía ese objeto), pero así queda simétrico: en todo el programa, ninguna firma de método toma el secreto de un jugador como parámetro.

## 4. Divide & Conquer

Cada pregunta binaria **divide** el conjunto de candidatos en dos subconjuntos disjuntos y complementarios (los que cumplen y los que no). Se descarta uno entero y se **conquista** el problema, ahora más chico, con el mismo procedimiento. La combinación es trivial porque las ramas son independientes.

Es la misma idea que la búsqueda binaria, generalizada: en vez de partir por "mayor o menor" sobre un número, se parte por un atributo booleano. Y se cumple la misma recurrencia:

```
T(n) = T(n/2) + O(1)   =>   O(log n)
```

Con `n = 23` da 5 preguntas en el peor caso, contra 23 de una búsqueda lineal (preguntar "¿es Fulano?" uno por uno). El programa lo demuestra en la opción 4 del menú: construye el árbol completo y muestra que su profundidad es 5.

`ArbolDecision.construir()` es la versión explícita y recursiva:

1. **Caso base:** queda 1 candidato → hoja.
2. Elegir la pregunta que mejor parte el conjunto.
3. **Dividir:** aplicar el filtro del SÍ y el del NO.
4. **Conquistar:** llamada recursiva sobre cada mitad, con una pregunta menos disponible.

### Merge Sort y Quick Sort (implementados a mano)

El segundo divide & conquer del proyecto es el ordenamiento de los personajes. Los dos algoritmos están escritos desde cero, sin usar `List.sort()`.

**Merge Sort** (`MergeSortPersonajes`) — el ejemplo canónico:

- **Dividir:** partir la lista al medio.
- **Conquistar:** ordenar cada mitad recursivamente. Caso base: una lista de un elemento ya está ordenada.
- **Combinar:** fusionar las dos mitades ordenadas recorriéndolas en paralelo y tomando siempre el menor de los dos frentes.
- Recurrencia `T(n) = 2·T(n/2) + O(n)` → `O(n log n)` **garantizado**, sin peor caso cuadrático, porque la partición siempre es exactamente por la mitad y no depende de los datos.
- Cuesta `O(n)` de memoria por el arreglo auxiliar. Es el precio de la garantía.
- Es **estable**: en `combinar()` se usa `<=` y no `<`, lo que hace que ante un empate se tome siempre el elemento de la mitad izquierda, conservando el orden relativo previo.

**Quick Sort** (`QuickSortPersonajes`) — el mismo esquema al revés:

- **Dividir:** elegir un pivote y particionar (partición de Lomuto) dejando los menores a la izquierda y los mayores a la derecha. El pivote queda en su posición definitiva.
- **Conquistar:** ordenar recursivamente cada lado, excluyendo el pivote.
- **Combinar:** no hace falta; la partición ya dejó todo en orden.
- En el merge sort el trabajo pesado está en *combinar*; en el quick sort está en *dividir*. Por eso el quick sort ordena **en el lugar**, usando solo `O(log n)` de memoria por la pila de recursión.
- `O(n log n)` en promedio, pero `O(n²)` en el peor caso, cuando el pivote parte la lista `1 / n-1` en cada nivel.
- **Pivote aleatorio**, decisión deliberada: en este programa el quick sort ordena por id listas que muchas veces ya vienen casi ordenadas, que es justamente el peor caso del pivote fijo. Al sortear la posición del pivote, el peor caso deja de depender de cómo vienen los datos.
- **No es estable**: la partición intercambia elementos lejanos entre sí.

**Por qué cada uno donde está.** El orden inicial por género usa merge sort *porque es estable*. El enunciado pide ordenar "únicamente según su género", y solo un algoritmo estable garantiza que no se cuele ningún otro criterio de desempate: con quick sort, personajes del mismo género quedarían en un orden arbitrario que depende del pivote sorteado, y los ids asignados después serían distintos en cada ejecución sin motivo. El quick sort se usa donde la estabilidad no importa y sí importa no gastar memoria: reordenar por id los candidatos de un `Tablero` antes de mostrarlos, que es una clave única y por lo tanto no tiene empates posibles.

La **opción 6 del menú** demuestra todo esto en vivo: corre los dos algoritmos sobre la misma lista, cuenta las comparaciones de cada uno, y prueba la estabilidad partiendo de la lista ordenada por id y ordenando por género (el merge sort deja los ids `[1,2,3,...,23]`, el quick sort los deja desordenados dentro de cada género).

**Por qué el árbol no se precalcula durante la partida.** Se construye igual y se muestra, pero las máquinas recalculan la mejor pregunta turno a turno. Motivos: (a) el resultado es el mismo camino, con costo despreciable para n=23; (b) el conjunto de candidatos puede cambiar por fuera del árbol, por ejemplo cuando una apuesta falla y hay que descartar ese candidato, y un árbol fijo no contempla esa rama; (c) no hay que mantener en memoria una estructura que se recorre una sola vez.

## 5. Algoritmo Greedy

Elegir la **secuencia óptima** de preguntas es un problema de optimización de árbol de decisión: habría que explorar todas las órdenes posibles de preguntas y todas sus ramas, lo que crece exponencialmente. Es un problema NP-difícil en el caso general.

En vez de eso se usa una heurística **greedy**: en cada turno se toma la mejor decisión *local*, sin mirar más allá del turno.

### Greedy 1 — elección de pregunta (`EstrategiaBase.evaluar`)

Para cada pregunta disponible se cuenta cuántos candidatos caen del lado SÍ (`s`) y cuántos del NO (`n - s`), y se elige la que **minimiza el peor caso**:

```
mejor = argmin ( max(s, n - s) )
```

Es decir, la partición más cercana a la mitad. Es el criterio de *minimax*: garantizar que, responda lo que responda el rival, el conjunto quede lo más chico posible. Es equivalente a maximizar la información obtenida (una partición 50/50 da 1 bit completo; una 22/1 da casi nada).

- **Costo:** `O(P × n)` por turno, con P = 6 preguntas y n ≤ 23. Trivial.
- **Optimalidad:** no está garantizada en general (greedy no la garantiza nunca), pero en este tablero balanceado alcanza el óptimo de 5 preguntas, verificable con la opción 4 del menú.
- Las preguntas que no separan nada (`s = 0` o `s = n`) se descartan explícitamente: no aportan información y solo gastarían un turno.

### Greedy 2 — cuándo arriesgar (lo que diferencia a las dos máquinas)

Este es el segundo criterio greedy y es el que hace que "una máquina sea más asertiva que la otra".

**Máquina 1 — `EstrategiaCautelosa`.** Nunca arriesga hasta tener certeza. Sigue preguntando mientras haya alguna pregunta que separe candidatos, y apuesta recién cuando queda uno solo (probabilidad 100%). Nunca desperdicia un turno en una apuesta fallida, pero siempre paga el camino completo de preguntas.

**Máquina 2 — `EstrategiaArriesgada`.** Usa el mismo criterio para elegir pregunta, pero compara turnos esperados antes de decidir:

- Si apuesta ahora con `n` candidatos vivos, gana en este turno con probabilidad `1/n`.
- Si va a lo seguro, necesita `⌈log₂(n)⌉` preguntas más y después un turno de apuesta.

Con `n ≤ 3` la apuesta rinde: al menos ~33% de ganar ya mismo contra 2 o 3 turnos garantizados de espera. Ese es el umbral (`UMBRAL_RIESGO = 3`, constante fácil de tocar). Si falla, descarta ese candidato y sigue, así que el costo del error es un turno.

Es greedy porque decide con la información del turno actual, sin simular la partida completa ni modelar cuántos turnos le faltan al rival.

### La ventaja de la Máquina 2

El enunciado pide que la Máquina 2 arranque conociendo las preguntas de la Máquina 1. Está implementado en `JugadorMaquina.incorporarHistorialAjeno()`: antes de su turno, la Máquina 2 recibe el `HistorialPreguntas` de la Máquina 1 y aplica de una todos esos pares pregunta/respuesta a su propio tablero.

Efecto práctico: en el turno 1 la Máquina 2 empieza con ~11 candidatos en vez de 23, sin haber gastado una sola pregunta. Combinado con su disposición a arriesgar, suele ganar por uno o dos turnos. En el modo Máquina vs Máquina esto se imprime explícitamente, incluido el historial recibido y cuántos candidatos descartó gratis.

## 6. Complejidad

| Operación | Costo |
|---|---|
| Ordenar por género (merge sort) | `O(n log n)` garantizado, `O(n)` memoria |
| Reordenar por id (quick sort) | `O(n log n)` promedio, `O(n²)` peor caso, `O(log n)` memoria |
| Buscar personaje por id | `O(1)` (la lista está indexada 1..n) |
| Aplicar un filtro | `O(n)` |
| Evaluar todas las preguntas de un turno | `O(P × n)` |
| Partida completa (una máquina) | `O(P × n × log n)` |
| Preguntas necesarias, peor caso | `⌈log₂ 23⌉ = 5` |
| Construir el árbol completo | `O(P × n × log n)`, profundidad 5 |

## 7. Algoritmos considerados y descartados

Sirve tenerlos a mano porque el docente puede preguntar por los que **no** se aplicaron.

- **Búsqueda lineal** ("¿es el 1? ¿es el 2? ..."): `O(n)`, 23 turnos en el peor caso. Descartada por lenta, aunque sigue disponible como jugada manual para el humano.
- **Búsqueda binaria pura sobre los ids**: sería `O(log n)`, pero exige una respuesta de tipo "más alto / más bajo", que en este juego no existe: el rival solo puede contestar sí o no sobre atributos visibles. Un id no es un atributo observable del personaje, así que preguntar por él sería hacer trampa. Por eso el divide & conquer se implementa sobre los atributos, no sobre el id.
- **Backtracking / búsqueda exhaustiva del árbol óptimo**: da el árbol perfecto pero es exponencial en la cantidad de preguntas. Con este tablero el greedy ya llega al óptimo (profundidad 5), así que no se justifica.
- **Programación dinámica sobre subconjuntos**: `O(2ⁿ)` estados, inviable para n = 23 y además innecesario por lo mismo.
- **Otros ordenamientos**: burbuja, selección e inserción son `O(n²)`; con n = 23 andarían igual, pero no escalan y no ilustran divide & conquer. Inserción tiene una virtud real (es `O(n)` sobre datos casi ordenados y es estable) y de hecho es lo que usa TimSort para los tramos chicos; se descartó por no ser divide & conquer. **Heap sort** da `O(n log n)` garantizado y en el lugar, o sea lo mejor de los dos, pero no es estable y no es divide & conquer, así que no servía para el orden por género ni como ejemplo del paradigma.
- **Entropía de Shannon** como criterio de elección en vez de minimax: maximizar la información esperada en vez de minimizar el peor caso. Es una alternativa válida y también greedy; con un tablero balanceado como este las dos elegirían la misma pregunta. Se prefirió minimax porque es más simple de explicar y de justificar.

## 8. Cómo compilar y ejecutar

```bash
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out Main
```

En IntelliJ: marcar `src` como *Sources Root* y correr `Main`.

El archivo `marcador.txt` se crea solo en el directorio desde donde se ejecuta el programa.

## 9. Decisiones de diseño discutibles (para la defensa)

Son puntos donde el enunciado admite más de una lectura. Conviene tenerlos identificados.

1. **Quién adivina a quién en el modo humano.** El enunciado dice que las dos máquinas compiten contra el humano y que cada una elige un personaje distinto para sí misma. Se resolvió así: el humano persigue el personaje de la Máquina 1, y las dos máquinas persiguen el del humano. De esta forma la ventaja de la Máquina 2 (conocer las preguntas de la Máquina 1) tiene sentido, porque ambas preguntan sobre el mismo objetivo. Se puede cambiar el objetivo del humano en `ModoHumanoVsMaquinas.jugar()`, en la línea donde se decide `secretoM1` como objetivo.

2. **Objetivo en el modo Máquina vs Máquina.** El enunciado pide, por un lado, que cada máquina elija su propio personaje, y por otro, que la Máquina 2 aproveche el historial de preguntas de la Máquina 1 como ventaja — dos lecturas incompatibles entre sí: si cada una tuviera un secreto distinto, las preguntas que la Máquina 1 hizo sobre *su* objetivo no ayudarían en nada a la Máquina 2 a encontrar un objetivo *diferente*. Se optó por un personaje secreto único y compartido, elegido por el sistema y desconocido para ambas, que es la única lectura bajo la cual esa ventaja explícita tiene sentido.

3. **Color de pelo de los personajes calvos.** Todos los personajes tienen un color declarado, incluidos los calvos (se interpreta como el color de cejas y barba). Esto mantiene la lógica total: la pregunta por color siempre tiene respuesta y nunca hay `null`. La alternativa —color nulo para los calvos— obligaría a tratar un caso especial en tres filtros.

4. **Merge sort para el orden inicial en vez de quick sort.** Se justifica por estabilidad, como se explica en la sección 4. Si el docente prefiere quick sort en ese punto, alcanza con cambiar la línea `private final Ordenador ordenadorInicial = new MergeSortPersonajes();` en `GestorPersonaje` — esa es exactamente la ventaja de haber puesto el ordenamiento detrás de una interfaz.

5. **Umbral de riesgo de la Máquina 2.** Está fijo en 3 y es una constante. Se puede justificar en 2 (más conservadora) o en 4 (más agresiva). El cálculo que lo sustenta está en el comentario de `EstrategiaArriesgada`.
