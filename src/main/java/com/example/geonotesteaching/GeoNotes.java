package com.example.geonotesteaching;

import java.time.Instant;
import java.util.Scanner;

public class GeoNotes {
    private final static Timeline timeline = new Timeline();
    private final static Scanner scanner = new Scanner(System.in);
    private static long noteCounter = 1;

    public static void main(String[] args) {
        if (args != null && args.length > 0 && "examples".equalsIgnoreCase(args[0])) { seedExamples(); exportNotesToJson(); return; }
        System.out.println("--------------------------------------");
        System.out.println("  📝 Bienvenid@ a la aplicación GeoNotes");
        System.out.println("--------------------------------------");
        boolean running = true;
        while (running) {
            printMenu();
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                switch (choice) {
                    case 1 -> createNote();
                    case 2 -> listNotes();
                    case 3 -> filterNotes();
                    case 4 -> exportNotesToJson();
                    case 5 -> running = false;
                    default -> System.out.println("❌ Opción no válida. Inténtalo de nuevo.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Entrada no válida. Por favor, ingresa un número.");
            }
        }
        System.out.println("¡Gracias por usar GeoNotes! 👋");
    }

    private static void printMenu() {
        System.out.println("\n--- Menú ---");
        System.out.println("1. Crear una nueva nota");
        System.out.println("2. Listar todas las notas");
        System.out.println("3. Filtrar notas por palabra clave");
        System.out.println("4. Exportar notas a JSON (Text Blocks)");
        System.out.println("5. Salir");
        System.out.print("Elige una opción: ");
    }

    private static void createNote() {
        System.out.println("\n--- Crear una nueva nota ---");
        System.out.print("Título: "); var title = scanner.nextLine();
        System.out.print("Contenido: "); var content = scanner.nextLine();
        System.out.print("Latitud: "); var lat = Double.parseDouble(scanner.nextLine());
        System.out.print("Longitud: "); var lon = Double.parseDouble(scanner.nextLine());
        try {
            var geoPoint = new GeoPoint(lat, lon);
            var note = new Note(noteCounter++, title, content, geoPoint, Instant.now(), null);
            timeline.addNote(note);
            System.out.println("✅ Nota creada con éxito.");
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private static void listNotes() {
        System.out.println("\n--- Notas disponibles ---");
        if (timeline.getNotes().isEmpty()) { System.out.println("No hay notas creadas."); return; }
        timeline.getNotes().forEach((id, note) -> System.out.printf("ID: %d | Título: %s | Contenido: %s%n", id, note.title(), note.content()));
    }

    private static void filterNotes() {
        System.out.print("\nIntroduce la palabra clave para filtrar: "); var keyword = scanner.nextLine();
        System.out.println("\n--- Resultados de búsqueda ---");
        var filtered = timeline.getNotes().values().stream().filter(n -> n.title().contains(keyword) || n.content().contains(keyword)).toList();
        if (filtered.isEmpty()) { System.out.println("No se encontraron notas con: " + keyword); return; }
        filtered.forEach(n -> System.out.printf("ID: %d | %s | %s%n", n.id(), n.title(), n.content()));
    }

    private static void exportNotesToJson() {
        var renderer = new Timeline().new Render();
        String json = renderer.export();
        System.out.println("\n--- Exportando notas a JSON ---");
        System.out.println(json);
    }

    private static void seedExamples() {
        timeline.addNote(new Note(noteCounter++, "Cádiz", "Playita", new GeoPoint(36.5297, -6.2927), Instant.now(), new Photo("u", 2000, 1000)));
        timeline.addNote(new Note(noteCounter++, "Sevilla", "Triana", new GeoPoint(37.3826, -5.9963), Instant.now(), new Audio("a", 320)));
        timeline.addNote(new Note(noteCounter++, "Córdoba", "Mezquita", new GeoPoint(37.8790, -4.7794), Instant.now(), new Link("http://cordoba", "Oficial")));
    }
}