package com.example;

public class House {
    private Integer i = 0;
    private Integer j = 0;
    private Integer score = 0;
    private String value;
    private Long expectedValue;
    private Integer passedTurnsToFollow = 0;

    public Integer getPassedTurnsToFollow() {
        return passedTurnsToFollow;
    }

    public void setPassedTurnsToFollow(Integer passedTurnsToFollow) {
        this.passedTurnsToFollow = passedTurnsToFollow;
    }

    public Base.Action getAction() {
        return action;
    }

    public void setAction(Base.Action action) {
        this.action = action;
    }

    Base.Action action;

    public Integer getI() {
        return i;
    }

    public void setI(Integer i) {
        this.i = i;
    }

    public Integer getJ() {
        return j;
    }

    public void setJ(Integer j) {
        this.j = j;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getExpectedValue() {
        return expectedValue;
    }

    public void setExpectedValue(Long expectedValue) {
        this.expectedValue = expectedValue;
    }
}
