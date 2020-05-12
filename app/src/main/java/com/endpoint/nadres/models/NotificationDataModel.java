package com.endpoint.nadres.models;

import java.io.Serializable;
import java.util.List;

public class NotificationDataModel implements Serializable {

    private List<NotificationModel> data;
private int current_page;
    public List<NotificationModel> getData() {
        return data;
    }

    public int getCurrent_page() {
        return current_page;
    }

    public static class NotificationModel implements Serializable
    {
     private int id;
        private int from_user_id;
        private int to_user_id;
        private int notification_type;
        private int action_type;
        private int is_read;
        private int order_id;
        private int notification_message_id;
        private String notification_date;
        private int offer_id;
        private String model_type;
        private int model_id;
        private String other_message;
           private Notification_message notification_message;

        public int getId() {
            return id;
        }

        public int getFrom_user_id() {
            return from_user_id;
        }

        public int getTo_user_id() {
            return to_user_id;
        }

        public int getNotification_type() {
            return notification_type;
        }

        public int getAction_type() {
            return action_type;
        }

        public int getIs_read() {
            return is_read;
        }

        public int getOrder_id() {
            return order_id;
        }

        public int getNotification_message_id() {
            return notification_message_id;
        }

        public int getOffer_id() {
            return offer_id;
        }

        public String getModel_type() {
            return model_type;
        }

        public int getModel_id() {
            return model_id;
        }

        public String getOther_message() {
            return other_message;
        }

        public String getNotification_date() {
            return notification_date;
        }

        public Notification_message getNotification_message() {
            return notification_message;
        }

        public class Notification_message implements Serializable {
       private int id;
               private String ar_title;
               private String en_title;
               private String ar_desc;
               private String en_desc;

               private String title;
               private String desc;

            public int getId() {
                return id;
            }

            public String getAr_title() {
                return ar_title;
            }

            public String getEn_title() {
                return en_title;
            }

            public String getAr_desc() {
                return ar_desc;
            }

            public String getEn_desc() {
                return en_desc;
            }

            public String getTitle() {
                return title;
            }

            public String getDesc() {
                return desc;
            }
        }
    }
}
