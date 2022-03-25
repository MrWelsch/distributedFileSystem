package com.filesystem.server.entities;

import lombok.Data;

@Data
public class InitDataClientDTO {

    private DataClientConnection dataClientConnection;
    private String initPath;

}
