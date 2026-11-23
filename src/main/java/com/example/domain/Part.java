package com.example.domain;

public record Part(
        String partNumber,
        String description) {

    @Override
    public String toString() {
        return partNumber + "  " + description;
    }
}