package de.greenrobot.daoexample;


// THIS CODE IS GENERATED, DO NOT EDIT.
/** 
 * Entity mapped to table NOTE (schema version 1).
*/
public class Note {

    private Long id; 
    private String text; 
    private String date; 

    public Note() {
    }

    public Note(Long id) {
        this.id = id;
    }

    public Note(Long id, String text, String date) {
        this.id = id;
        this.text = text;
        this.date = date;
    }

    public Long getId() {
        return id;
    } 

    public void setId(Long id) {
        this.id = id;
    } 

    public String getText() {
        return text;
    } 

    public void setText(String text) {
        this.text = text;
    } 

    public String getDate() {
        return date;
    } 

    public void setDate(String date) {
        this.date = date;
    } 

}
