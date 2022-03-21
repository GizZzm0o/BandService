package com.example.bandservice.controller;

import com.example.bandservice.exception.NullBandReferenceException;
import com.example.bandservice.model.Band;
import com.example.bandservice.service.BandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/bands")
public class BandController {
    private final BandService bandService;
    private final Logger logger = LoggerFactory.getLogger(BandController.class);

    public BandController(BandService bandService) {
        this.bandService = bandService;
    }

    @PostMapping
    public ResponseEntity<Band> saveBand(@Valid @RequestBody Band band, Errors errors) {
        logger.info("Creating new band");
        if (errors.hasErrors()) {
            throw new NullBandReferenceException("Band is not valid");
        }
        Band band2 = bandService.readByName(band.getName());
        if (band2 != null) {
            throw new NullBandReferenceException("The band is in DB");
        }
        return ResponseEntity.ok(bandService.create(band));
    }

    @GetMapping
    public ResponseEntity<Band> getBand(@RequestParam("bandName") String name) {
        logger.info("Getting band name = {}", name);
        return ResponseEntity.ok(bandService.readByName(name));
    }
    
    @GetMapping("/bb/{id}")
    public ResponseEntity<String> getUnicornByIdByEntity(@PathVariable final String id) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForEntity(
                "https://mafias-user-service-app.herokuapp.com/api/users/" + id,
                String.class);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Band> findBandById(@PathVariable("id") Long id) {
        logger.info("Getting band id = {}", id);
        Band band = bandService.readById(id);
        if (Objects.isNull(band)) {
            throw new NullBandReferenceException("Not found");
        } else {
            return ResponseEntity.ok(band);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Band>> findAll() {
        logger.info("Getting all bands");
        return ok(bandService.getAll());

    }

    @GetMapping("/report")
    public ResponseEntity<Map<String, List<String>>> getReport() {
        logger.info("Getting global report");
        return ResponseEntity.ok(bandService.getReport());
    }

    @GetMapping("/{id}/report")
    public ResponseEntity<List<String>> getBandReport(@PathVariable("id") Long id) {
        logger.info("Getting band report with id {}", id);
        return ResponseEntity.ok(bandService.getSingleReport(id));
    }

    @GetMapping("/{id}/tasks/{taskId}/check")
    public ResponseEntity<String> makeReadyCheck(@PathVariable("id") Long id, @PathVariable("taskId") Long taskId) {
        logger.info("Checking task with id {}", taskId);
        return ResponseEntity.ok(bandService.getReadyCheck(id, taskId));
    }

    @DeleteMapping("/{id}")
    public void deleteBand(@PathVariable("id") Long id) {
        logger.info("Deleting band id = {}", id);
        bandService.delete(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Band> updateBand(@PathVariable("id") Long id, @RequestBody Band band) {
        logger.info("Updating band id = {}", id);
        return ResponseEntity.ok(bandService.update(id, band));
    }
}
