package com.example.geonotesteaching.Attachment;

public record Video(
        String url,
        int width,
        int height,
        int seconds
) implements Attachment {

}
