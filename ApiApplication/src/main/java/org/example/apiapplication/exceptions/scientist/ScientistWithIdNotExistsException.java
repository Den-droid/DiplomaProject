package org.example.apiapplication.exceptions.scientist;

public class ScientistWithIdNotExistsException extends RuntimeException{
    public ScientistWithIdNotExistsException(Integer id){
        super(String.format("Scientist with id %d does not exist!", id));
    }
}
