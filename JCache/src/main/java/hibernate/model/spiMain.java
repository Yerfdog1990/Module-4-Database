package hibernate.model;

import java.util.ServiceLoader;

public class spiMain {
    public static void main(String[] args){
        ServiceLoader<CachedService> loader = ServiceLoader.load(CachedService.class);
        for(CachedService service : loader){
            System.out.println(service.getFromCache("key"));
        }
    }
}
