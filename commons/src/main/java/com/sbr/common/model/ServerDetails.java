package com.sbr.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor(staticName = "instance")
@NoArgsConstructor(staticName = "instance")
@Data
@Builder
public class ServerDetails {
    private String id;
    private String name;
    private String host;
    private Integer port;
    private LatLng location;
    private String application;
}
