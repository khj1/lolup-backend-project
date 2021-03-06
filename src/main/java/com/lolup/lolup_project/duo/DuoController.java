package com.lolup.lolup_project.duo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/duo")
public class DuoController {

    private final DuoService duoService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> findAll(String position, String tier, Pageable pageable) {
        Map<String, Object> map = duoService.findAll(position, tier, pageable);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PostMapping("/new")
    public ResponseEntity<Long> save(DuoForm form) {
        log.info("add Duo form data ={}", form.toString());
        return new ResponseEntity<>(duoService.save(form), HttpStatus.OK);
    }

    @PatchMapping("/{duoId}")
    public ResponseEntity<Long> update(@PathVariable Long duoId, String position, String desc) {
        return new ResponseEntity<>(duoService.update(duoId, position, desc), HttpStatus.OK);
    }

    @DeleteMapping("/{duoId}")
    public ResponseEntity<Long> delete(@PathVariable Long duoId, Long memberId) {
        return new ResponseEntity<>(duoService.delete(duoId, memberId), HttpStatus.OK);
    }
}
