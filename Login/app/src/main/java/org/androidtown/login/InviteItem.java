package org.androidtown.login;

public class InviteItem {
    public String title ,date, num="0", RID,inviteid;

    public  InviteItem()
    {}


    public InviteItem(String title,String date, String num, String RID )
    {
        this.title = title; this.date = date; this.num = num; this.RID = RID;
    }

    public void setInviteid(String s)
    {
        inviteid = s;
    }

    public void setTitle(String s)
    {
        title = s;
    }

    public void setDate(String s)
    {
        date = s;
    }

    public void setNum(String s)
    {
        num = s;
    }

    public void setRID(String s)
    {
        RID = s;
    }

    public String getTitle()
    {
        return title;
    }

    public String getDate()
    {
        return date;
    }

    public String getNum()
    {
        return num;
    }
    public String getRID()
    {
        return RID;
    }

    public String getInviteid()
    {
        return inviteid;
    }

}
