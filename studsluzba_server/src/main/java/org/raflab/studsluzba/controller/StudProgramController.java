package org.raflab.studsluzba.controller;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.StudProgram;
import org.raflab.studsluzba.service.StudProgramService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stud-programi")
@RequiredArgsConstructor
public class StudProgramController {

    private final StudProgramService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudProgram create(@RequestBody StudProgram body) {
        return service.create(body);
    }

    @GetMapping("/{id}")
    public StudProgram findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public StudProgram update(@PathVariable Long id, @RequestBody StudProgram body) {
        return service.update(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
