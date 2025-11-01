package org.raflab.studsluzba.controller;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.StudentPredispitnaObaveza;
import org.raflab.studsluzba.service.StudentPredispitnaObavezaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student-predispitne-obaveze")
@RequiredArgsConstructor
public class StudentPredispitnaObavezaController {

    private final StudentPredispitnaObavezaService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudentPredispitnaObaveza create(@RequestBody StudentPredispitnaObaveza body) {
        return service.create(body);
    }

    @GetMapping("/{id}")
    public StudentPredispitnaObaveza findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public StudentPredispitnaObaveza update(@PathVariable Long id, @RequestBody StudentPredispitnaObaveza body) {
        return service.update(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
