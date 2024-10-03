package org.black_matter.monospace.util;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListHashMap<K, V> {
	
	@Getter
	@Accessors(fluent = true)
	private final HashMap<K, List<V>> map;
	
	private int totalSize = 0;
	
	public ListHashMap(int initialCapacity, float loadFactor) {
		this.map = new HashMap<>(initialCapacity, loadFactor);
	}
	
	public ListHashMap(int initialCapacity) {
		this(initialCapacity, 0.75f);
	}
	
	public ListHashMap() {
		this(16, 0.75f);
	}
	
	public void add(K key, V value) {
		var list = map.get(key);
		
		if(list == null) {
			list = new ArrayList<>();
			map.put(key, list);
		}
		
		list.add(value);
		totalSize++;
	}
	
	public List<V> get(K key) {
		return map.get(key);
	}
	
	public int size() {
		return map.size();
	}
	
	public int totalSize() {
		return totalSize;
	}
}
