package com.clum.clum.dto;

/**
 * Respuesta de POST /api/auth/register.
 */
public record RegisterResponse(boolean registered, String email) {}
