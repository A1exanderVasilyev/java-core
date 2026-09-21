import java.util.HashMap;
import java.util.Map;

public class FrequencyAnalysis {

    public static <T> Map<T, Integer> getFrequency(T[] input) {
        if (input == null) {
            throw new NullPointerException("input array is null");
        }

        Map<T, Integer> map = new HashMap<>();
        for (T elem : input) {
            map.put(elem, map.getOrDefault(elem, 0) + 1);
        }

        return map;
    }
}
