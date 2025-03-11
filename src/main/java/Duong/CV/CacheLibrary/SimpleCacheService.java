package Duong.CV.CacheLibrary;

import java.util.concurrent.*;


public class SimpleCacheService implements CacheService {
    private final ConcurrentHashMap<String, CacheObject> cache = new ConcurrentHashMap<>();
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);

    @Override
    public <T> T save(String key, T value, Long ttl) {
        CacheObject<T> newCacheObject = new CacheObject<>(value, System.currentTimeMillis() + ttl * 1000);
        cache.put(key, newCacheObject);
        executorService.schedule(() -> cache.remove(key), ttl, TimeUnit.SECONDS);
        return value;
    }

    @Override
    public <T> T get(String key) {
        CacheObject<T> cacheObject = (CacheObject<T>) cache.get(key);
        if (cacheObject != null && cacheObject.isValid()) {
            return cacheObject.getValue();
        }
        return null;
    }

    private static class CacheObject<T> {
        private T value;
        private long expiryTime;

        public CacheObject(T value, long expiryTime) {
            this.value = value;
            this.expiryTime = expiryTime;
        }

        public T getValue() {
            return value;
        }

        public boolean isValid() {
            return System.currentTimeMillis() < expiryTime;
        }
    }
}
