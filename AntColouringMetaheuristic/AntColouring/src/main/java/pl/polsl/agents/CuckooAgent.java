package pl.polsl.agents;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class CuckooAgent {
    private Map<String, Integer> currentVertex;
}
