package stellarburgers.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ingredient {
    private String _id;
    private String name;
    private String type;
    private Integer calories;
    private Double price;
}
