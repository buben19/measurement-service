package cz.slanina.ms.repository;

import cz.slanina.ms.model.Measurement;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.annotation.Nonnull;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface MeasurementsRepository extends CrudRepository<Measurement, Long> {

    @Nonnull
    @Query(value = "WITH" +
            " dates(date) AS (" +
            "SELECT DISTINCT CAST(timestamp AS DATE) AS date" +
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

    // TODO: Implement this method.
    @Nonnull
    @Query(value = "WITH" +
            " dates(date) AS (" +
            "SELECT DISTINCT CAST(timestamp AS DATE) AS date" +
            " FROM measurements" +
            " WHERE temperature BETWEEN :min AND :max" +
                " AND CAST(timestamp AS TIME) BETWEEN :start AND :end" +
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
    List<Streak> findLongestInterval(double min, double max, LocalTime start, LocalTime end);

    /**
     * Spring JPA Projection interface.
     */
    interface Streak {

        Date getStart();

        default LocalDate getStartAsLocalDate() {
            return this.getStart().toLocalDate();
        }

        Date getEnd();

        default LocalDate getEndAsLocalDate() {
            return this.getEnd().toLocalDate();
        }
    }
}
