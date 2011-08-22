package de.greenrobot.dao;

public interface IdentityScope<K, T> {

    T get(K key);

    void put(K key, T entity);

    boolean detach(K key, T entity);

    void remove(K key);

    void clear();

    void lock();

    void unlock();

}