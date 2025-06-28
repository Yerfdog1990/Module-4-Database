package hibernate.model;

public class CachedServiceImpl implements CachedService {
    @Override
    public Object getFromCache(String key) {
        return "Same object as in DB";
    }
}
