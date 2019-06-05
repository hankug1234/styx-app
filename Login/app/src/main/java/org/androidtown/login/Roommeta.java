package org.androidtown.login;

public class Roommeta {
    public  String title,date,personnel="0";
    public Roommeta()
    {}
    public void setTitle(String s)
    {
        title =s;
    }
    public void setDate(String s)
    {
        date = s;
    }
    public void setPersonnel(String s)
    {
        personnel = s;
    }

   public String getTitle()
    {return title;}

    public String getDate()
    {
        return date;
    }

    public String getPersonnel()
    {
        return personnel;
    }
}
