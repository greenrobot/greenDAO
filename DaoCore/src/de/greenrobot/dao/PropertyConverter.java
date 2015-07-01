package de.greenrobot.dao;

/** Implement this to use custom types in your entity. */
public interface PropertyConverter<P, D> {
    P convertToEntityProperty(D databaseValue);

    D convertToDatabaseValue(P entityProperty);
}
