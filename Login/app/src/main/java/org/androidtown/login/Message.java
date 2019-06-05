package org.androidtown.login;

public class Message {
    public  String text;
    public MemberData memberData;
    public boolean belongsToCurrentUser;

    public Message(String text,MemberData data, boolean belongsToCurrentUser) {
        this.text = text;
        this.memberData = data;
        this.belongsToCurrentUser = belongsToCurrentUser;
    }

    public String getText() {
        return text;
    }

   public MemberData getMemberData() {
        return memberData;
    }

    public boolean isBelongsToCurrentUser() {
        return belongsToCurrentUser;
    }
}
