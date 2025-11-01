package org.raflab.studsluzba.controller;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.ObnovaGodine;
import org.raflab.studsluzba.service.ObnovaGodineService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/obnove-godine")
@RequiredArgsConstructor
public class ObnovaGodineController {

    private final ObnovaGodineService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ObnovaGodine create(@RequestBody ObnovaGodine body) {
        return service.create(body);
    }

    @GetMapping("/{id}")
    public ObnovaGodine findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public ObnovaGodine update(@PathVariable Long id, @RequestBody ObnovaGodine body) {
        return service.update(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
