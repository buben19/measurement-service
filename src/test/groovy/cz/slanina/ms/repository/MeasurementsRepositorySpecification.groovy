package cz.slanina.ms.repository

import cz.slanina.ms.model.Measurement
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import spock.lang.Ignore
import spock.lang.Specification

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

    @Ignore
    def "get interval"() {
        when:
        this.repository.save(new Measurement(
                temperature: 10.0,
                timestamp: OffsetDateTime.of(2020, 1, 1, 1, 0, 0, 0, ZoneOffset.UTC)
        ))
        this.repository.save(new Measurement(
                temperature: 12.0,
                timestamp: OffsetDateTime.of(2020, 1, 1, 2, 0, 0, 0, ZoneOffset.UTC)
        ))
        this.repository.save(new Measurement(
                temperature: 22.0,
                timestamp: OffsetDateTime.of(2020, 1, 1, 3, 0, 0, 0, ZoneOffset.UTC)
        ))
        this.repository.save(new Measurement(
                temperature: 10.0,
                timestamp: OffsetDateTime.of(2020, 1, 1, 4, 0, 0, 0, ZoneOffset.UTC)
        ))
        this.repository.save(new Measurement(
                temperature: 15.0,
                timestamp: OffsetDateTime.of(2020, 1, 1, 5, 0, 0, 0, ZoneOffset.UTC)
        ))
        this.repository.save(new Measurement(
                temperature: 16.0,
                timestamp: OffsetDateTime.of(2020, 1, 1, 6, 0, 0, 0, ZoneOffset.UTC)
        ))
        this.repository.save(new Measurement(
                temperature: 23.0,
                timestamp: OffsetDateTime.of(2020, 1, 1, 7, 0, 0, 0, ZoneOffset.UTC)
        ))
        this.repository.save(new Measurement(
                temperature: 11.0,
                timestamp: OffsetDateTime.of(2020, 1, 1, 8, 0, 0, 0, ZoneOffset.UTC)
        ))

        then:
        def intervals = this.repository.findLongestInterval(10.0, 20.0)
        intervals.size() == 3
        def head = intervals.head()
        head.getStartAsOffsetDateTime() == OffsetDateTime.of(2020, 1, 1, 4, 0, 0, 0, ZoneOffset.UTC)
        head.getEndAsOffsetDateTime() == OffsetDateTime.of(2020, 1, 1, 6, 0, 0, 0, ZoneOffset.UTC)
    }
}
