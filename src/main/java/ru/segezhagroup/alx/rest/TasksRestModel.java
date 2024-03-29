package ru.segezhagroup.alx.rest;

import javax.xml.bind.annotation.*;
@XmlRootElement(name = "message")
@XmlAccessorType(XmlAccessType.FIELD)
public class TasksRestModel {

    @XmlElement(name = "value")
    private String message;

    public TasksRestModel() {
    }

    public TasksRestModel(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}