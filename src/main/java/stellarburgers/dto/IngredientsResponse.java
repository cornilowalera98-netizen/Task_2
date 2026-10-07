package stellarburgers.dto;

import lombok.Data;
import java.util.List;

@Data
public class IngredientsResponse {
    private boolean success;
    private List<Ingredient> data;
}