package org.raflab.studsluzba.controller;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.Ispit;
import org.raflab.studsluzba.model.dto.IzlazakIspitDto;
import org.raflab.studsluzba.model.dto.RezultatIspitaStudentDto;
import org.raflab.studsluzba.service.IspitService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ispiti")
@RequiredArgsConstructor
public class IspitController {

    private final IspitService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Ispit create(@RequestBody Ispit body) {
        return service.create(body);
    }

    @GetMapping("/{id}")
    public Ispit findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public Ispit update(@PathVariable Long id, @RequestBody Ispit body) {
        return service.update(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
    @GetMapping("/{ispitId}/prosecna-ocena")
    @ResponseStatus(HttpStatus.OK)
    public Double getProsecnaOcena(@PathVariable Long ispitId) {
        return service.getProsecnaOcenaNaIspitu(ispitId);



    }
    @GetMapping("/{ispitId}/rezultati")
    @ResponseStatus(HttpStatus.OK)
    public List<RezultatIspitaStudentDto> getRezultatiIspita(@PathVariable Long ispitId) {
        return service.getRezultatiIspitaSorted(ispitId);
    }
}
