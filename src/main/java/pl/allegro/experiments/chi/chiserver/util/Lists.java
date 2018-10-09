package pl.allegro.experiments.chi.chiserver.util;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class Lists {
    public static <T> List<T> join(List<T> list, T element) {
        return (List)ImmutableList.builder().addAll(list).add(element).build();
    }

    public static <T> List<T> join(List<T> listA, List<T> listB) {
        return (List)ImmutableList.builder().addAll(listA).addAll(listB).build();
    }
}
