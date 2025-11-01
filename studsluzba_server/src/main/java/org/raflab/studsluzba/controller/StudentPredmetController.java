package org.raflab.studsluzba.controller;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.StudentPredmet;
import org.raflab.studsluzba.service.StudentPredmetService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student-predmeti")
@RequiredArgsConstructor
public class StudentPredmetController {

    private final StudentPredmetService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudentPredmet create(@RequestBody StudentPredmet body) {
        return service.create(body);
    }

    @GetMapping("/{id}")
    public StudentPredmet findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public StudentPredmet update(@PathVariable Long id, @RequestBody StudentPredmet body) {
        return service.update(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
