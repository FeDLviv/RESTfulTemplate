package net.omisoft.rest;

//TODO SELECT, UPDATE, DELETE + Assert
public interface SQLQueryCounter {

    int getTotalCount();

    int getInsertCount();

    void reset();

}