package com.example.demo.utilities;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ListHelper {
    public <T> Map<Integer, List<T>> breakToChunksByMaxChunkSize(List<T> sourceList, int maxChunkSize) {
        Map<Integer, List<T>> result = null;

        if (sourceList != null) {
            result = new HashMap<>();

            for (int currentSourceItemIndex = 0, currentChunk = 1; currentSourceItemIndex < sourceList.size();
                    currentSourceItemIndex++) {
                List<T> currentChunkItems;
                T sourceItem = sourceList.get(currentSourceItemIndex);

                if (!result.containsKey(currentChunk)) {
                    result.put(currentChunk, new LinkedList<>());
                }

                currentChunkItems = result.get(currentChunk);
                currentChunkItems.add(sourceItem);

                if (currentChunkItems.size() == maxChunkSize) {
                    currentChunk++;
                }
            }
        }

        return result;
    }
}
