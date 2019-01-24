package in.kolkatatailor.kolkatatailor;

public class TailorModel {


    private String userid,name,photourl,time,tailorcontact,type;

    TailorModel(){}
    TailorModel(String userid,String name,String photourl,String time,String tailorcontact, String type){

        this.userid=userid;
        this.name=name;
        this.photourl=photourl;
        this.time=time;
        this.tailorcontact=tailorcontact;
        this.type=type;

    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPhotourl() {
        return photourl;
    }

    public void setPhotourl(String photourl) {
        this.photourl = photourl;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTailorcontact() {
        return tailorcontact;
    }

    public void setTailorcontact(String tailorcontact) {
        this.tailorcontact = tailorcontact;
    }
}