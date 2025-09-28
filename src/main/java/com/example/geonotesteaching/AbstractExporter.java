package com.example.geonotesteaching;
public abstract sealed class AbstractExporter implements Exporter permits JsonExporter, Timeline.Render {
    public abstract String export();
}