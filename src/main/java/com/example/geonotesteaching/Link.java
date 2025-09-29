package com.example.geonotesteaching;

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