package hibernate.model;

import lombok.Value;

import java.io.Serializable;

@Value
public class PhoneNumber implements Serializable {
    String countryCode;
    String number;
}
