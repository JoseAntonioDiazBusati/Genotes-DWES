package com.example.geonotesteaching;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

final class Timeline {
    private final Map<Long, Note> notes = new LinkedHashMap<>();

    public void addNote(Note note) { notes.put(note.id(), note); }
    public Note getNote(long id) { return notes.get(id); }
    public Map<Long, Note> getNotes() { return notes; }

    public final class Render extends AbstractExporter implements Exporter {
        @Override public String export() {
            var notesList = notes.values().stream()
                .map(note -> """
                        {
                          "id": %d,
                          "title": "%s",
                          "content": "%s",
                          "location": { "lat": %f, "lon": %f },
                          "createdAt": "%s"
                        }
                        """.formatted(
                            note.id(), note.title(), note.content(),
                            note.location().lat(), note.location().lon(),
                            note.createdAt()))
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.joining(",\n"));
            return """
                    { "notes": [ %s ] }
                    """.formatted(notesList);
        }
    }
}