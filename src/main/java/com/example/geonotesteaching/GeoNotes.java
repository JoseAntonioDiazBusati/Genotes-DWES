package com.example.geonotesteaching;

import java.time.Instant;
import java.util.Scanner;

public class GeoNotes {
    // Timeline donde se guardan todas las notas
    private final static Timeline timeline = new Timeline();
    // Scanner para leer datos desde la entrada estándar (teclado)
    private final static Scanner scanner = new Scanner(System.in);
    // Contador para generar IDs únicos para las notas
    private static long noteCounter = 1;

    public static void main(String[] args) {
        // Si el programa se ejecuta con el argumento "examples",
        // se crean notas de ejemplo y se exportan a JSON automáticamente
        if (args != null && args.length > 0 && "examples".equalsIgnoreCase(args[0])) {
            seedExamples();
            exportNotesToJson();
            return;
        }

        // Mensaje de bienvenida
        System.out.println("--------------------------------------");
        System.out.println("  📝 Bienvenid@ a la aplicación GeoNotes");
        System.out.println("--------------------------------------");

        boolean running = true;
        while (running) { // Bucle principal del menú
            printMenu(); // Mostrar opciones disponibles
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim()); // Leer opción del usuario
                // Switch con las acciones del menú
                switch (choice) {
                    case 1 -> createNote();       // Crear nueva nota
                    case 2 -> listNotes();        // Listar todas las notas
                    case 3 -> filterNotes();      // Buscar notas por palabra clave
                    case 4 -> exportNotesToJson();// Exportar a JSON
                    case 8 -> running = false;    // Salir del programa
                    case 5 -> getLatestNotes();   // Listar últimas N notas
                    case 6 -> ShowMd();           // Exportar a formato Markdown
                    case 7 -> where();            // Clasificar ubicación (GeoPoint)
                    default -> System.out.println("❌ Opción no válida. Inténtalo de nuevo.");
                }
            } catch (NumberFormatException e) {
                // Si el usuario introduce algo que no es número
                System.out.println("❌ Entrada no válida. Por favor, ingresa un número.");
            }
        }
        System.out.println("¡Gracias por usar GeoNotes! 👋");
    }

    // Muestra las opciones del menú en pantalla
    private static void printMenu() {
        System.out.println("\n--- Menú ---");
        System.out.println("1. Crear una nueva nota");
        System.out.println("2. Listar todas las notas");
        System.out.println("3. Filtrar notas por palabra clave");
        System.out.println("4. Exportar notas a JSON (Text Blocks)");
        System.out.println("5. Listar ultimas N");
        System.out.println("6. Exportar Markdown");
        System.out.println("7. Mostrar  el where");
        System.out.println("8. Salir");
        System.out.print("Elige una opción: ");
    }

    // Crea una nueva nota a partir de datos ingresados por el usuario
    private static void createNote() {
        System.out.println("\n--- Crear una nueva nota ---");
        System.out.print("Título: "); var title = scanner.nextLine();
        System.out.print("Contenido: "); var content = scanner.nextLine();
        System.out.print("Latitud: "); var lat = Double.parseDouble(scanner.nextLine());
        System.out.print("Longitud: "); var lon = Double.parseDouble(scanner.nextLine());
        try {
            var geoPoint = new GeoPoint(lat, lon); // Ubicación
            // Se crea la nota con un ID único, fecha actual e inicialmente sin adjunto
            var note = new Note(noteCounter++, title, content, geoPoint, Instant.now(), null);
            timeline.addNote(note); // Guardar en el timeline
            System.out.println("✅ Nota creada con éxito.");
        } catch (NumberFormatException e) {
            System.out.println("❌ Entrada no válida. Por favor, ingresa un número.");
        } catch (IllegalArgumentException e) {
            // Captura error si las coordenadas no son válidas
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    // Lista todas las notas existentes
    private static void listNotes() {
        System.out.println("\n--- Notas disponibles ---");
        if (timeline.getNotes().isEmpty()) {
            System.out.println("No hay notas creadas.");
            return;
        }
        // Recorre el mapa de notas e imprime ID, título y contenido
        timeline.getNotes().forEach(
                (id, note) -> System.out.printf("ID: %d | Título: %s | Contenido: %s%n", id, note.title(), note.content())
        );
    }

    // Filtra notas por palabra clave en el título o contenido
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
        // Mostrar las notas que coinciden
        filtered.forEach(n -> System.out.printf("ID: %d | %s | %s%n", n.id(), n.title(), n.content()));
    }

    // Exporta las notas en formato JSON
    private static void exportNotesToJson() {
        var renderer = new Timeline().new Render(); // Renderizador interno de Timeline
        String json = renderer.export();
        System.out.println("\n--- Exportando notas a JSON ---");
        System.out.println(json);
    }

    // Permite al usuario listar las últimas N notas según fecha de creación
    private static void getLatestNotes() {
        try {
            System.out.println("Introduce el número de notas que deseas ver: ");
            int choice = Integer.parseInt(scanner.nextLine().trim());
            var latestNotes = timeline.latest(choice); // Se obtiene con el método latest()

            for (Note note : latestNotes) {
                System.out.println(note.toString()); // Muestra cada nota
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Entrada no válida. Por favor, ingresa un número.");
        }
    }

    // Exporta una nota en formato Markdown
    private static void ShowMd() {
        if (timeline.getNotes().isEmpty()) {
            System.out.println("No hay Md creados.");
            return;
        }
        // ⚠️ Aquí parece que hay un error: usa noteCounter en lugar de un ID válido
        Note nota = timeline.getNote(noteCounter);
        MarkdownExporter objetoMd = new MarkdownExporter(nota, nota.location());
        System.out.println("Md: " + objetoMd.export());
    }

    // Determina en qué región se encuentra un punto geográfico
    private static void where() {
        try {
            System.out.println("Introduce latitud: ");
            double lat = Double.parseDouble(scanner.nextLine());
            System.out.println("Introduce longitud: ");
            double lon = Double.parseDouble(scanner.nextLine());

            var ubicacion = Match.where(new GeoPoint(lat, lon));
            System.out.println("Ubicacion: " + ubicacion);
        } catch (NumberFormatException e) {
            System.out.println("❌ Entrada no válida. Por favor, ingresa un número.");
        }
    }

    // Carga notas de ejemplo en el timeline con distintos tipos de adjuntos
    private static void seedExamples() {
        timeline.addNote(new Note(noteCounter++, "Cádiz", "Playita", new GeoPoint(36.5297, -6.2927), Instant.now(), new Photo("u", 2000, 1000)));
        timeline.addNote(new Note(noteCounter++, "Sevilla", "Triana", new GeoPoint(37.3826, -5.9963), Instant.now(), new Audio("a", 320)));
        timeline.addNote(new Note(noteCounter++, "Córdoba", "Mezquita", new GeoPoint(37.8790, -4.7794), Instant.now(), new Link("http://cordoba", "Oficial")));
        // Ejemplo de Video añadido
        timeline.addNote(new Note(noteCounter++, "Málaga", "Vídeo playa", new GeoPoint(36.7213, -4.4214), Instant.now(), new Video("v", 1920, 1080, 150)));
    }
}
