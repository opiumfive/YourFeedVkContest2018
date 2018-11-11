package com.vk.sdk.api.methods;

import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;

public class VKApiLikes extends VKApiBase {

    public VKRequest add(VKParameters params) {
        return prepareRequest("add", params);
    }

    @Override
    protected String getMethodsGroup() {
        return "likes";
    }
}
