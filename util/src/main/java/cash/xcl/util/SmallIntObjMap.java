package cash.xcl.util;

import com.koloboke.function.IntObjConsumer;

public class SmallIntObjMap<V> extends XCLIntObjMap<V> {
    final int[] keys;
    final V[] values;
    int size = 0;

    public SmallIntObjMap(int capacity) {
        keys = new int[capacity];
        values = (V[]) new Object[capacity];
    }

    @Override
    public V put(int key, V value) {
        for (int i = 0; i < size; i++) {
            if (keys[i] == key) {
                V tmp = values[i];
                values[i] = value;
                return tmp;
            }
        }
        keys[size] = key;
        values[size] = value;
        size++;
        return null;
    }

    @Override
    public V get(int key) {
        for (int i = 0; i < size; i++) {
            if (keys[i] == key) {
                return values[i];
            }
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean containsKey(int key) {
        for (int i = 0; i < size; i++) {
            if (keys[i] == key) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void clear() {
        size = 0;
    }

    @Override
    public void forEach(IntObjConsumer<? super V> longObjConsumer) {
        for (int i = 0; i < size; i++) {
            longObjConsumer.accept(keys[i], values[i]);
        }
    }
}
