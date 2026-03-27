package com.clum.clum.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

/**
 * Tabla: cuotas
 * Define QUÉ se cobra en cada club (concepto y monto).
 * Los pagos individuales se registran en la tabla Payment.
 */
@Entity
@Table(name = "cuotas")
@Getter
@Setter
@NoArgsConstructor
public class Fee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "club_id")
    private Club club;

    @Column(name = "descripcion", nullable = false)
    private String description; // "Cuota mensual", "Cuota de inscripción", "Evento especial"

    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /** Fecha límite para pagar esta cuota */
    @Column(name = "fecha_vencimiento")
    private LocalDate dueDate;

    @Column(name = "activo", nullable = false)
    private boolean active = true;
}
