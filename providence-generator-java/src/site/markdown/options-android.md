Providence Android Parcelable
=============================

Providence supports android `Parcelable` serialization. Adding the `android`
generator param will add the `android.os.Parcelable` interface to *all*
messages, and add a static `CREATOR` field for the parcelable creator.

```java
class MyMessage extends PMessage<MyMessage,MyMessage._Field> implements Parcelable {
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel) {
        // ...
    }

    public static final Parcelable.Creator<MyMessage> CREATOR = new Parcelable.Creator<>() {
        // ...
    };
}
```

This way the generated objects can be used as stateful objects when sending
intents between android processes.
