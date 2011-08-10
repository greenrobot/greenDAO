package de.greenrobot.dao;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The context for entity identities. Provides the scope in which entities will be tracked and managed.
 * 
 * @author Markus
 * @param <K>
 * @param <T>
 */
public class IdentityScope<K, T> {
    private final Map<K, WeakReference<T>> identityScope;

    public IdentityScope() {
        identityScope = new ConcurrentHashMap<K, WeakReference<T>>();
    }

    public T get(K key) {
        WeakReference<T> ref = identityScope.get(key);
        if (ref != null) {
            return ref.get();
        }
        return null;
    }

    public void put(K key, T entity) {
        identityScope.put(key, new WeakReference<T>(entity));
    }
    
    public void remove(K key) {
        identityScope.remove(key);
    }

    public void clear() {
        identityScope.clear();
    }
    
}
