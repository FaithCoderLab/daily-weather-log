package faithcoderlab.dailyweatherlog.repository;

import faithcoderlab.dailyweatherlog.model.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    List<Diary> findAllByDate(LocalDate date);
    List<Diary> findAllByDateBetween(LocalDate startDate, LocalDate endDate);
    void deleteAllByDate(LocalDate date);
}
