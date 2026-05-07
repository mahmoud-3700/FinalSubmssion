package com.smarthome.persistence.dao;


// Immutable user row returned from users queries.
public record User(String userId, String name, String pin, String role) {}
