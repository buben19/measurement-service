package cz.slanina.ts.model;


import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.OffsetDateTime;

/**
 * Measurement entity. Contains temperature reading and measurement timestamp.
 */
@Data
@Entity
@Table(name = "temperature")
public class Measurement {

    @Id
    @GeneratedValue
    private Long id;

    private Double temperature;

    private OffsetDateTime timestamp;
}
