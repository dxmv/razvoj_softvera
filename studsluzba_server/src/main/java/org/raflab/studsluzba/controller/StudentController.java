package org.raflab.studsluzba.controller;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.ObnovaGodine;
import org.raflab.studsluzba.model.Student;
import org.raflab.studsluzba.model.dto.ObnovaGodineDto;
import org.raflab.studsluzba.model.dto.PolozenPredmetDto;
import org.raflab.studsluzba.model.dto.PredmetDto;
import org.raflab.studsluzba.model.dto.UpisGodineDto;
import org.raflab.studsluzba.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/studenti")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Student create(@RequestBody Student body) {
        return service.create(body);
    }

    @GetMapping("/{id}")
    public Student findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public Student update(@PathVariable Long id, @RequestBody Student body) {
        return service.update(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    // selekcija studenta (njegovih ličnih podataka) preko broja indeksa
    @GetMapping("/by-index/{index}")
    @ResponseStatus(HttpStatus.OK)
    public Student findByIndex(@PathVariable String index) {
        return service.findByIndex(index);
    }

    // selekcija svih položenih ispita za broj indeksa studenta, paginirano
    @GetMapping("/by-index/{index}/passed")
    @ResponseStatus(HttpStatus.OK)
    public Page<PolozenPredmetDto> findPassedByIndex(@PathVariable String index, Pageable pageable) {
        return service.findPassedExamsByIndex(index, pageable);
    }

    // selekcija svih nepoloženih ispita za broj indeksa studenta, paginirano
    @GetMapping("/by-index/{index}/failed")
    @ResponseStatus(HttpStatus.OK)
    public Page<PredmetDto> findFailedByIndex(@PathVariable String index, Pageable pageable) {
        return service.findFailedExamsByIndex(index, pageable);
    }

    // pregled svih upisanih godina za broj indeksa
    @GetMapping("/by-index/{index}/enroll")
    @ResponseStatus(HttpStatus.OK)
    public List<UpisGodineDto> findEnrolledYearsByIndex(@PathVariable String index) {
        return service.findEnrolledYearsByIndex(index);
    }

    // pregled obnovljenih godina za broj indeksa
    @GetMapping("/by-index/{index}/repeated")
    @ResponseStatus(HttpStatus.OK)
    public List<ObnovaGodineDto> findRepeatedYearsByIndex(@PathVariable String index) {
        return service.findRepeatedYearsByIndex(index);
    }

    // selekcija studenata na osnovu imena i/ili prezimena (može samo ime, ili samo prezime ili oba da se unesu), paginirano

    // selekcija svih upisanih studenata koji su završili određenu srednju školu

}
