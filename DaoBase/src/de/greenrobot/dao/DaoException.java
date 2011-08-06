package de.greenrobot.dao;

import android.database.SQLException;

/**
 * Exception thrown when something goes wrong in the DAO/ORM layer.
 * 
 * @author Markus
 *
 */
public class DaoException extends SQLException {

    public DaoException() {
    }

    public DaoException(String error) {
        super(error);
    }

}
