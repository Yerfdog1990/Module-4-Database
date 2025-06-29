package spi;

public class CacheServiceImpl implements CacheService {
  @Override
  public Object getFromCache(String id) {
    return "Some Object";
  }
}
