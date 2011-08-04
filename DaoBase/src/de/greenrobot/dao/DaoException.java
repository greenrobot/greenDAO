package de.greenrobot.dao;

import android.database.SQLException;

public class DaoException extends SQLException {

    public DaoException() {
    }

    public DaoException(String error) {
        super(error);
    }

}
