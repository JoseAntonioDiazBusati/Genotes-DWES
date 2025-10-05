# Hoja de ejercicios — GeoNotesTeaching (Java 21)

## Cómo empezar (5′)

1. Abre el proyecto en IntelliJ (File → Open… → `geonotes-teaching-java21/`).
2. Asegúrate de **JDK 21**.
3. Ejecuta:

   * CLI: `Tasks > application > run`
   * Ejemplos: `Tasks > application > examples`

---

## Bloque A — Fundamentos y calentamiento (20–30′)

### A1. Validación y excepciones

**Objetivo:** reforzar validación clásica y mensajes claros.

* En `Note`, añade validaciones adicionales:

  * `title` → mínimo 3 caracteres.
  * `content` → recorta con `trim()`; si queda vacío, usa `"–"`.
* Maneja la excepción en el menú (ya lo hace) y muestra un mensaje útil.
  **Pista:** usa el *compact constructor* del `record`.
```java
public record Note(long id, String title, String content, GeoPoint location, Instant createdAt, Attachment attachment) {
    public Note {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("title requerido");
        if (title.length() < 3) throw new IllegalArgumentException("El titulo no puede ser menor a 3 caracteres .");

        if (content == null){
            content = "";
        } else {
            content = content.trim();
        }
        if (location == null) throw new IllegalArgumentException("location requerido");
        if (createdAt == null) createdAt = Instant.now();
    }
}
```
### A2. Equals/HashCode vs. Records (conceptual)

**Objetivo:** entender qué genera un `record`.

* Crea una clase `LegacyPoint` (clásica, *no record*) con `double lat, lon`, **equals**, **hashCode** y **toString** manuales.
* Compara su uso con `GeoPoint`.
  **Entrega:** breve comentario en el código o README: ¿qué ventajas / cuándo *no* usar `record`?
```java
public class LegacyPoint {
    private double lat;
    private double lon;

    public LegacyPoint(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    public String toString() {
        return "LegacyPoint{ lat = "+ lat + ", lon = "+ lon +"}";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LegacyPoint)) {
            return false;
        }else{
            return this == obj;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(lat, lon);
    }
}
```

```java
public record GeoPoint(double lat, double lon) {
    public GeoPoint {
        if (lat < -90 || lat > 90) throw new IllegalArgumentException("Latitud inválida: " + lat);
        if (lon < -180 || lon > 180) throw new IllegalArgumentException("Longitud inválida: " + lon);
    }
}
```
---

## Bloque B — Jerarquía *sealed* y `switch` moderno (25–35′)

### B1. Nuevo subtipo: `Video`

**Objetivo:** ampliar jerarquía sellada.

* Crea `public record Video(String url, int width, int height, int seconds) implements Attachment`.
* Actualiza `Attachment` (permits …) para incluir `Video`.
* Añade soporte en `Describe.describeAttachment`:

  ```java
  case Video v when v.seconds() > 120 -> "🎬 Vídeo largo";
  case Video v -> "🎬 Vídeo";
  ```
* **Exhaustividad:** comprueba que el `switch` obliga a cubrir `Video`.

```java
public record Video(
        String url,
        int width,
        int height,
        int seconds
) implements Attachment {
}
  ```
```java
public sealed interface Attachment permits Audio, Link, Photo, Video {
}
  ```
```java
public static String describeAttachment(Attachment a) {
    return switch (a) {
        case Photo p when p.width() > 1920 -> "📷 Foto en alta definición (%d x %d)".formatted(p.width(), p.height());
        case Photo p -> "📷 Foto";
        case Audio audio when audio.duration() > 300 -> {
            var mins = audio.duration() / 60;
            yield " Audio (" + mins + " min)" ;
        }
        case Audio audio -> "🎵 Audio";
        case Link l -> "🔗 %s".formatted((l.label() == null || l.label().isEmpty()) ? l.url() : l.label());
        case Video v when v.seconds() > 120-> "￿ Vídeo largo";
        case Video v-> "￿ Vídeo";
    };
}
  ```

### B2. Formato corto vs. largo en `switch`

**Objetivo:** usar `yield` con bloques.

* Cambia alguna rama de `Describe` a bloque:

  ```java
  case Audio a when a.duration() > 300 -> {
    var mins = a.duration() / 60;
    yield "🎵 Audio (" + mins + " min)";
  }
  ```
* Asegúrate de compilar y probar.

```java
case Audio audio when audio.duration() > 300 -> {
    var mins = audio.duration() / 60;
    yield " Audio (" + mins + " min)" ;
}
  ```
---

## Bloque C — *Text Blocks* y exportación (20–25′)

### C1. Export JSON pretty

**Objetivo:** mejorar legibilidad del JSON.

* En `Timeline.Render.export()`, ajusta el *text block* para alinear y sangrar mejor.
* Escapa comillas del `content` si hiciera falta (p. ej., `replace("\"","\\\"")` antes de `formatted`).

