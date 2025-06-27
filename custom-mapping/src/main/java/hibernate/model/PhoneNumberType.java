package hibernate.model;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class PhoneNumberType implements UserType<PhoneNumber> {
    @Override
    public int getSqlType() {
    return Types.CHAR;
    }

    @Override
    public Class<PhoneNumber> returnedClass() {
        return PhoneNumber.class;
    }

    @Override
    public boolean equals(PhoneNumber x, PhoneNumber y) {
        return x.equals(y);
    }

    @Override
    public int hashCode(PhoneNumber phoneNumber) {
        return phoneNumber.hashCode();
    }

    @Override
    public PhoneNumber nullSafeGet(ResultSet resultSet, int i, SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws SQLException {
        String phoneNumber = resultSet.getString(i);
        if(phoneNumber != null){
            String[] parts = phoneNumber.split("-");
            // Clean up leading/trailing whitespaces
            String countryCode = parts[0].trim();
            String number = parts[1].trim();
            return new PhoneNumber(countryCode, number);
        }else{
            return null;
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement preparedStatement, PhoneNumber phoneNumber, int i, SharedSessionContractImplementor sharedSessionContractImplementor) throws SQLException {
        if(phoneNumber != null){
            preparedStatement.setString(i, phoneNumber.getCountryCode() + "-" + phoneNumber.getNumber());
        }else{
            preparedStatement.setNull(i, Types.CHAR);
        }
    }

    @Override
    public PhoneNumber deepCopy(PhoneNumber phoneNumber) {
        return new PhoneNumber(phoneNumber.getCountryCode(), phoneNumber.getNumber());
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(PhoneNumber phoneNumber) {
        return deepCopy(phoneNumber);
    }

    @Override
    public PhoneNumber assemble(Serializable serializable, Object o) {
        return serializable == null ? null : (PhoneNumber) serializable;
    }
}
