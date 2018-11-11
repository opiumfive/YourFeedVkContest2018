package com.vk.sdk.api.methods;

import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.model.VKApiNewsFeedArray;

public class VkApiNewsFeed extends VKApiBase {

    public VKRequest getDiscover(VKParameters params) {
        return prepareRequest("getDiscoverForContestant", params, VKApiNewsFeedArray.class);
    }

    public VKRequest ignoreItem(VKParameters params) {
        return prepareRequest("ignoreItem", params);
    }

    @Override
    protected String getMethodsGroup() {
        return "newsfeed";
    }
}
