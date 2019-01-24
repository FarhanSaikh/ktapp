package in.kolkatatailor.kolkatatailor;

public class UserModel {

    private  String name, userid,phonenumber,address,time,emailid,unread;
    UserModel(){}
    UserModel(String name, String userid,String phonenumber,String address,String time, String emailid ,String unread){

        this.name=name;
        this.userid=userid;
        this.phonenumber=phonenumber;
        this.address=address;
        this.time=time;
        this.emailid=emailid;
         this.unread=unread;

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getEmailid() {
        return emailid;
    }

    public void setEmailid(String emailid) {
        this.emailid = emailid;
    }


    public String getUnread() {
        return unread;
    }

    public void setUnread(String unread) {
        this.unread = unread;
    }
}
