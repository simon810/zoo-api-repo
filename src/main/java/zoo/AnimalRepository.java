package zoo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnimalRepository extends JpaRepository<Animal, Long> {
    public List<Animal> findByIsHappyAndType(boolean isHappy, String type);
}
