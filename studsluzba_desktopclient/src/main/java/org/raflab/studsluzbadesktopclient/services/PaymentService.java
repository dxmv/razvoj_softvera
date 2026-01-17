package org.raflab.studsluzbadesktopclient.services;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.dto.CreateUplataRequest;
import org.raflab.studsluzba.model.dto.RemainingTuitionDto;
import org.raflab.studsluzba.model.dto.UplataDto;
import org.raflab.studsluzbadesktopclient.client.PaymentApiClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service layer for payment-related business logic.
 * Delegates HTTP operations to PaymentApiClient.
 * Contains validation, business rules, and orchestration.
 */
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentApiClient paymentApiClient;

    /**
     * Find payments for student asynchronously.
     */
    public Flux<UplataDto> findPaymentsForStudent(Long studentId) {
        if (studentId == null) {
            return Flux.empty();
        }
        return paymentApiClient.findPaymentsForStudentAsync(studentId);
    }

    /**
     * Get remaining tuition balance asynchronously.
     */
    public Mono<RemainingTuitionDto> getRemainingTuition(Long studentId) {
        if (studentId == null) {
            return Mono.empty();
        }
        return paymentApiClient.getRemainingTuitionAsync(studentId);
    }

    /**
     * Create payment asynchronously.
     * Validates input before making API call.
     */
    public Mono<UplataDto> createPayment(Long studentId, CreateUplataRequest request) {
        if (studentId == null) {
            return Mono.error(new IllegalArgumentException("studentId je obavezan"));
        }
        if (request == null) {
            return Mono.error(new IllegalArgumentException("Request je obavezan"));
        }
        if (request.getIznosUDinarima() == null) {
            return Mono.error(new IllegalArgumentException("Iznos je obavezan"));
        }
        return paymentApiClient.createPaymentAsync(studentId, request);
    }
}
