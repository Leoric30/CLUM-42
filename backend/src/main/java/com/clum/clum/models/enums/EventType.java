package com.clum.clum.models.enums;

/**
 * Clasifica la naturaleza de un evento del club.
 *
 * JUNTA → Reunión formal con agenda: toma de decisiones, votaciones, etc.
 * ACTIVIDAD → Actividad social, deportiva o cultural del club.
 * REUNION → Reunión informal o de planificación sin acta obligatoria.
 * OTRO → Evento que no encaja en las categorías anteriores.
 */
public enum EventType {
    JUNTA,
    ACTIVIDAD,
    REUNION,
    OTRO
}
