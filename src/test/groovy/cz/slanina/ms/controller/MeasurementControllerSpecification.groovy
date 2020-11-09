package cz.slanina.ms.controller

import cz.slanina.ms.repository.MeasurementsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import spock.lang.Specification
import spock.mock.DetachedMockFactory

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest
class MeasurementControllerSpecification extends Specification {

    @Autowired
    MockMvc mvc

    @Autowired
    MeasurementsRepository repository;

    def "test"() {
        expect:
        mvc.perform(MockMvcRequestBuilders.get("/measurements"))
                .andExpect(status().isOk())
    }

    @TestConfiguration
    static class MockConfig {
        def detachedMockFactory = new DetachedMockFactory()

        @Bean
        MeasurementsRepository measurementsRepository() {
            return detachedMockFactory.Stub(MeasurementsRepository)
        }
    }
}
