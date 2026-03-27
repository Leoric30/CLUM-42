package com.clum.clum.dto;

/**
 * Cuerpo del request para POST /api/auth/login.
 * Spring deserializa automáticamente el JSON { "email": "...", "password": "..." }.
 */
public record LoginRequest(String email, String password) {}
