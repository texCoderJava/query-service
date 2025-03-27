package com.sbr.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor(staticName = "instance")
@NoArgsConstructor(staticName = "instance")
@Data
@Builder
public class Pair<K, V> {
    private K key;
    private V value;
}
