package cz.slanina.ms.repository

import cz.slanina.ms.model.Measurement
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

@DataJpaTest
class MeasurementsRepositorySpecification extends Specification {

    @Autowired
    MeasurementsRepository repository

    def "context loads"() {
        expect:
        repository
    }

    def "create entity"() {
        given:
        def measurement = new Measurement(
                temperature: 1.0,
                timestamp: OffsetDateTime.now()
        )

        when:
        def save = this.repository.save(measurement)
        measurement.setId(save.getId())

        then:
        measurement == this.repository.findById(save.getId()).get()
    }

    def "delete entity"() {
        given:
        def measurement = new Measurement(
                temperature: 1.0,
                timestamp: OffsetDateTime.now()
        )

        when:
        def save = this.repository.save(measurement)
        this.repository.deleteById(save.getId())
        def find = this.repository.findById(save.getId())

        then:
        !find
    }

    def "get interval"() {
        when:
        this.repository.save(new Measurement(
                temperature: 10.0,
                timestamp: OffsetDateTime.of(2020, 1, 1, 1, 0, 0, 0, ZoneOffset.UTC)
        ))
        this.repository.save(new Measurement(
                temperature: 12.0,
                timestamp: OffsetDateTime.of(2020, 1, 2, 1, 0, 0, 0, ZoneOffset.UTC)
        ))
        this.repository.save(new Measurement(
                temperature: 22.0,
                timestamp: OffsetDateTime.of(2020, 1, 3, 1, 0, 0, 0, ZoneOffset.UTC)
        ))
        this.repository.save(new Measurement(
                temperature: 10.0,
                timestamp: OffsetDateTime.of(2020, 1, 4, 1, 0, 0, 0, ZoneOffset.UTC)
        ))
        this.repository.save(new Measurement(
                temperature: 15.0,
                timestamp: OffsetDateTime.of(2020, 1, 5, 1, 0, 0, 0, ZoneOffset.UTC)
        ))
        this.repository.save(new Measurement(
                temperature: 16.0,
                timestamp: OffsetDateTime.of(2020, 1, 6, 1, 0, 0, 0, ZoneOffset.UTC)
        ))
        this.repository.save(new Measurement(
                temperature: 23.0,
                timestamp: OffsetDateTime.of(2020, 1, 7, 1, 0, 0, 0, ZoneOffset.UTC)
        ))
        this.repository.save(new Measurement(
                temperature: 11.0,
                timestamp: OffsetDateTime.of(2020, 1, 8, 1, 0, 0, 0, ZoneOffset.UTC)
        ))

        then:
        def intervals = this.repository.findLongestInterval(10.0, 20.0)
        intervals.size() == 3
        def head = intervals.head()
        head.getStartAsLocalDate() == LocalDate.of(2020, 1, 4)
        head.getEndAsLocalDate() == LocalDate.of(2020, 1, 6)
    }

    def "get interval with time ranges"() {
        when: "one measurement per day"
        this.repository.save(new Measurement(
                temperature: 10.0,
                timestamp: OffsetDateTime.of(2020, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC)
        ))
        this.repository.save(new Measurement(
                temperature: 12.0,
                timestamp: OffsetDateTime.of(2020, 1, 2, 12, 0, 0, 0, ZoneOffset.UTC)
        ))
        this.repository.save(new Measurement(
                temperature: 22.0,
                timestamp: OffsetDateTime.of(2020, 1, 3, 12, 0, 0, 0, ZoneOffset.UTC)
        ))
        this.repository.save(new Measurement(
                temperature: 10.0,
                timestamp: OffsetDateTime.of(2020, 1, 4, 12, 0, 0, 0, ZoneOffset.UTC)
        ))
        this.repository.save(new Measurement(
                temperature: 15.0,
                timestamp: OffsetDateTime.of(2020, 1, 5, 12, 0, 0, 0, ZoneOffset.UTC)
        ))

        and: "measurement in same day, but too early"
        this.repository.save(new Measurement(
                temperature: 22.0,
                timestamp: OffsetDateTime.of(2020, 1, 5, 10, 0, 0, 0, ZoneOffset.UTC)
        ))

        and: "follows one measurement per day"
        this.repository.save(new Measurement(
                temperature: 16.0,
                timestamp: OffsetDateTime.of(2020, 1, 6, 12, 0, 0, 0, ZoneOffset.UTC)
        ))
        this.repository.save(new Measurement(
                temperature: 23.0,
                timestamp: OffsetDateTime.of(2020, 1, 7, 12, 0, 0, 0, ZoneOffset.UTC)
        ))
        this.repository.save(new Measurement(
                temperature: 11.0,
                timestamp: OffsetDateTime.of(2020, 1, 8, 12, 12, 0, 0, ZoneOffset.UTC)
        ))

        then:
        def intervals = this.repository.findLongestInterval(10.0, 20.0, LocalTime.of(11, 1, 0), LocalTime.of(17, 0, 0))
        intervals.size() == 3
        def head = intervals.head()
        head.getStartAsLocalDate() == LocalDate.of(2020, 1, 4)
        head.getEndAsLocalDate() == LocalDate.of(2020, 1, 6)
    }
}
