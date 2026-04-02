package com.vince.gulimall.search.service;

import com.vince.common.es.SkuEsModel;

import java.io.IOException;
import java.util.List;


public interface ProductSaveService {

    boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException;
}
