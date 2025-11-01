package org.raflab.studsluzba.controller;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.UpisGodine;
import org.raflab.studsluzba.service.UpisGodineService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/upisi-godine")
@RequiredArgsConstructor
public class UpisGodineController {

    private final UpisGodineService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UpisGodine create(@RequestBody UpisGodine body) {
        return service.create(body);
    }

    @GetMapping("/{id}")
    public UpisGodine findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public UpisGodine update(@PathVariable Long id, @RequestBody UpisGodine body) {
        return service.update(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
