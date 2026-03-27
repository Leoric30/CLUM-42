package com.clum.clum.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.clum.clum.models.Payment;
import com.clum.clum.models.enums.PaymentStatus;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /** Historial completo de pagos de un usuario */
    List<Payment> findByUserEmailOrderByPaymentDateDesc(String email);

    /** Pagos de un usuario para una cuota concreta */
    List<Payment> findByUserIdAndFeeId(Long userId, Long feeId);

    /** Todos los pagos de una cuota (para que el secretario vea quién pagó) */
    List<Payment> findByFeeIdOrderByPaymentDateDesc(Long feeId);

    /** Pagos pendientes de un usuario */
    List<Payment> findByUserEmailAndStatus(String email, PaymentStatus status);

    /** ¿Ya pagó este usuario esta cuota? */
    boolean existsByUserIdAndFeeIdAndStatus(Long userId, Long feeId, PaymentStatus status);
}
