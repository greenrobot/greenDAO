package de.greenrobot.dao.test;

// THIS CODE IS GENERATED, DO NOT EDIT.
/** 
 * Entity mapped to table DATE_ENTITY (schema version 1).
*/
public class DateEntity {

    private Long id; 
    private java.util.Date date; 
    private java.util.Date dateNotNull; 

    public DateEntity() {
    }

    public DateEntity(Long id) {
        this.id = id;
    }

    public DateEntity(Long id, java.util.Date date, java.util.Date dateNotNull) {
        this.id = id;
        this.date = date;
        this.dateNotNull = dateNotNull;
    }

    public Long getId() {
        return id;
    } 

    public void setId(Long id) {
        this.id = id;
    } 

    public java.util.Date getDate() {
        return date;
    } 

    public void setDate(java.util.Date date) {
        this.date = date;
    } 

    public java.util.Date getDateNotNull() {
        return dateNotNull;
    } 

    public void setDateNotNull(java.util.Date dateNotNull) {
        this.dateNotNull = dateNotNull;
    } 


}
