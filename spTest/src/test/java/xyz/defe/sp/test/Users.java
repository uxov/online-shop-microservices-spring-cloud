package xyz.defe.sp.test;

public enum Users {
    ALEN("Alen", "alen", "female", 23, "123"),
    MIKE("Mike", "mike", "male", 22, "123"),
    JOHN("John", "john", "male", 23, "123");

    public String name;
    public String uname;
    public String sex;
    public Integer age;
    public String pwd;

    Users(String name, String uname, String sex, Integer age, String pwd) {
        this.name = name;
        this.uname = uname;
        this.sex = sex;
        this.age = age;
        this.pwd = pwd;
    }
}