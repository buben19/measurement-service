package cz.slanina.ms.model;

import lombok.Data;

import java.time.OffsetDateTime;

/**
 * Streak resource model.
 */
@Data
public class Streak {

    Double min;
    Double max;
    OffsetDateTime start;
    OffsetDateTime end;
}
