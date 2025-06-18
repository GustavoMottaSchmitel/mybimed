package motta.dev.MyBimed.dto;

import lombok.Data;

import java.util.List;

@Data
public class WhatsAppWebhookDTO {
    private String object;
    private List<Entry> entry;

    @Data
    public static class Entry {
        private String id;
        private List<Change> changes;
    }

    @Data
    public static class Change {
        private Value value;
        private String field;
    }

    @Data
    public static class Value {
        private List<Message> messages;
        private List<Contact> contacts;
        private Metadata metadata;
    }

    @Data
    public static class Message {
        private String from;
        private String id;
        private String timestamp;
        private Text text;
        private String type;
    }

    @Data
    public static class Text {
        private String body;
    }

    @Data
    public static class Contact {
        private Profile profile;
        private String wa_id;
    }

    @Data
    public static class Profile {
        private String name;
    }

    @Data
    public static class Metadata {
        private String display_phone_number;
        private String phone_number_id;
    }
}
