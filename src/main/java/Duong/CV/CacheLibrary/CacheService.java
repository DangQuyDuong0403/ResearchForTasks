package Duong.CV.CacheLibrary;

public interface CacheService {
    <T> T save(String key, T value, Long ttl);
    <T> T get(String key);
}
