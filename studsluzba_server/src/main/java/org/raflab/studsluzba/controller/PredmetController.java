package org.raflab.studsluzba.controller;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.Predmet;
import org.raflab.studsluzba.model.dto.PredmetCreateDto;
import org.raflab.studsluzba.model.dto.PredmetDto;
import org.raflab.studsluzba.service.PredmetService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/predmeti")
@RequiredArgsConstructor
public class PredmetController {

    private final PredmetService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Predmet create(@RequestBody Predmet body) {
        return service.create(body);
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public PredmetDto createPredmet(@RequestBody PredmetCreateDto predmetDto) {
        return service.createPredmet(predmetDto);
    }
    @GetMapping("/{id}")
    public Predmet findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public Predmet update(@PathVariable Long id, @RequestBody Predmet body) {
        return service.update(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/by-stud-program")
    @ResponseStatus(HttpStatus.OK)
    public Page<PredmetDto> findByStudProgram(@RequestParam Long studProgramId, Pageable pageable) {
        return service.findPredmetiByStudijskiProgram(studProgramId,pageable);
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<PredmetDto> getAllPredmeti(Pageable pageable) {
        return service.getAllPredmeti(pageable);
    }

    @GetMapping("/{id}/prosecna-ocena")
    @ResponseStatus(HttpStatus.OK)
    public Double getAverageGrade(@PathVariable Long id,
                                  @RequestParam int yearFrom,
                                  @RequestParam int yearTo) {
        return service.getAverageGradeForSubjectInYearRange(id, yearFrom, yearTo);
    }

}
