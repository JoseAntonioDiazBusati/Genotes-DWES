package com.example.geonotesteaching.Attachment;

public record Photo(
        String url,
        int width,
        int height) implements Attachment {


}