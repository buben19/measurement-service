package cz.slanina.ms.repository;

import cz.slanina.ms.model.Measurement;
import org.springframework.data.repository.CrudRepository;

public interface MeasurementsRepository extends CrudRepository<Measurement, Long> {

//    @Nonnull
//    Optional<Interval> findLongestInterval(double min, double max);

//    @Nonnull
//    Optional<Interval> findLongestInterval(double min, double max, @Nonnull Interval interval);
}
