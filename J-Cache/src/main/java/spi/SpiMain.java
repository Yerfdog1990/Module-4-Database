package spi;

import java.util.ServiceLoader;

public class SpiMain {
  public static void main(String[] args) {
    ServiceLoader<CacheService> serviceLoader = ServiceLoader.load(CacheService.class);

    for (CacheService service : serviceLoader) {
      System.out.println(service.getFromCache("whatever"));
    }
  }
}
