package zoo;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ZooController {

    ZooService service;

    public ZooController(ZooService service) {
        this.service = service;
    }

    @PostMapping("/animals")
    @ResponseStatus(HttpStatus.CREATED)
    public Animal addAnimal(@RequestBody Animal animal) {
        return service.addAnimal(animal);
    }

    @GetMapping("/animals")
    public List<Animal> getAllAnimals() {
        return service.getAllAnimals();
    }

    @PostMapping("/habitats")
    @ResponseStatus(HttpStatus.CREATED)
    public Habitat addHabitat(@RequestBody Habitat habitat) {
        return service.addHabitat(habitat);
    }

    @PatchMapping("/update-habitat")
    public void assignAnimalToHabitat(@RequestParam Long animalId, @RequestParam Long habitatId) throws AnimalPlacementException {
        service.assignAnimalToHabitat(animalId, habitatId);
    }

    @GetMapping("/animals-by-type-mood")
    public List<Animal> getAnimalByMoodAndType(@RequestParam boolean isHappy, @RequestParam String type) {
        return service.getAnimalByMoodAndType(isHappy, type);
    }

    @GetMapping("empty-habitats")
    public List<Habitat> getEmptyHabitats() {
        return service.getEmptyHabitats();
    }

}
