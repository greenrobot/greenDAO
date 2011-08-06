package de.greenrobot.dao;

import android.database.SQLException;
import android.util.Log;

/**
 * Exception thrown when something goes wrong in the DAO/ORM layer.
 * 
 * @author Markus
 * 
 */
public class DaoException extends SQLException {

    private static final long serialVersionUID = -5877937327907457779L;

    public DaoException() {
    }

    public DaoException(String error) {
        super(error);
    }

    public DaoException(String error, Throwable cause) {
        super(error);
        safeInitCause(cause);
    }

    public DaoException(Throwable th) {
        safeInitCause(th);
    }

    protected void safeInitCause(Throwable cause) {
        try {
            initCause(cause);
        } catch (Throwable e) {
            Log.e("greenDAO", "Could not set initial cause", e);
            Log.e("greenDAO", "Initial cause is:", cause);
        }
    }

}
