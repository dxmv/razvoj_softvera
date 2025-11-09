package org.raflab.studsluzba.controller;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.IzlazakIspit;
import org.raflab.studsluzba.model.dto.IzlazakIspitDto;
import org.raflab.studsluzba.service.IzlazakIspitService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/izlasci-na-ispit")
@RequiredArgsConstructor
public class IzlazakIspitController {

    private final IzlazakIspitService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IzlazakIspit create(@RequestBody IzlazakIspit body) {
        return service.create(body);
    }

    @GetMapping("/{id}")
    public IzlazakIspit findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public IzlazakIspit update(@PathVariable Long id, @RequestBody IzlazakIspit body) {
        return service.update(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PostMapping("/dodaj-izlazak")
    @ResponseStatus(HttpStatus.CREATED)
    public IzlazakIspitDto dodajIzlazak(@RequestBody IzlazakIspitDto izlazakDto) {
        return service.dodajIzlazak(izlazakDto);
    }
}
