package cz.slanina.ms.service;

import cz.slanina.ms.model.Measurement;
import cz.slanina.ms.model.Streak;
import cz.slanina.ms.repository.MeasurementsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MeasurementService {

    private final MeasurementsRepository repository;

    @Autowired
    public MeasurementService(MeasurementsRepository repository) {
        this.repository = repository;
    }

    @Nonnull
    public Iterable<Measurement> getAll() {
        return this.repository.findAll();
    }

    @Nonnull
    public Optional<Measurement> get(@Nonnull Long id) {
        return this.repository.findById(id);
    }

    @Nonnull
    public Measurement create(@Nonnull Measurement measurement) {
        return this.repository.save(measurement);
    }

    @Nonnull
    public Measurement update(Measurement measurement) {
        return this.repository.save(measurement);
    }

    public void delete(@Nonnull Long id) {
        this.repository.deleteById(id);
    }

    @Nonnull
    public Optional<Streak> findStreak(double min, double max) {
        List<MeasurementsRepository.Streak> longestInterval = this.repository.findLongestInterval(min, max);
        if (longestInterval.isEmpty()) {
            return Optional.empty();
        } else {
            MeasurementsRepository.Streak streak = longestInterval.get(0);
            return Optional.of(new Streak(streak.getStartAsLocalDate(), streak.getEndAsLocalDate()));
        }
    }

    @Nonnull
    public Optional<Streak> findStreakWithTime(double min, double max, @Nonnull LocalTime start, @Nonnull LocalTime end) {
        List<MeasurementsRepository.Streak> longestInterval = this.repository.findLongestInterval(min, max, start, end);
        if (longestInterval.isEmpty()) {
            return Optional.empty();
        } else {
            MeasurementsRepository.Streak streak = longestInterval.get(0);
            return Optional.of(new Streak(streak.getStartAsLocalDate(), streak.getEndAsLocalDate()));
        }
    }
}
