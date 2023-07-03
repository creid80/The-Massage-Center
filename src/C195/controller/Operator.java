package C195.controller;

@FunctionalInterface
public interface Operator<T> {
    T process (T a, T b, T c);
}
