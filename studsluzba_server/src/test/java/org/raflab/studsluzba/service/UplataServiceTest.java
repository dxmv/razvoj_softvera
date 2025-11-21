package org.raflab.studsluzba.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.raflab.studsluzba.client.ExchangeRateClient;
import org.raflab.studsluzba.model.SkolskaGodina;
import org.raflab.studsluzba.model.Student;
import org.raflab.studsluzba.model.Uplata;
import org.raflab.studsluzba.model.dto.CreateUplataRequest;
import org.raflab.studsluzba.model.dto.ExchangeRateDto;
import org.raflab.studsluzba.model.dto.RemainingTuitionDto;
import org.raflab.studsluzba.model.dto.UplataDto;
import org.raflab.studsluzba.repositories.SkolskaGodinaRepository;
import org.raflab.studsluzba.repositories.StudentRepository;
import org.raflab.studsluzba.repositories.UplataRepository;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UplataServiceTest {

    @Mock
    private UplataRepository uplataRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private SkolskaGodinaRepository skolskaGodinaRepository;
    @Mock
    private ExchangeRateClient exchangeRateClient;

    @InjectMocks
    private UplataService uplataService;

    private Student testStudent;
    private SkolskaGodina aktivnaGodina;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        testStudent = new Student();
        testStudent.setId(1L);
        aktivnaGodina = new SkolskaGodina();
        aktivnaGodina.setId(99L);
    }

    // ------------------ createWithCurrentRate ------------------

    @Test
    void testCreateWithCurrentRate_Success() {
        CreateUplataRequest request = CreateUplataRequest.builder()
                .datumUplate(LocalDate.of(2024, 5, 20))
                .iznosUDinarima(new BigDecimal("120000"))
                .build();

        ExchangeRateDto rateDto = new ExchangeRateDto();
        rateDto.setExchangeMiddle(new BigDecimal("117.1234"));

        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(skolskaGodinaRepository.findAktivnaSkolskaGodina()).thenReturn(Optional.of(aktivnaGodina));
        when(exchangeRateClient.fetchTodayEurRate()).thenReturn(rateDto);
        when(uplataRepository.save(any(Uplata.class))).thenAnswer(invocation -> {
            Uplata saved = invocation.getArgument(0);
            saved.setId(55L);
            return saved;
        });

        UplataDto dto = uplataService.createWithCurrentRate(1L, request);

        assertEquals(55L, dto.getId());
        assertEquals(new BigDecimal("120000"), dto.getIznosUDinarima());
        assertEquals(new BigDecimal("117.1234"), dto.getSrednjiKurs());
        assertEquals(1L, dto.getStudentId());
        assertEquals(99L, dto.getSkolskaGodinaId());

        ArgumentCaptor<Uplata> captor = ArgumentCaptor.forClass(Uplata.class);
        verify(uplataRepository).save(captor.capture());
        Uplata stored = captor.getValue();
        assertEquals(LocalDate.of(2024, 5, 20), stored.getDatumUplate());
        assertEquals(request.getIznosUDinarima(), stored.getIznosUDinarima());
        assertEquals(rateDto.getExchangeMiddle(), stored.getSrednjiKurs());
    }

    @Test
    void testCreateWithCurrentRate_DefaultsDateWhenAbsent() {
        CreateUplataRequest request = CreateUplataRequest.builder()
                .iznosUDinarima(new BigDecimal("50000"))
                .build();

        ExchangeRateDto rateDto = new ExchangeRateDto();
        rateDto.setExchangeMiddle(new BigDecimal("118.00"));

        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(skolskaGodinaRepository.findAktivnaSkolskaGodina()).thenReturn(Optional.of(aktivnaGodina));
        when(exchangeRateClient.fetchTodayEurRate()).thenReturn(rateDto);
        when(uplataRepository.save(any(Uplata.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LocalDate today = LocalDate.now();

        UplataDto dto = uplataService.createWithCurrentRate(1L, request);

        assertEquals(new BigDecimal("118.00"), dto.getSrednjiKurs());

        ArgumentCaptor<Uplata> captor = ArgumentCaptor.forClass(Uplata.class);
        verify(uplataRepository).save(captor.capture());
        LocalDate storedDate = captor.getValue().getDatumUplate();
        assertEquals(today, storedDate);
    }

    @Test
    void testCreateWithCurrentRate_StudentNotFound() {
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        CreateUplataRequest request = CreateUplataRequest.builder()
                .iznosUDinarima(new BigDecimal("500"))
                .build();

        assertThrows(ResponseStatusException.class,
                () -> uplataService.createWithCurrentRate(1L, request));
    }

    @Test
    void testCreateWithCurrentRate_NoActiveYear() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(skolskaGodinaRepository.findAktivnaSkolskaGodina()).thenReturn(Optional.empty());

        CreateUplataRequest request = CreateUplataRequest.builder()
                .iznosUDinarima(new BigDecimal("500"))
                .build();

        assertThrows(ResponseStatusException.class,
                () -> uplataService.createWithCurrentRate(1L, request));
    }

    @Test
    void testCreateWithCurrentRate_InvalidAmountNull() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(skolskaGodinaRepository.findAktivnaSkolskaGodina()).thenReturn(Optional.of(aktivnaGodina));

        CreateUplataRequest request = CreateUplataRequest.builder()
                .iznosUDinarima(null)
                .build();

        assertThrows(ResponseStatusException.class,
                () -> uplataService.createWithCurrentRate(1L, request));
        verifyNoInteractions(exchangeRateClient);
        verify(uplataRepository, never()).save(any());
    }

    @Test
    void testCreateWithCurrentRate_InvalidAmountNonPositive() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(skolskaGodinaRepository.findAktivnaSkolskaGodina()).thenReturn(Optional.of(aktivnaGodina));

        CreateUplataRequest request = CreateUplataRequest.builder()
                .iznosUDinarima(new BigDecimal("0"))
                .build();

        assertThrows(ResponseStatusException.class,
                () -> uplataService.createWithCurrentRate(1L, request));
        verifyNoInteractions(exchangeRateClient);
        verify(uplataRepository, never()).save(any());
    }

    @Test
    void testCreateWithCurrentRate_InvalidExchangeRate() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(skolskaGodinaRepository.findAktivnaSkolskaGodina()).thenReturn(Optional.of(aktivnaGodina));

        ExchangeRateDto invalidRate = new ExchangeRateDto();
        invalidRate.setExchangeMiddle(null);
        when(exchangeRateClient.fetchTodayEurRate()).thenReturn(invalidRate);

        CreateUplataRequest request = CreateUplataRequest.builder()
                .iznosUDinarima(new BigDecimal("1000"))
                .build();

        assertThrows(ResponseStatusException.class,
                () -> uplataService.createWithCurrentRate(1L, request));
        verify(uplataRepository, never()).save(any());
    }

    // ------------------ getRemainingTuition ------------------

    @Test
    void testGetRemainingTuition_Success() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(skolskaGodinaRepository.findAktivnaSkolskaGodina()).thenReturn(Optional.of(aktivnaGodina));

        Uplata first = new Uplata();
        first.setIznosUDinarima(new BigDecimal("120000"));
        first.setSrednjiKurs(new BigDecimal("120"));

        Uplata second = new Uplata();
        second.setIznosUDinarima(new BigDecimal("150000"));
        second.setSrednjiKurs(new BigDecimal("100"));

        when(uplataRepository.findByStudentIdAndSkolskaGodinaId(1L, 99L))
                .thenReturn(Arrays.asList(first, second));

        ExchangeRateDto rateDto = new ExchangeRateDto();
        rateDto.setExchangeMiddle(new BigDecimal("117.50"));
        when(exchangeRateClient.fetchTodayEurRate()).thenReturn(rateDto);

        RemainingTuitionDto dto = uplataService.getRemainingTuition(1L);

        assertEquals(new BigDecimal("500.00"), dto.getPreostaloEur());
        assertEquals(new BigDecimal("58750.00"), dto.getPreostaloRsd());
        assertEquals(new BigDecimal("117.50"), dto.getPrimenjeniKurs());
    }

    @Test
    void testGetRemainingTuition_StudentNotFound() {
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> uplataService.getRemainingTuition(1L));
    }

    @Test
    void testGetRemainingTuition_NoActiveYear() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(skolskaGodinaRepository.findAktivnaSkolskaGodina()).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> uplataService.getRemainingTuition(1L));
    }

    @Test
    void testGetRemainingTuition_InvalidExchangeRate() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(skolskaGodinaRepository.findAktivnaSkolskaGodina()).thenReturn(Optional.of(aktivnaGodina));
        when(uplataRepository.findByStudentIdAndSkolskaGodinaId(1L, 99L))
                .thenReturn(Collections.emptyList());

        ExchangeRateDto invalidRate = new ExchangeRateDto();
        invalidRate.setExchangeMiddle(BigDecimal.ZERO);
        when(exchangeRateClient.fetchTodayEurRate()).thenReturn(invalidRate);

        assertThrows(ResponseStatusException.class,
                () -> uplataService.getRemainingTuition(1L));
    }

    @Test
    void testGetRemainingTuition_Overpaid() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(skolskaGodinaRepository.findAktivnaSkolskaGodina()).thenReturn(Optional.of(aktivnaGodina));

        Uplata huge = new Uplata();
        huge.setIznosUDinarima(new BigDecimal("400000"));
        huge.setSrednjiKurs(new BigDecimal("100"));
        when(uplataRepository.findByStudentIdAndSkolskaGodinaId(1L, 99L))
                .thenReturn(Collections.singletonList(huge));

        ExchangeRateDto rateDto = new ExchangeRateDto();
        rateDto.setExchangeMiddle(new BigDecimal("115.00"));
        when(exchangeRateClient.fetchTodayEurRate()).thenReturn(rateDto);

        RemainingTuitionDto dto = uplataService.getRemainingTuition(1L);

        assertEquals(BigDecimal.ZERO.setScale(2), dto.getPreostaloEur());
        assertEquals(BigDecimal.ZERO.setScale(2), dto.getPreostaloRsd());
    }

    @Test
    void testGetRemainingTuition_InvalidStoredPaymentRate() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(skolskaGodinaRepository.findAktivnaSkolskaGodina()).thenReturn(Optional.of(aktivnaGodina));

        Uplata broken = new Uplata();
        broken.setIznosUDinarima(new BigDecimal("1000"));
        broken.setSrednjiKurs(BigDecimal.ZERO);

        when(uplataRepository.findByStudentIdAndSkolskaGodinaId(1L, 99L))
                .thenReturn(Collections.singletonList(broken));

        assertThrows(ResponseStatusException.class,
                () -> uplataService.getRemainingTuition(1L));
    }
}
