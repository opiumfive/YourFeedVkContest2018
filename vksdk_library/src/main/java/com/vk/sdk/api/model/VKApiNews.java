package com.vk.sdk.api.model;

import android.os.Parcel;
import org.json.JSONException;
import org.json.JSONObject;

public class VKApiNews extends VKApiModel implements Identifiable, android.os.Parcelable {

    public static Creator<VKApiPost> CREATOR = new Creator<VKApiPost>() {
        public VKApiPost createFromParcel(Parcel source) {
            return new VKApiPost(source);
        }

        public VKApiPost[] newArray(int size) {
            return new VKApiPost[size];
        }
    };

    public int id;
    public long date;
    public String text;
    public String type;
    public long source_id;
    public long signer_id;
    public int post_id;
    public int marked_as_ads;
    public String post_type;
    public VKAttachments attachments = new VKAttachments();

    public VKApiNews(JSONObject from) throws JSONException {
        parse(from);
    }

    public VKApiNews(Parcel in) {
        this.id = in.readInt();
        this.date = in.readLong();
        this.text = in.readString();
        this.post_type = in.readString();
        this.attachments = in.readParcelable(VKAttachments.class.getClassLoader());
        this.type = in.readString();
        this.marked_as_ads = in.readInt();
        this.source_id = in.readLong();
        this.post_id = in.readInt();
        this.signer_id = in.readLong();
    }

    public VKApiNews() {

    }

    public VKApiNews parse(JSONObject source) throws JSONException {
        id = source.optInt("id");
        date = source.optLong("date");
        text = source.optString("text");
        post_type = source.optString("post_type");
        attachments.fill(source.optJSONArray("attachments"));
        type = source.optString("type");
        marked_as_ads = source.optInt("marked_as_ads");
        source_id = source.optLong("source_id");
        post_id = source.optInt("post_id");
        signer_id = source.optLong("signer_id");
        return this;
    }

    @Override
    public int getId() {
        return post_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeLong(this.date);
        dest.writeString(this.text);
        dest.writeString(this.post_type);
        dest.writeParcelable(attachments, flags);
        dest.writeString(this.type);
        dest.writeInt(this.marked_as_ads);
        dest.writeLong(this.source_id);
        dest.writeInt(this.post_id);
        dest.writeLong(this.signer_id);
    }

}
