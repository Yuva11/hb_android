package com.HungryBells.DTO;

import android.os.Parcel;
import android.os.Parcelable;

import com.felipecsl.asymmetricgridview.library.model.AsymmetricItem;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class ContentDealDTO implements Serializable, AsymmetricItem {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    Long id;

    MerchantDto merchant;

    String name;

    String contentType;//Promotional OR Non-promotional

    ContentTemplate contentTemplate;
/*  1) Text only ( Text + Text),
 *  2) Image Only ( Image+image),
 *  3) Video only ( IMAGE OF VIDEO + video),
 *  4) Video + embedded text,
 *  5) Text + Image&Text*/

    String thumbnailText; // contentTemplate -1

    String detailText;  // contentTemplate -1

    String thumbNailURL;

    String displayURL;

    String videoURL;

    String startDate;

    String endDate;

    Integer viewCount;

    String createdDate; //Content Created Date

    String modifiedDate; //Last Modified Date

    Object location;

    Integer tileSize;

    DealStatus status;

    List<Customers> customer;

    Boolean isliked;

    List<AdFeedbackDTO> feedbacks;

    List<AdViewsDTO> views;

    Integer likeCount;

    Integer feedBackCount;








    /*
    Variable Sized Grid related method
     */
    private int columnSpan;
    private int rowSpan;
    private int position;

    public ContentDealDTO() {
        this(1, 1, 0);
    }

    public ContentDealDTO(final int columnSpan, final int rowSpan, int position) {
        this.columnSpan = columnSpan;
        this.rowSpan = rowSpan;
        this.position = position;
    }

    public void setColumnSpan(int colSpan) {
        this.columnSpan = colSpan;
    }

    public void setRowSpan(int rowSpan) {
        this.rowSpan = rowSpan;
    }

    public ContentDealDTO(final Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int getColumnSpan() {
        return columnSpan;
    }

    @Override
    public int getRowSpan() {
        return rowSpan;
    }

    public int getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return String.format("%s: %sx%s", position, rowSpan, columnSpan);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private void readFromParcel(final Parcel in) {
        columnSpan = in.readInt();
        rowSpan = in.readInt();
        position = in.readInt();
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeInt(columnSpan);
        dest.writeInt(rowSpan);
        dest.writeInt(position);
    }

    /* Parcelable interface implementation */
    public static final Parcelable.Creator<ContentDealDTO> CREATOR = new Parcelable.Creator<ContentDealDTO>() {

        @Override
        public ContentDealDTO createFromParcel(final Parcel in) {
            return new ContentDealDTO(in);
        }

        @Override
        public ContentDealDTO[] newArray(final int size) {
            return new ContentDealDTO[size];
        }
    };

}
