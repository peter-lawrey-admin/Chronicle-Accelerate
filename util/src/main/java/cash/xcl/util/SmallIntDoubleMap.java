package cash.xcl.util;

import com.koloboke.function.IntDoubleConsumer;

public class SmallIntDoubleMap extends XCLIntDoubleMap {
    final int[] keys;
    final double[] values;
    int size = 0;

    public SmallIntDoubleMap(int capacity) {
        keys = new int[capacity];
        values = new double[capacity];
    }

    @Override
    public double put(int key, double value) {
        for (int i = 0; i < size; i++) {
            if (keys[i] == key) {
                double tmp = values[i];
                values[i] = value;
                return tmp;
            }
        }
        keys[size] = key;
        values[size] = value;
        size++;
        return 0.0;
    }

    @Override
    public double get(int key) {
        for (int i = 0; i < size; i++) {
            if (keys[i] == key) {
                return values[i];
            }
        }
        return 0.0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
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
    public void forEach(IntDoubleConsumer intDoubleConsumer) {
        for (int i = 0; i < size; i++) {
            intDoubleConsumer.accept(keys[i], values[i]);
        }
    }
}
