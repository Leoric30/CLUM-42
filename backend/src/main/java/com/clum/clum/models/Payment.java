package com.clum.clum.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.clum.clum.models.enums.PaymentStatus;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

/**
 * Tabla: pagos
 * Historial de pagos individuales de cuotas.
 * Cada fila representa un pago de un usuario específico por una cuota concreta.
 */
@Entity
@Table(name = "pagos")
@Getter
@Setter
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Qué cuota se está pagando */
    @ManyToOne(optional = false)
    @JoinColumn(name = "cuota_id")
    private Fee fee;

    /** Quién paga */
    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    private User user;

    /** Monto real pagado (puede diferir del monto original en la cuota) */
    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "fecha_pago")
    private LocalDate paymentDate;

    /** URL del comprobante en S3 */
    @Column(name = "comprobante")
    private String receipt;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private PaymentStatus status = PaymentStatus.PENDIENTE;

    /** Secretario o director que validó / registró el pago */
    @ManyToOne
    @JoinColumn(name = "registrado_por_id")
    private User registeredBy;
}
