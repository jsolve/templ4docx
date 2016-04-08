package pl.jsolve.templ4docx;

import java.util.List;

public class Student {
    private String name;
    private Integer age;
    private String logoPath;
    private List<String> languages;
    private String content;
    private String a;
    private String b;
    private String c;

    public Student(String name, Integer age, String logoPath, List<String> languages) {
        this.name = name;
        this.age = age;
        this.logoPath = logoPath;
        this.languages = languages;
    }

    public Student(String name, Integer age, String logoPath, List<String> languages, String content) {
        this.name = name;
        this.age = age;
        this.logoPath = logoPath;
        this.languages = languages;
        this.content = content;
    }

    public Student(String name, Integer age, String logoPath, List<String> languages, String content, String a, String b, String c) {
        this.name = name;
        this.age = age;
        this.logoPath = logoPath;
        this.languages = languages;
        this.content = content;
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}