package org.kainos.ea.db;

import com.password4j.Hash;
import com.password4j.Password;
import org.kainos.ea.cli.Login;
import org.kainos.ea.util.DaoUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterDao {
    public boolean doesEmailExist(Connection conn, String email) throws SQLException {
        String statement = "SELECT * FROM user WHERE email = ?";
        ResultSet resultSet = DaoUtil.executeStatement(conn, statement, true, email);
        return resultSet.next();
    }

    public void register(Connection conn, Login login) throws SQLException {
        String statement = "INSERT INTO user (email, secured_password, role_id) VALUES (?,?,?)";

        Hash hash = Password.hash(login.getPassword()).addRandomSalt(12).withArgon2();
        String password = hash.getResult();

        DaoUtil.executeStatement(conn, statement, false,
                login.getEmail(), password, RoleID.EMPLOYEE);
    }
}