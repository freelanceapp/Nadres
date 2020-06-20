package com.endpoint.nadres.models;

import com.endpoint.nadres.interfaces.Listeners;

import java.io.Serializable;
import java.util.List;

public class StageDataModel implements Serializable {
    private List<Stage> data;

    public List<Stage> getData() {
        return data;
    }

    public static class Stage implements Serializable {
        private int id;
        private String title;
        private List<ClassesFk> classes_fk;

        public Stage() {
        }

        public Stage(int id, String title) {
            this.id = id;
            this.title = title;
        }

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public Stage(String title) {
            this.title = title;
        }

        public List<ClassesFk> getClasses_fk() {
            return classes_fk;
        }

        public static class ClassesFk implements Serializable {
            private int id;
            private String title;

            public ClassesFk() {
            }

            public ClassesFk(int id, String title) {
                this.id = id;
                this.title = title;
            }

            public int getId() {
                return id;
            }

            public String getTitle() {
                return title;
            }

            public ClassesFk(String title) {
                this.title = title;
            }
        }
    }
}