package zoo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.web.servlet.config.MvcNamespaceHandler;


import java.util.List;

import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static utils.Constants.*;

@SpringBootTest
@AutoConfigureMockMvc
class ZooControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AnimalRepository animalRepository;

    @Autowired
    HabitatRepository habitatRepository;

    ObjectMapper mapper;

    @BeforeEach
    public void setup() {
        habitatRepository.deleteAll();
        animalRepository.deleteAll();
        mapper = new ObjectMapper();
    }

    @Test
    public void addAnimal() throws Exception {
        Animal animal = new Animal("lion", WALKING);
        String animalJson = mapper.writeValueAsString(animal);

        MvcResult mvcResult = mockMvc.perform(post("/animals")
                .content(animalJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
             ).andExpect(status().isCreated())
                .andReturn();

        Animal result = mapper.readValue(mvcResult.getResponse().getContentAsString(), Animal.class);

        assertEquals(result, animalRepository.findById(result.getId()).get());
        assertEquals("lion", result.getName());
        assertEquals(WALKING, result.getType());
    }

    @Test
    public void getAllAnimals() throws Exception {
        Animal lion = animalRepository.save(new Animal("lion", WALKING));
        Animal flamingo = animalRepository.save(new Animal("flamingo", FLYING));
        List<Animal> animals = List.of(lion, flamingo);
        String animalJson = mapper.writeValueAsString(animals);

        mockMvc.perform(get("/animals"))
                .andExpect(status().isOk())
                .andExpect(content().string(animalJson));
    }

    @Test
    public void addHabitat() throws Exception {
        Habitat habitat = new Habitat("nest", FLYING);
        String habitatJson = mapper.writeValueAsString(habitat);

        MvcResult mvcResult = mockMvc.perform(post("/habitats")
                .content(habitatJson)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated())
                .andReturn();

        Habitat result = mapper.readValue(mvcResult.getResponse().getContentAsString(), Habitat.class);

        assertEquals(result, habitatRepository.findById(result.getId()).get());
        assertEquals("nest", result.getName());
        assertEquals(FLYING, result.getType());
    }

    @Test
    public void assignAnimalToHabitat() throws Exception {
        Habitat habitat = habitatRepository.save(new Habitat("nest", FLYING));
        Animal flamingo = animalRepository.save(new Animal("flamingo", FLYING));

        mockMvc.perform(patch("/update-habitat?animalId=" + flamingo.getId() + "&habitatId=" + habitat.getId()))
                .andExpect(status().isOk());
    }

    @Test
    public void getAnimalByMoodAndType() throws Exception {
        Animal lion = animalRepository.save(new Animal("lion", WALKING));
        Animal elephant = animalRepository.save(new Animal("elephant", WALKING));
        Animal flamingo = animalRepository.save(new Animal("flamingo", FLYING));

        List<Animal> expected = List.of(lion, elephant);
        String expectedString = mapper.writeValueAsString(expected);

        mockMvc.perform(get("/animals-by-type-mood?isHappy=false&type=walking"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedString));
    }

    @Test
    public void getEmptyHabitats() throws Exception {
        Animal flamingo = new Animal("flamingo", FLYING);
        Habitat habitat1 = new Habitat("nest", FLYING);
        habitat1.setAnimal(flamingo);
        habitatRepository.save(habitat1);
        Habitat habitat2 = habitatRepository.save(new Habitat("ocean", SWIMMING));
        Habitat habitat3 = habitatRepository.save(new Habitat("burrow", WALKING));
        List<Habitat> emptyHabitats = List.of(habitat2, habitat3);
        String habitatString = mapper.writeValueAsString(emptyHabitats);

        mockMvc.perform(get("/empty-habitats"))
                .andExpect(status().isOk())
                .andExpect(content().string(habitatString));
    }
}