package com.example.geonotesteaching;

public record Photo(
        String url,
        int width,
        int height) implements Attachment {


}