package com.endpoint.nadres.models;

import java.io.Serializable;
import java.util.List;

public class CreateRoomModel implements Serializable {

    private int teacher_id;
    private String skill_type;
    private List<Integer> student_ids;

    public int getTeacher_id() {
        return teacher_id;
    }

    public void setTeacher_id(int teacher_id) {
        this.teacher_id = teacher_id;
    }

    public String getSkill_type() {
        return skill_type;
    }

    public void setSkill_type(String skill_type) {
        this.skill_type = skill_type;
    }

    public List<Integer> getStudent_ids() {
        return student_ids;
    }

    public void setStudent_ids(List<Integer> student_ids) {
        this.student_ids = student_ids;
    }
}