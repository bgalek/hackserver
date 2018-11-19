package pl.allegro.experiments.chi.chiserver.util;

import com.google.common.collect.ImmutableList;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Lists {
    public static <T> List<T> join(List<T> list, T element) {
        return (List)ImmutableList.builder().addAll(list).add(element).build();
    }

    public static <T> List<T> join(List<T> listA, List<T> listB) {
        return (List)ImmutableList.builder().addAll(listA).addAll(listB).build();
    }

    /**
     * applies trim() and toLowerCase()
     */
    public static List<String> sanitizedCopy(List<String> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        var result = list.stream().map(it -> it.trim().toLowerCase()).collect(Collectors.toList());
        return (List)ImmutableList.builder().addAll(result).build();
    }
}
