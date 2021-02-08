package zoo;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ZooService {
    private AnimalRepository animalRepository;
    private HabitatRepository habitatRepository;

    public ZooService(AnimalRepository animalRepository, HabitatRepository habitatRepository) {
        this.animalRepository = animalRepository;
        this.habitatRepository = habitatRepository;
    }

    public Animal addAnimal(Animal animal) {
        return animalRepository.save(animal);
    }

    public List<Animal> getAllAnimals() {
        return animalRepository.findAll();
    }

    public void giveTreat(long animalId) {
        Animal animalToUpdate = animalRepository.findById(animalId).get();
        animalToUpdate.setHappy(true);
        animalRepository.save(animalToUpdate);
    }

    public Habitat addHabitat(Habitat habitat) {
        return habitatRepository.save(habitat);
    }

    public void assignAnimalToHabitat(long animalId, long habitatId) throws AnimalPlacementException {
        Animal animalToPlace = animalRepository.findById(animalId).get();
        Habitat habitatToUpdate = habitatRepository.findById(habitatId).get();

        if(!habitatToUpdate.getType().equals(animalToPlace.getType())) {
            animalToPlace.setHappy(false);
            animalRepository.save(animalToPlace);
            throw new AnimalPlacementException("Unable To Place Animal Due To Incompatible Habitat");
        } else if (habitatToUpdate.getAnimal() != null) {
            throw new AnimalPlacementException("Unable To Place Animal Due To Occupied Habitat");
        }

        habitatToUpdate.setAnimal(animalToPlace);
        habitatRepository.save(habitatToUpdate);
    }

    public List<Animal> getAnimalByMoodAndType(boolean isHappy, String type) {
        return animalRepository.findByIsHappyAndType(isHappy, type);
    }

    public List<Habitat> getEmptyHabitats() {
        return habitatRepository.findByAnimal(null);
    }
}
