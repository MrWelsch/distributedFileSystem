package com.filesystem.server.entities;

import com.mongodb.lang.NonNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class DataClientConnection implements Cloneable {

    @Id
    private String id;
    @NonNull
    private String ipv4;
    private String port = "8080";
    private String name = "";

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
