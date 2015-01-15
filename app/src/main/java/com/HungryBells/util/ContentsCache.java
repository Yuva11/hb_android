package com.HungryBells.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;



import com.HungryBells.DTO.ContentDTO;


public class ContentsCache {
	private static ContentsCache instance;
	Map<String, ContentDTO> contents;

    public static void setInstance(ContentsCache instance) {
        ContentsCache.instance = instance;
    }

    public Map<String, ContentDTO> getContents() {
        return contents;
    }

    public void setContents(Map<String, ContentDTO> contents) {
        this.contents = contents;
    }

    private ContentsCache() {
		contents = new HashMap<String, ContentDTO>();

	}

	public static synchronized ContentsCache getInstance() {
		if (instance == null) {
			instance = new ContentsCache();
		}
		return instance;
	}

	public boolean getCacheContents(String location) {

		if (contents.containsKey(location)) {
			if (new Date().getTime() - contents.get(location).getCurrentTime() > (5 * 60 * 1000)) {
				return false;
			}
			return true;
		} else {
			return false;
		}
	}
}
