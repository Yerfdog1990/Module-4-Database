package txtisolationdemo;

import static txisolationdemo.JdbcUtils.setUpDatabase;

import java.sql.SQLException;
import txisolationdemo.JdbcUtils.*;

public class NonRepeatableReadLearningTest {

  void recreateNonRepeatableRead() throws SQLException, InterruptedException {
    setUpDatabase();

    // Test

  }
}
