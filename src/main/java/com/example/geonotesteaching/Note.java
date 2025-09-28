package com.example.geonotesteaching;

import java.time.Instant;

public record Note(long id, String title, String content, GeoPoint location, Instant createdAt, Attachment attachment) {
    public Note {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("title requerido");
        if (content == null) content = "";
        if (location == null) throw new IllegalArgumentException("location requerido");
        if (createdAt == null) createdAt = Instant.now();
    }
}