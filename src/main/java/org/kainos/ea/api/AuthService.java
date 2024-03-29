package org.kainos.ea.api;

import org.kainos.ea.cli.Login;
import org.kainos.ea.client.FailedToGenerateTokenException;
import org.kainos.ea.client.FailedToLoginException;
import org.kainos.ea.client.FailedToValidateLoginException;
import org.kainos.ea.db.AuthDao;
import org.kainos.ea.db.DatabaseConnector;

import java.sql.Connection;
import java.sql.SQLException;

public class AuthService {
    private final AuthDao authDao;
    private final DatabaseConnector databaseConnector;

    public AuthService(AuthDao authDao, DatabaseConnector databaseConnector) {
        this.authDao = authDao;
        this.databaseConnector = databaseConnector;
    }

    public boolean ping() {
        try {
            Connection conn = databaseConnector.getConnection();
            return authDao.ping(conn);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public String login(Login login)
            throws FailedToGenerateTokenException,
            FailedToValidateLoginException, FailedToLoginException {
        try {
            Connection conn = databaseConnector.getConnection();
            if (authDao.isValidLogin(conn, login)) {
                try {
                    return authDao.generateJWT(conn, login.getEmail());
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                    throw new FailedToGenerateTokenException();
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            throw new FailedToValidateLoginException();
        }

        throw new FailedToLoginException();
    }
}
