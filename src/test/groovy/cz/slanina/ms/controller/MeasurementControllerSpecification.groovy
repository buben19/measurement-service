package cz.slanina.ms.controller

import cz.slanina.ms.model.Measurement
import cz.slanina.ms.repository.MeasurementsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.hateoas.MediaTypes
import org.springframework.http.HttpHeaders
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification
import spock.mock.DetachedMockFactory

import java.time.OffsetDateTime
import java.time.ZoneOffset

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest
class MeasurementControllerSpecification extends Specification {

    @Autowired
    MockMvc mvc

    @Autowired
    MeasurementsRepository repository

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