```java
public final class Render extends AbstractExporter implements Exporter {
    @Override public String export() {
        var notesList = notes.values().stream()
                .map(note -> """
                            {
                              "id": %d,
                              "title": "%s",
                              "content": "%s",
                              "location": {
                                "lat": %f,
                                "lon": %f },
                              "createdAt": "%s"
                            }
                            """.formatted(
                        note.id(),
                        note.title(),
                        note.content(),
                        note.location().lat(),
                        note.location().lon(),
                        note.createdAt()))
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.joining(",\n"));

        return """
                { "notes": [ %s ] }
                """.formatted(notesList);
    }
}
  ```

### C2. Export Markdown (extra)

**Objetivo:** practicar *text blocks*.

* Crea `MarkdownExporter` (implementa `Exporter`) que genere:

  ```md
  # GeoNotes
  - [ID 1] Título — (lat, lon) — YYYY-MM-DD
  ```
* Muestra su salida desde la CLI (añade opción 6: “Exportar Markdown”).

```java
public final class MarkdownExporter implements Exporter {
    private Note note;
    private GeoPoint ubication;

    public MarkdownExporter(Note note, GeoPoint ubication) {
        this.note = note;
        this.ubication = ubication;
    }

    @Override
    public String export() {
        return """
            # GeoNotes
            [ID]: %d - Título: %s - (%.4f, %.4f) - %s
            """.formatted(note.id(), note.title(), ubication.lat(), ubication.lon(), note.createdAt().atZone(ZoneId.of("Europe/Madrid")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }
}
  ```

```java
case 6 -> ShowMd(); // Exportar a formato Markdown

private static void ShowMd() {
    if (timeline.getNotes().isEmpty()) {
        System.out.println("No hay notas para exportar a Markdown.");
    } else {
        timeline.getNotes().values().forEach(nota -> {
            MarkdownExporter objetoMd = new MarkdownExporter(nota, nota.location());
            System.out.println("Md:\n" + objetoMd.export());
        });
    }
}
  ```
---

## Bloque D — Colecciones y orden (25–30′)

### D1. Orden por fecha y límite

**Objetivo:** practicar Streams y Comparator.

* Añade método en `Timeline`:

  ```java
  public java.util.List<Note> latest(int n)
  ```

  que devuelva las `n` notas más recientes (por `createdAt` descendente).
* Añade opción en CLI: “Listar últimas N”.

```java
public java.util.List<Note> latest(int n) {
    return getNotes()
            .values().stream()
            .sorted(java.util.Comparator.comparing(Note::createdAt).reversed())
            .limit(n)
            .toList();
}
  ```

```java
private static void getLatestNotes() {
    try {
        System.out.println("Introduce el número de notas que deseas ver: ");
        int choice = Integer.parseInt(scanner.nextLine().trim());
        var latestNotes = timeline.latest(choice);

        for (Note note : latestNotes) {
            System.out.println(note.toString());
        }
    } catch (NumberFormatException e) {
        System.out.println("❌ Entrada no válida. Por favor, ingresa un número.");
    }
}
  ```

### D2. Búsqueda con varios criterios

**Objetivo:** filtros encadenados.

* En CLI, añade una opción “Buscar avanzada”:

  * Por rango de lat/lon (ej.: lat entre A–B).
  * Por palabra clave en `title` o `content`.
* Reutiliza `Match.isInArea` o crea un método auxiliar.

```java
private static void filterNotes() {
    System.out.print("\nIntroduce la palabra clave para filtrar: ");
    var keyword = scanner.nextLine();
    System.out.println("\n--- Resultados de búsqueda ---");
    var filtered = timeline.getNotes().values().stream()
            .filter(n -> n.title().contains(keyword) || n.content().contains(keyword))
            .toList();
    if (filtered.isEmpty()) {
        System.out.println("No se encontraron notas con: " + keyword);
        return;
    }
    filtered.forEach(n -> System.out.printf("ID: %d | %s | %s%n", n.id(), n.title(), n.content()));
}
  ```

---

## Bloque E — *Pattern Matching* + *Record Patterns* (Java 21) (20–30′)

> El proyecto docente usa un enfoque clásico en `Match`, pero ahora practicaremos lo nuevo.

### E1. `instanceof` con patrón

**Objetivo:** simplificar *casting*.

* En `Describe` añade un método:

  ```java
  static int mediaPixels(Object o)
  ```

  que:

  * Si es `Photo p`, devuelva `p.width() * p.height()`.
  * Si es `Video v`, devuelva `v.width() * v.height()`.
  * Si no, 0.
* Implementa con `if (o instanceof Photo p) { ... }`.

```java
static int mediaPixels(Object o) {
    if(o instanceof Photo) {
        return ((Photo) o).width() * ((Photo) o).height();
    } else if (o instanceof Video) {
        return ((Video) o).width() * ((Video) o).height();
    } else {
        return 0;
    }
}
  ```

### E2. *Record patterns* en `if` o `switch`

**Objetivo:** desestructurar con patrón.

