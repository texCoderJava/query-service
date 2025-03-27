package com.sbr.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "instance")
@AllArgsConstructor(staticName = "instance")
@Data
public class Distance {
    private String distance;
    private LatLng start;
    private LatLng end;
    private String startIndex;
    private String endIndex;
}
