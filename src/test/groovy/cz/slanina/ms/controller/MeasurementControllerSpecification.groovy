package cz.slanina.ms.controller

import com.fasterxml.jackson.databind.ObjectMapper
import cz.slanina.ms.model.Measurement
import cz.slanina.ms.repository.MeasurementsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.hateoas.MediaTypes
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification
import spock.mock.DetachedMockFactory

import java.time.OffsetDateTime
import java.time.ZoneOffset

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest
class MeasurementControllerSpecification extends Specification {

    @Autowired
    MockMvc mvc

    @Autowired
    MeasurementsRepository repository

    @Autowired
    ObjectMapper objectMapper

    def "context loads"() {
        expect:
        mvc
        repository
        objectMapper
    }

    def "get all measurements"() {
        when:
        mvc.perform(get("/measurements"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath('$._embedded.measurements[0].id').value(1))
                .andExpect(jsonPath('$._embedded.measurements[0].temperature').value(10.0))
                .andExpect(jsonPath('$._embedded.measurements[0].timestamp').value('2020-01-01T01:00:00Z'))
                .andExpect(jsonPath('$._embedded.measurements[0]._links.self.href').value('http://localhost/measurements/1'))
                .andExpect(jsonPath('$._embedded.measurements[0]._links.measurements.href').value('http://localhost/measurements'))
                .andExpect(jsonPath('$._embedded.measurements[1].id').value(2))
                .andExpect(jsonPath('$._embedded.measurements[1].temperature').value(11.0))
                .andExpect(jsonPath('$._embedded.measurements[1].timestamp').value('2020-01-01T02:00:00Z'))
                .andExpect(jsonPath('$._embedded.measurements[1]._links.self.href').value('http://localhost/measurements/2'))
                .andExpect(jsonPath('$._embedded.measurements[1]._links.measurements.href').value('http://localhost/measurements'))

        then:
        repository.findAll() >> [
            new Measurement(
                    id: 1,
                    temperature: 10.0,
                    timestamp: OffsetDateTime.of(2020, 1, 1, 1, 0, 0, 0, ZoneOffset.UTC)
            ),
            new Measurement(
                    id: 2,
                    temperature: 11.0,
                    timestamp: OffsetDateTime.of(2020, 1, 1, 2, 0, 0, 0, ZoneOffset.UTC)
            )
        ]
    }

    def "get single measurement"() {
        when:
        mvc.perform(get("/measurements/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath('$.id').value(1))
                .andExpect(jsonPath('$.temperature').value(10.0))
                .andExpect(jsonPath('$.timestamp').value('2020-01-01T01:00:00Z'))
                .andExpect(jsonPath('$._links.self.href').value('http://localhost/measurements/1'))
                .andExpect(jsonPath('$._links.measurements.href').value('http://localhost/measurements'))

        then:
        repository.findById(1) >> Optional.of(new Measurement(
                id: 1,
                temperature: 10.0,
                timestamp: OffsetDateTime.of(2020, 1, 1, 1, 0, 0, 0, ZoneOffset.UTC)
        ))
    }

    def "create new measurement"() {
        given:
        def measurement = new Measurement(
                temperature: 10.0,
                timestamp: OffsetDateTime.of(2020, 1, 1, 2, 0, 0, 0, ZoneOffset.UTC)
        )

        when:
        mvc.perform(post("/measurements")
                .content(objectMapper.writeValueAsString(measurement))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString()))
                .andExpect(jsonPath('$.id').value(1))
                .andExpect(jsonPath('$.temperature').value(10.0))
                .andExpect(jsonPath('$.timestamp').value('2020-01-01T02:00:00Z'))
                .andExpect(jsonPath('$._links.self.href').value('http://localhost/measurements/1'))

        then:
        repository.save(measurement) >> new Measurement(
                id: 1,
                temperature: 10.0,
                timestamp: OffsetDateTime.of(2020, 1, 1, 2, 0, 0, 0, ZoneOffset.UTC)
        )
    }

    def "update existing measurement"() {
        given:
        def measurement = new Measurement(
                temperature: 20.0,
                timestamp: OffsetDateTime.of(2020, 1, 1, 2, 0, 0, 0, ZoneOffset.UTC)
        )

        when:
        mvc.perform(put("/measurements/1")
                .content(objectMapper.writeValueAsString(measurement))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent())

        then:
        repository.save(new Measurement(
                id: 1,
                temperature: 20.0,
                timestamp: OffsetDateTime.of(2020, 1, 1, 2, 0, 0, 0, ZoneOffset.UTC)
        ))
    }

    def "delete measurement"() {
        when:
        mvc.perform(delete("/measurements/1"))
                .andDo(print())
                .andExpect(status().isNoContent())

        then:
        repository.deleteById(1)
    }

    def "empty streak"() {
        when:
        mvc.perform(get("/measurements/streak")
                .param("min", "10")
                .param("max", "20"))
                .andDo(print())
                .andExpect(status().isNoContent())

        then:
        repository.findLongestInterval(10, 20) >> []
    }

    def "get streak"() {
        given:
        def streak = Mock(MeasurementsRepository.Streak)

        when:
        mvc.perform(get("/measurements/streak")
                .param("min", "10")
                .param("max", "20"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.start').value("2020-01-01T02:00:00Z"))
                .andExpect(jsonPath('$.end').value("2020-01-01T03:00:00Z"))

        then:
        repository.findLongestInterval(10, 20) >> [streak]
        streak.getStartAsOffsetDateTime() >> OffsetDateTime.of(2020, 1, 1, 2, 0, 0, 0, ZoneOffset.UTC)
        streak.getEndAsOffsetDateTime() >> OffsetDateTime.of(2020, 1, 1, 3, 0, 0, 0, ZoneOffset.UTC)
    }

    @SuppressWarnings("unused")
    @TestConfiguration
    static class MockConfig {
        def detachedMockFactory = new DetachedMockFactory()

        @Bean
        MeasurementsRepository measurementsRepository() {
            return detachedMockFactory.Stub(MeasurementsRepository)
        }
    }
}
