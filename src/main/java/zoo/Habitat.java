package zoo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Habitat {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String type;

    @OneToOne(cascade = CascadeType.ALL)
    private Animal animal;

    public Habitat(String name, String type) {
        this.name = name;
        this.type = type;
    }
}
