package org.androidtown.login;

public class friendInformation {
    public String name;
    public String phone;
    public String state;

    public friendInformation(String name, String phone)
    {
        this.name = name;
        this.phone = phone;
    }

    public String getName()
    {return name;}

    public String getPhone()
    {
        return phone;
    }

    public String getState(){return state;}

    public void setName(String name)
    {
        this.name = name;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public void setState(String state){this.state = state;}

}
