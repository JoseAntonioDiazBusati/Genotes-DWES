package com.example.geonotesteaching;
public sealed interface Exporter permits AbstractExporter, JsonExporter, Timeline.Render {
    String export();
}