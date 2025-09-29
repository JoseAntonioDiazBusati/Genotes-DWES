package com.example.geonotesteaching;

public sealed interface Exporter permits AbstractExporter, MarkdownExporter, JsonExporter, Timeline.Render {
    String export();
}