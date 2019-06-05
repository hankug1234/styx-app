package org.androidtown.login;

public class AptItem {
    public String timeT,titleT,numberT="0";
    public String rid;

    public void setTime(String time)
    {
        timeT = time;
    }

    public void setTitle(String title)
    {
        titleT = title;
    }

    public void setNumber(String num)
    {
        numberT = num;
    }

    public String getTimeT()
    {
        return this.timeT;
    }

    public String getTitleT()
    {
        return this.titleT;
    }

    public String getNum()
    {
        return this.numberT;
    }

    public void setRID(String uid)
    {
        this.rid = uid;
    }

    public String getRID()
    {
        return this.rid;
    }
}
