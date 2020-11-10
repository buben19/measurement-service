package cz.slanina.ms.controller;

import cz.slanina.ms.model.Measurement;
import cz.slanina.ms.model.Streak;
import cz.slanina.ms.repository.MeasurementsRepository;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Basic REST API controller. It provides basic CRUD methods and functionality for measurement streaks.
 */
@SuppressWarnings({"WeakerAccess", "unused", "DefaultAnnotationParam"})
@RestController
public class MeasurementController {

    private final MeasurementsRepository repository;

    @Autowired
    public MeasurementController(MeasurementsRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/measurements")
    public ResponseEntity<CollectionModel<EntityModel<Measurement>>> getAll() {
        List<EntityModel<Measurement>> measurements = StreamSupport.stream(this.repository.findAll().spliterator(), false)
                .map(measurement -> EntityModel.of(measurement,
                        linkTo(methodOn(MeasurementController.class).get(measurement.getId())).withSelfRel(),
                        linkTo(methodOn(MeasurementController.class).getAll()).withRel("measurements")))
                .collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(measurements, linkTo(methodOn(MeasurementController.class).getAll()).withSelfRel()));
    }

    @GetMapping("/measurements/{id}")
    public ResponseEntity<EntityModel<Measurement>> get(@PathVariable Long id) {
        return this.repository.findById(id)
                .map(measurement -> EntityModel.of(measurement,
                        linkTo(methodOn(MeasurementController.class).get(measurement.getId())).withSelfRel(),
                        linkTo(methodOn(MeasurementController.class).getAll()).withRel("measurements")))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/measurements")
    public ResponseEntity<?> create(@RequestBody Measurement measurement) {
        try {
            Measurement save = this.repository.save(measurement);
            EntityModel<Measurement> of = EntityModel.of(save,
                    linkTo(methodOn(MeasurementController.class).get(save.getId())).withSelfRel());
            return ResponseEntity.created(new URI(of.getRequiredLink(IanaLinkRelations.SELF).getHref()))
                    .body(of);
        } catch (URISyntaxException ex) {
            return ResponseEntity.badRequest().body("Unable to create " + measurement);
        }
    }

    @PutMapping("/measurements/{id}")
    public ResponseEntity<?> update(@RequestBody Measurement measurement, @PathVariable Long id) {
        measurement.setId(id);
        this.repository.save(measurement);
        Link link = linkTo(methodOn(MeasurementController.class).get(id)).withSelfRel();
        try {
            return ResponseEntity.noContent().location(new URI(link.getHref())).build();
        } catch (URISyntaxException ex) {
            return ResponseEntity.badRequest().body("Unable to update " + measurement);
        }
    }

    @DeleteMapping("/measurements/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        this.repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/measurements/streak")
    public ResponseEntity<?> streak(
            @RequestParam(name = "min", required = true) double min,
            @RequestParam(name = "max", required = true) double max,
            @RequestParam(name = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime start,
            @RequestParam(name = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime end) {
        List<MeasurementsRepository.Streak> intervals;
        if (ObjectUtils.allNotNull(start, end)) {
            intervals = this.repository.findLongestInterval(min, max, start, end);
        } else {
            intervals = this.repository.findLongestInterval(min, max);
        }
        if (intervals.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            MeasurementsRepository.Streak streak = intervals.get(0);
            Streak resource = new Streak(streak.getStartAsLocalDate(), streak.getEndAsLocalDate());
            return ResponseEntity.ok().body(resource);
        }
    }
}