* Crea método en `Match`:

  ```java
  static String where(GeoPoint p)
  ```

  que use:

  ```java
  return switch (p) {
    case GeoPoint(double lat, double lon) when lat == 0 && lon == 0 -> "ORIGIN";
    case GeoPoint(double lat, double lon) when lat == 0 -> "Equator";
    case GeoPoint(double lat, double lon) when lon == 0 -> "Greenwich";
    case GeoPoint(double lat, double lon) -> "(" + lat + "," + lon + ")";
  };
  ```
* Añade opción CLI para consultar `where`.

```java
public static String where(GeoPoint p) {
    return switch (p) {
        case GeoPoint(double lat, double lon) when lat == 0 && lon == 0 -> "ORIGIN";
        case GeoPoint(double lat, double lon) when lat == 0 -> "Equator";
        case GeoPoint(double lat, double lon) when lon == 0 -> "Greenwich";
        case GeoPoint(double lat, double lon) -> "(" + lat + "," + lon + ")";
    };
}
  ```
---

## Bloque F — Errores y robustez (15–20′)

### F1. Manejo de `InputMismatch`/`NumberFormat`

**Objetivo:** entradas seguras.

* Asegura que **todas** las lecturas de números usan `Double.parseDouble(scanner.nextLine())` y están en `try/catch` con mensajes claros (ya está iniciado en `GeoNotes`).

```java
try {
    int choice = Integer.parseInt(scanner.nextLine().trim());
    switch (choice) {
        case 1 -> createNote();
        case 2 -> listNotes();
        case 3 -> filterNotes();
        case 4 -> exportNotesToJson();
        case 8 -> running = false;
        case 5 -> getLatestNotes();
        case 6 -> ShowMd();
        case 7 -> where(timeline,scanner);
        default -> System.out.println("❌ Opción no válida. Inténtalo de nuevo.");
    }
} catch (NumberFormatException e) {
    System.out.println("❌ Entrada no válida. Por favor, ingresa un número.");
} catch (Exception e) {
    System.out.println("❌ Ocurrió un error inesperado: " + e.getMessage());
}
  ```

### F2. Comprobaciones nulas

**Objetivo:** práctica “clásica” (sin null-safety de Kotlin).

* Si `label` en `Link` es nulo/vacío, muestra la `url` al exportar (ya implementado en `Describe`; revisa consistencia en exportadores).

```java
public record Link(
        String url,
        String label) implements Attachment {

    public Link {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL REQUERIDA!!!");
        }

        if (label != null && label.isBlank()) {
            label = null;
        }
    }

    public String effectiveLabel() {
        return (label == null || label.isBlank()) ? url : label;
    }
}
  ```

```java
case Link l -> "🔗 %s".formatted((l.label() == null || l.label().isEmpty()) ? l.url() : l.label());
  ```
---

## Bloque G — Extensión opcional (si hay tiempo)

### G1. Vista invertida (java 21, **Sequenced**)

**Objetivo:** mostrar la API moderna sin cambiar el enfoque clásico.

* Sustituye el `Map<Long, Note>` interno por `SequencedMap<Long,Note>` (con `LinkedHashMap`).
* Añade método:

  ```java
  public java.util.Collection<Note> reversed() { return notes.reversed().values(); }
  ```
* Opción CLI: “Listar (reversed)”.

### G2. Demo *virtual threads* (muy opcional)

**Objetivo:** idea general de Loom.

* Crea `VirtualDemo.runIO()` que lance \~50 tareas “simuladas” (sleep 200–300 ms) con:

  ```java
  try (var exec = java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor()) { ... }
  ```
* Muestra el hilo actual en cada tarea. **No** mezclar con la lógica del proyecto (solo demo).

---

## Entrega sugerida

* Cambios en el código + pequeño `README` con:

  * Lista de ejercicios hechos.
  * Notas sobre decisiones de diseño y Java vs Kotlin (2–4 bullets).

## Rúbrica (10 ptos)

* **A–B**: Validación, sealed + switch/guards (4 ptos).
* **C–D**: Text Blocks/Exporter, Streams/orden (3 ptos).
* **E**: Pattern matching + record patterns (2 ptos).
* **F**: Robustez de entradas + nulos (1 pto).
  *(+1 extra por G1 o G2)*

---

## Apéndice — Snippets útiles

**Orden por fecha:**

```java
var latest = timeline.getNotes()
    .values().stream()
    .sorted(java.util.Comparator.comparing(Note::createdAt).reversed())
    .limit(n)
    .toList();
```

**Text Block con `.formatted(...)`:**

```java
String json = """
  { "title": "%s", "content": "%s" }
  """.formatted(title, content.replace("\"","\\\""));
```

**`instanceof` con patrón:**

```java
if (obj instanceof Photo p) {
    // p ya está casteado a Photo
}
```

**`switch` con `yield`:**

```java
String label = switch (a) {
  case Audio au when au.duration() > 600 -> {
    var min = au.duration() / 60;
    yield "🎵 Podcast (" + min + " min)";
  }
  case Audio au -> "🎵 Audio";
  default -> "Otro";
};
```

---
