package org.raflab.studsluzba.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.raflab.studsluzba.model.*;
import org.raflab.studsluzba.model.dto.*;
import org.raflab.studsluzba.repositories.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;
    @Mock
    private IndeksService indeksService;
    @Mock
    private PolozenPredmetRepository polozenPredmetRepository;
    @Mock
    private StudentPredmetRepository studentPredmetRepository;
    @Mock
    private UpisGodineRepository upisGodineRepository;
    @Mock
    private SkolskaGodinaRepository skolskaGodinaRepository;
    @Mock
    private ObnovaGodineRepository obnovaGodineRepository;
    @Mock
    private SrednjasSkolaRepository srednjasSkolaRepository;
    @Mock
    private PredmetRepository predmetRepository;
    @Mock
    private NastavnikPredmetRepository nastavnikPredmetRepository;

    @InjectMocks
    private StudentService studentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    // 1. Selekcija studenta preko broja indeksa

    @Test
    void testFindByIndex_Success() {
        // Arrange
        // Koristimo format RI012021 umesto 2021/0001
        String index = "RI012021";
        Student student = new Student();
        student.setIme("Marko");
        
        Indeks indeks = new Indeks();
        indeks.setStudent(student);

        when(indeksService.findByShort(index)).thenReturn(indeks);

        // Act
        Student result = studentService.findByIndex(index);

        // Assert
        assertNotNull(result);
        assertEquals("Marko", result.getIme());
        verify(indeksService, times(1)).findByShort(index);
    }

    @Test
    void testFindByIndex_NotFound() {
        // Arrange
        String index = "invalid-index";
        when(indeksService.findByShort(index)).thenReturn(null);

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> studentService.findByIndex(index));
        verify(indeksService, times(1)).findByShort(index);
    }

    // 2. Selekcija svih položenih ispita za broj indeksa studenta

    @Test
    void testFindPassedExamsByIndex_Success() {
        // Arrange
        String index = "RI012021";
        Pageable pageable = Pageable.unpaged();
        Indeks indeks = new Indeks();
        
        PolozenPredmet pp = new PolozenPredmet();
        pp.setOcena(10);
        pp.setPredmet(new Predmet()); // Ensure predmet is not null for mapper
        pp.getPredmet().setNaziv("Matematika");

        Page<PolozenPredmet> page = new PageImpl<>(Collections.singletonList(pp));

        when(indeksService.findByShort(index)).thenReturn(indeks);
        when(polozenPredmetRepository.findByStudentskiIndeks(indeks, pageable)).thenReturn(page);

        // Act
        Page<PolozenPredmetDto> result = studentService.findPassedExamsByIndex(index, pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(10, result.getContent().get(0).getOcena());
        verify(indeksService, times(1)).findByShort(index);
        verify(polozenPredmetRepository, times(1)).findByStudentskiIndeks(indeks, pageable);
    }

    @Test
    void testFindPassedExamsByIndex_IndexNotFound() {
        // Arrange
        String index = "invalid-index";
        Pageable pageable = Pageable.unpaged();
        when(indeksService.findByShort(index)).thenReturn(null);

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> studentService.findPassedExamsByIndex(index, pageable));
    }

    // 3. Selekcija svih nepoloženih ispita za broj indeksa studenta

    @Test
    void testFindFailedExamsByIndex_Success() {
        // Arrange
        String index = "RI012021";
        Pageable pageable = Pageable.unpaged();
        Indeks indeks = new Indeks();
        
        Predmet predmet = new Predmet();
        predmet.setNaziv("Fizika");
        
        Page<Predmet> page = new PageImpl<>(Collections.singletonList(predmet));

        when(indeksService.findByShort(index)).thenReturn(indeks);
        when(studentPredmetRepository.findUnpassedSubjectsByIndeks(indeks, pageable)).thenReturn(page);

        // Act
        Page<PredmetDto> result = studentService.findFailedExamsByIndex(index, pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals("Fizika", result.getContent().get(0).getNaziv());
        verify(indeksService, times(1)).findByShort(index);
    }

    @Test
    void testFindFailedExamsByIndex_IndexNotFound() {
        // Arrange
        String index = "invalid-index";
        Pageable pageable = Pageable.unpaged();
        when(indeksService.findByShort(index)).thenReturn(null);

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> studentService.findFailedExamsByIndex(index, pageable));
    }

    // 4. Pregled svih upisanih godina za broj indeksa

    @Test
    void testFindEnrolledYearsByIndex_Success() {
        // Arrange
        String index = "RI012021";
        Indeks indeks = new Indeks();
        
        UpisGodine upis = new UpisGodine();
        upis.setGodinaStudija(2);
        
        List<UpisGodine> list = Collections.singletonList(upis);

        when(indeksService.findByShort(index)).thenReturn(indeks);
        when(upisGodineRepository.findByStudentskiIndeks(indeks)).thenReturn(list);

        // Act
        List<UpisGodineDto> result = studentService.findEnrolledYearsByIndex(index);

        // Assert
        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getGodinaStudija());
        verify(indeksService, times(1)).findByShort(index);
    }

    @Test
    void testFindEnrolledYearsByIndex_IndexNotFound() {
        // Arrange
        String index = "invalid-index";
        when(indeksService.findByShort(index)).thenReturn(null);

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> studentService.findEnrolledYearsByIndex(index));
    }

    // 5. Pregled obnovljenih godina za broj indeksa

    @Test
    void testFindRepeatedYearsByIndex_Success() {
        // Arrange
        String index = "RI012021";
        Indeks indeks = new Indeks();
        
        ObnovaGodine obnova = new ObnovaGodine();
        obnova.setGodinaStudija(1);
        
        List<ObnovaGodine> list = Collections.singletonList(obnova);

        when(indeksService.findByShort(index)).thenReturn(indeks);
        when(obnovaGodineRepository.findByStudentskiIndeks(indeks)).thenReturn(list);

        // Act
        List<ObnovaGodineDto> result = studentService.findRepeatedYearsByIndex(index);

        // Assert
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getGodinaStudija());
        verify(indeksService, times(1)).findByShort(index);
    }

    @Test
    void testFindRepeatedYearsByIndex_IndexNotFound() {
        // Arrange
        String index = "invalid-index";
        when(indeksService.findByShort(index)).thenReturn(null);

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> studentService.findRepeatedYearsByIndex(index));
    }

    // 6. Selekcija studenata na osnovu imena i/ili prezimena

    @Test
    void testSearch_ByBoth_Success() {
        // Arrange
        String ime = "Jovan";
        String prezime = "Jovanovic";
        Pageable pageable = Pageable.unpaged();
        
        Student student = new Student();
        student.setIme(ime);
        student.setPrezime(prezime);
        
        Page<Student> page = new PageImpl<>(Collections.singletonList(student));

        when(studentRepository.searchByImeAndPrezime(ime, prezime, pageable)).thenReturn(page);

        // Act
        Page<StudentDto> result = studentService.search(ime, prezime, pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(ime, result.getContent().get(0).getIme());
        verify(studentRepository, times(1)).searchByImeAndPrezime(ime, prezime, pageable);
    }

    @Test
    void testSearch_OnlyName() {
        // Arrange
        String ime = "Jovan";
        Pageable pageable = Pageable.unpaged();
        
        Student student = new Student();
        student.setIme(ime);
        
        Page<Student> page = new PageImpl<>(Collections.singletonList(student));

        when(studentRepository.searchByImeAndPrezime(ime, null, pageable)).thenReturn(page);

        // Act
        Page<StudentDto> result = studentService.search(ime, null, pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        verify(studentRepository, times(1)).searchByImeAndPrezime(ime, null, pageable);
    }

    @Test
    void testSearch_OnlySurname() {
        // Arrange
        String prezime = "Jovanovic";
        Pageable pageable = Pageable.unpaged();
        
        Student student = new Student();
        student.setPrezime(prezime);
        
        Page<Student> page = new PageImpl<>(Collections.singletonList(student));

        when(studentRepository.searchByImeAndPrezime(null, prezime, pageable)).thenReturn(page);

        // Act
        Page<StudentDto> result = studentService.search(null, prezime, pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        verify(studentRepository, times(1)).searchByImeAndPrezime(null, prezime, pageable);
    }

    // 7. Selekcija svih upisanih studenata koji su završili određenu srednju školu

    @Test
    void testFindEnrolledByHighSchool_Success() {
        // Arrange
        Long skolaId = 1L;
        Student student = new Student();
        student.setIme("Petar");
        
        List<Student> list = Collections.singletonList(student);

        when(srednjasSkolaRepository.existsById(skolaId)).thenReturn(true);
        when(studentRepository.findEnrolledByHighSchool(skolaId)).thenReturn(list);

        // Act
        List<StudentDto> result = studentService.findEnrolledByHighSchool(skolaId);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Petar", result.get(0).getIme());
        verify(srednjasSkolaRepository, times(1)).existsById(skolaId);
        verify(studentRepository, times(1)).findEnrolledByHighSchool(skolaId);
    }

    @Test
    void testFindEnrolledByHighSchool_SchoolNotFound() {
        // Arrange
        Long skolaId = 999L;
        when(srednjasSkolaRepository.existsById(skolaId)).thenReturn(false);

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> studentService.findEnrolledByHighSchool(skolaId));
        verify(srednjasSkolaRepository, times(1)).existsById(skolaId);
        verify(studentRepository, never()).findEnrolledByHighSchool(any());
    }

    // 8. Upis godine (enroll)

    @Test
    void testEnroll_Success() {
        // Arrange
        String index = "RI012021";
        UpisGodineEnrollmentRequest request = new UpisGodineEnrollmentRequest();
        request.setGodinaStudija(2);

        Indeks indeks = new Indeks();
        indeks.setStudijskiProgram(new StudProgram());
        indeks.getStudijskiProgram().setId(1L);

        SkolskaGodina aktivnaGodina = new SkolskaGodina();
        aktivnaGodina.setId(1L);

        List<Predmet> nepolozeni = Collections.emptyList();
        List<NastavnikPredmet> predmetiZaSlusanje = Collections.singletonList(new NastavnikPredmet());
        
        UpisGodine savedUpis = new UpisGodine();
        savedUpis.setGodinaStudija(2);
        savedUpis.setSkolskaGodina(aktivnaGodina);

        when(indeksService.findByShort(index)).thenReturn(indeks);
        when(skolskaGodinaRepository.findAktivnaSkolskaGodina()).thenReturn(java.util.Optional.of(aktivnaGodina));
        when(upisGodineRepository.existsByStudentskiIndeksAndSkolskaGodina(indeks, aktivnaGodina)).thenReturn(false);
        when(studentPredmetRepository.findUnpassedSubjectsByIndeks(eq(indeks), any(Pageable.class)))
                .thenReturn(new PageImpl<>(nepolozeni));
        when(nastavnikPredmetRepository.findBySkolskaGodinaIdAndPredmetStudijskiProgramIdAndPredmetSemestarIn(
                eq(1L), eq(1L), anyList())).thenReturn(predmetiZaSlusanje);
        when(upisGodineRepository.save(any(UpisGodine.class))).thenReturn(savedUpis);

        // Act
        UpisGodineDto result = studentService.enroll(index, request);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getGodinaStudija());
        verify(upisGodineRepository, times(1)).save(any(UpisGodine.class));
        verify(studentPredmetRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testEnroll_IndexNotFound() {
        // Arrange
        String index = "invalid-index";
        when(indeksService.findByShort(index)).thenReturn(null);

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> studentService.enroll(index, new UpisGodineEnrollmentRequest()));
    }

    @Test
    void testEnroll_NoActiveSchoolYear() {
        // Arrange
        String index = "RI012021";
        when(indeksService.findByShort(index)).thenReturn(new Indeks());
        when(skolskaGodinaRepository.findAktivnaSkolskaGodina()).thenReturn(java.util.Optional.empty());

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> studentService.enroll(index, new UpisGodineEnrollmentRequest()));
    }

    @Test
    void testEnroll_AlreadyEnrolled() {
        // Arrange
        String index = "RI012021";
        Indeks indeks = new Indeks();
        SkolskaGodina aktivnaGodina = new SkolskaGodina();

        when(indeksService.findByShort(index)).thenReturn(indeks);
        when(skolskaGodinaRepository.findAktivnaSkolskaGodina()).thenReturn(java.util.Optional.of(aktivnaGodina));
        when(upisGodineRepository.existsByStudentskiIndeksAndSkolskaGodina(indeks, aktivnaGodina)).thenReturn(true);

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> studentService.enroll(index, new UpisGodineEnrollmentRequest()));
    }

    @Test
    void testEnroll_InvalidYear() {
        // Arrange
        String index = "RI012021";
        UpisGodineEnrollmentRequest request = new UpisGodineEnrollmentRequest();
        request.setGodinaStudija(0); // Invalid year

        Indeks indeks = new Indeks();
        SkolskaGodina aktivnaGodina = new SkolskaGodina();

        when(indeksService.findByShort(index)).thenReturn(indeks);
        when(skolskaGodinaRepository.findAktivnaSkolskaGodina()).thenReturn(java.util.Optional.of(aktivnaGodina));
        when(upisGodineRepository.existsByStudentskiIndeksAndSkolskaGodina(indeks, aktivnaGodina)).thenReturn(false);

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> studentService.enroll(index, request));
    }

    @Test
    void testEnroll_NoSubjectsFound() {
        // Arrange
        String index = "RI012021";
        UpisGodineEnrollmentRequest request = new UpisGodineEnrollmentRequest();
        request.setGodinaStudija(2);

        Indeks indeks = new Indeks();
        indeks.setStudijskiProgram(new StudProgram());
        indeks.getStudijskiProgram().setId(1L);
        
        SkolskaGodina aktivnaGodina = new SkolskaGodina();
        aktivnaGodina.setId(1L);

        when(indeksService.findByShort(index)).thenReturn(indeks);
        when(skolskaGodinaRepository.findAktivnaSkolskaGodina()).thenReturn(java.util.Optional.of(aktivnaGodina));
        when(upisGodineRepository.existsByStudentskiIndeksAndSkolskaGodina(indeks, aktivnaGodina)).thenReturn(false);
        when(studentPredmetRepository.findUnpassedSubjectsByIndeks(eq(indeks), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));
        
        // Return empty list of subjects for next year
        when(nastavnikPredmetRepository.findBySkolskaGodinaIdAndPredmetStudijskiProgramIdAndPredmetSemestarIn(
                eq(1L), eq(1L), anyList())).thenReturn(Collections.emptyList());

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> studentService.enroll(index, request));
    }
}
