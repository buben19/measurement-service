package cz.slanina.ms.repository;

import cz.slanina.ms.model.Measurement;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.annotation.Nonnull;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

public interface MeasurementsRepository extends CrudRepository<Measurement, Long> {

    @Nonnull
    @Query(value = "WITH" +
            " dates(date) AS (" +
            "SELECT DISTINCT timestamp as date" +
            " FROM measurements" +
            " WHERE temperature BETWEEN :min AND :max" +
            ")," +
            " groups AS (" +
            "SELECT" +
            " ROW_NUMBER() OVER (ORDER BY dates.date) AS rn," +
            " DATEADD(day, -ROW_NUMBER() OVER (ORDER BY dates.date), dates.date) AS grp," +
            " date" +
            " FROM dates" +
            ")" +
            " SELECT" +
              " COUNT(*) AS c," +
              " MIN(groups.date) AS start," +
              " MAX(groups.date) AS end" +
            " FROM groups" +
            " GROUP BY grp" +
            " ORDER BY 1 DESC", nativeQuery = true)
    List<Streak> findLongestInterval(double min, double max);

//    @Nonnull
//    Optional<Interval> findLongestInterval(double min, double max, @Nonnull Interval interval);

    /**
     * Spring JPA Projection interface.
     */
    interface Streak {

        Timestamp getStart();

        default OffsetDateTime getStartAsOffsetDateTime() {
            return OffsetDateTime.ofInstant(this.getStart().toInstant(), ZoneId.of("UTC"));
        }

        Timestamp getEnd();

        default OffsetDateTime getEndAsOffsetDateTime() {
            return OffsetDateTime.ofInstant(this.getEnd().toInstant(), ZoneId.of("UTC"));
        }
    }
}
