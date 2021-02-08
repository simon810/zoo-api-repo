package zoo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HabitatRepository extends JpaRepository<Habitat, Long> {
    public List<Habitat> findByAnimal(Animal animal);
}
