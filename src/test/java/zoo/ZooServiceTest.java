package zoo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static utils.Constants.FLYING;
import static utils.Constants.WALKING;

@ExtendWith(MockitoExtension.class)
class ZooServiceTest {

    @Mock
    AnimalRepository animalRepository;

    @Mock
    HabitatRepository habitatRepository;

    @InjectMocks
    ZooService service;

    @Test
    public void addAnimal_callsSaveOnAnimalRepository() {
        Animal animal = new Animal("lion", WALKING);
        when(animalRepository.save(animal)).thenReturn(animal);
        Animal result = service.addAnimal(animal);
        verify(animalRepository, times(1)).save(animal);
        assertEquals(animal, result);
    }

    @Test
    public void getAllAnimals_returnsListOfALlAnimalsInDatabase() {

        Animal lion = new Animal("lion", WALKING);
        Animal flamingo = new Animal("flamingo", FLYING);
        List<Animal> animals = List.of(lion, flamingo);

        when(animalRepository.findAll()).thenReturn(animals);
        List<Animal> result = service.getAllAnimals();
        verify(animalRepository, times(1)).findAll();
        assertEquals(animals, result);
    }


    @Test
    public void giveTreat_setsHappinessToTrue() {
        Animal flamingo = new Animal("flamingo", FLYING);
        flamingo.setId(12345L);

        Animal updatedAnimal = new Animal("flamingo", FLYING);
        updatedAnimal.setId(12345L);
        updatedAnimal.setHappy(true);

        when(animalRepository.findById(12345L)).thenReturn(java.util.Optional.of(flamingo));
        when(animalRepository.save(updatedAnimal)).thenReturn(updatedAnimal);

        service.giveTreat(12345L);

        verify(animalRepository, times(1)).findById(12345L);
        verify(animalRepository, times(1)).save(updatedAnimal);;
    }


    @Test
    public void addHabitat_callsSaveOnHabitatRepository() {
        Habitat habitat = new Habitat("nest", FLYING);
        when(habitatRepository.save(habitat)).thenReturn(habitat);
        Habitat result = service.addHabitat(habitat);
        verify(habitatRepository, times(1)).save(habitat);
        assertEquals(habitat, result);
    }

    @Test
    public void assignAnimalToHabitat_assignsSelectedAnimalToHabitat() throws AnimalPlacementException {
        Habitat habitat = new Habitat("nest", FLYING);
        habitat.setId(67890L);
        Animal flamingo = new Animal("flamingo", FLYING);
        flamingo.setId(12345L);

        when(animalRepository.findById(12345L)).thenReturn(java.util.Optional.of(flamingo));
        when(habitatRepository.findById(67890L)).thenReturn(java.util.Optional.of(habitat));
        when(habitatRepository.save(habitat)).thenReturn(null);

        service.assignAnimalToHabitat(12345L, 67890L);

        verify(animalRepository, times(1)).findById(12345L);
        verify(habitatRepository, times(1)).findById(67890L);
        verify(habitatRepository, times(1)).save(habitat);
    }

    @Test
    public void assignAnimalToHabitat_throwsExceptionAndMakesAnimalUnhappyIfPlacedInIncompatibleHabitat() {
        Animal lion = new Animal("lion", WALKING);
        lion.setId(12345L);
        lion.setHappy(true);
        Animal updatedAnimal = new Animal("lion", WALKING);
        updatedAnimal.setId(12345L);
        updatedAnimal.setHappy(true);
        Habitat habitat = new Habitat("nest", FLYING);
        habitat.setId(67890L);

        when(animalRepository.findById(12345L)).thenReturn(java.util.Optional.of(lion));
        when(habitatRepository.findById(67890L)).thenReturn(java.util.Optional.of(habitat));
        when(animalRepository.save(updatedAnimal)).thenReturn(updatedAnimal);

        assertThrows(AnimalPlacementException.class,
                () -> { service.assignAnimalToHabitat(12345L, 67890L); },
                "Unable To Place Animal Due To Incompatible Habitat");

        verify(animalRepository, times(1)).findById(12345L);
        verify(animalRepository, times(1)).save(updatedAnimal);
        verify(habitatRepository, times(1)).findById(67890L);
    }

    @Test
    public void assignAnimalToHabitat_throwsExceptionIfAnimalPlacedInOccupiedHabitat() {
        Animal lion = new Animal("lion", WALKING);
        lion.setId(12345L);

        Animal elephant = new Animal("elephant", WALKING);

        Habitat habitat = new Habitat("savanah", WALKING);
        habitat.setAnimal(elephant);
        habitat.setId(67890L);

        when(animalRepository.findById(12345L)).thenReturn(java.util.Optional.of(lion));
        when(habitatRepository.findById(67890L)).thenReturn(java.util.Optional.of(habitat));

        assertThrows(AnimalPlacementException.class,
                () -> { service.assignAnimalToHabitat(12345L, 67890L); },
                "Unable To Place Animal Due To Occupied Habitat");

        verify(animalRepository, times(1)).findById(12345L);
        verify(habitatRepository, times(1)).findById(67890L);
    }

    @Test
    public void getAnimalByMoodAndType_returnsListOfMatchingAnimals() {
        Animal lion = new Animal("lion", WALKING);
        Animal flamingo = new Animal("water buffalo", WALKING);
        List<Animal> animals = List.of(lion, flamingo);
        when(animalRepository.findByIsHappyAndType(false, WALKING)).thenReturn(animals);
        List<Animal> result = service.getAnimalByMoodAndType(false, WALKING);
        verify(animalRepository, times(1)).findByIsHappyAndType(false, WALKING);
        assertEquals(animals, result);
    }

    @Test
    public void getEmptyHabitats_returnsListOfEmptyHabitats() {
        Habitat habitat1 = new Habitat("savanah", WALKING);
        Habitat habitat2 = new Habitat("nest", FLYING);
        List<Habitat> habitats = List.of(habitat1, habitat2);

        when(habitatRepository.findByAnimal(null)).thenReturn(habitats);
        List<Habitat> result = service.getEmptyHabitats();
        verify(habitatRepository, times(1)).findByAnimal(null);
        assertEquals(habitats, result);
    }

}