package com.example.myapplication;

import com.example.myapplication.Buildings;

import java.util.ArrayList;

public class CustomArrayList<E> extends ArrayList<E> {

    @Override
    public boolean add(E element) {
        if (!containsBuilding(element)) {
            return super.add(element);
        }
        return false;
    }

    public boolean containsBuilding(Object element) {
//        if (!(element instanceof Buildings)) {
//            return false;
//        }
//        String uidToFind = ((Buildings) element).getUid();
//        for (E e : this) {
//            if (e instanceof Buildings && ((Buildings) e).getUid().equals(uidToFind)) {
//                return true;
//            }
//        }
        return true;
    }
}
