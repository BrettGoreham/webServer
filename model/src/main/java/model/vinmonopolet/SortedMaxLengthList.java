package model.vinmonopolet;

import java.util.*;

public class SortedMaxLengthList<E> implements List<E> {

    List<E> list = new ArrayList<>();
    int maxLength;
    Comparator<E> comparatorToSort;

    public SortedMaxLengthList(int maxLength, Comparator<E> comparator) {
        this.maxLength = maxLength;
        this.comparatorToSort = comparator;
    }

    /*This is the only real implementation difference than ArrayList.*/
    @Override
    public boolean add(Object o) {
        if(o == null) {
            throw new NullPointerException();
        }

        E objectToAdd;
        try {
            objectToAdd = (E) o;
        } catch (Exception e) {
          throw new ClassCastException();
        }

        list.add(objectToAdd);
        list.sort(comparatorToSort);
        if (list.size() > maxLength) {
            list = list.subList(0, maxLength);
        }

        return true;
    }

    @Override
    public boolean addAll(Collection c) {
        for(Object o : c) {
            this.add(o);
        }

        return true;
    }



    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public Iterator iterator() {
        return list.iterator();
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public boolean addAll(int index, Collection c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        return list.remove(o);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public E get(int index) {
        return list.get(index);
    }

    @Override
    public E set(int index, Object element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, Object element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E remove(int index) {
        return list.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator listIterator() {
        return list.listIterator();
    }

    @Override
    public ListIterator listIterator(int index) {
        return list.listIterator(index);
    }

    @Override
    public List subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    @Override
    public boolean retainAll(Collection c) {
        return list.remove(c);
    }

    @Override
    public boolean removeAll(Collection c) {
        return list.removeAll(c);
    }

    @Override
    public boolean containsAll(Collection c) {
        return list.containsAll(c);
    }

    @Override
    public Object[] toArray(Object[] a) {
        return new Object[0];
    }
}
