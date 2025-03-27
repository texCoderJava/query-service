package com.sbr.common.model;

import lombok.*;

@AllArgsConstructor(staticName = "instance")
@NoArgsConstructor(staticName = "instance")
@Data
@Builder
public class H3Detail {
    @NonNull
    private LatLng location;
    private Integer resolution;
    private Integer radius;
}
