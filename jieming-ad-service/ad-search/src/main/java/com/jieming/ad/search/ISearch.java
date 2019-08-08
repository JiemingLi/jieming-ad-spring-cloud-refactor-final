package com.jieming.ad.search;

import com.jieming.ad.search.vo.SearchRequest;
import com.jieming.ad.search.vo.SearchResponse;

public interface ISearch {
    SearchResponse fetchAds(SearchRequest request);
}
