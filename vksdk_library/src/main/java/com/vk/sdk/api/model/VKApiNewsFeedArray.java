package com.vk.sdk.api.model;

import android.os.Parcel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VKApiNewsFeedArray extends VKList<VKApiNews> {

    private String next_from = "";
    private VKUsersArray profiles = new VKUsersArray();
    private VKApiCommunityArray groups = new VKApiCommunityArray();

    public static Creator<VKPhotoArray> CREATOR = new Creator<VKPhotoArray>() {
        public VKPhotoArray createFromParcel(Parcel source) {
            return new VKPhotoArray(source);
        }

        public VKPhotoArray[] newArray(int size) {
            return new VKPhotoArray[size];
        }
    };

    @SuppressWarnings("unused")
    public VKApiNewsFeedArray() {
    }

    public VKApiNewsFeedArray(Parcel in) {
        super(in);
        this.next_from = in.readString();
        this.profiles = in.readParcelable(VKUsersArray.class.getClassLoader());
        this.groups = in.readParcelable(VKApiCommunityArray.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.next_from);
        dest.writeParcelable(this.profiles, flags);
        dest.writeParcelable(this.groups, flags);
    }

    @Override
    public VKApiModel parse(JSONObject response) throws JSONException {
        fill(response, VKApiNews.class);
        if (response.has("response")) {
            JSONObject resp = response.optJSONObject("response");
            next_from = resp.optString("next_from", next_from);
            profiles.fill(resp.optJSONArray("profiles"), VKApiUserFull.class);
            groups.fill(resp.optJSONArray("groups"), VKApiCommunityFull.class);
        }
        return this;
    }

    public String getNextFrom() {
        return next_from;
    }

    public VKUsersArray getProfiles() {
        return profiles;
    }

    public VKApiCommunityArray getGroups() {
        return groups;
    }
}