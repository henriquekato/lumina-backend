package com.luminabackend.utils;

import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

public class Sublist {
    public static <T> List<T> getSublist(List<T> list, Pageable page){
        final int start = (int) page.getOffset();
        final int end = Math.min((start + page.getPageSize()), list.size());
        List<T> subList = new ArrayList<T>();
        if (start <= end) subList = list.subList(start, end);
        return subList;
    }
}
