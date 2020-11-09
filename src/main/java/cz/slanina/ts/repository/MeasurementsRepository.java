package cz.slanina.ts.repository;

import cz.slanina.ts.model.Measurement;
import org.springframework.data.repository.CrudRepository;
import org.threeten.extra.Interval;

import javax.annotation.Nonnull;
import java.util.Optional;

public interface MeasurementsRepository extends CrudRepository<Measurement, Long> {

//    @Nonnull
//    Optional<Interval> findLongestInterval(double min, double max);

//    @Nonnull
//    Optional<Interval> findLongestInterval(double min, double max, @Nonnull Interval interval);
}
