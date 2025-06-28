package hibernate.model;

public interface CachedService {
    Object getFromCache(String key);
}
