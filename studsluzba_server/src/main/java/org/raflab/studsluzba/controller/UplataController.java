package org.raflab.studsluzba.controller;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.Uplata;
import org.raflab.studsluzba.model.dto.CreateUplataRequest;
import org.raflab.studsluzba.service.UplataService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/uplate")
@RequiredArgsConstructor
public class UplataController {

    private final UplataService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Uplata create(@RequestBody Uplata body) {
        return service.create(body);
    }

    @GetMapping("/{id}")
    public Uplata findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping("/student/{studentId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Uplata createWithCurrentRate(@PathVariable Long studentId,
                                        @RequestBody CreateUplataRequest request) {
        return service.createWithCurrentRate(studentId, request);
    }

    @PutMapping("/{id}")
    public Uplata update(@PathVariable Long id, @RequestBody Uplata body) {
        return service.update(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
