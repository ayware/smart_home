package com.ayware.smarthome.enums;

public enum ItemType {

    ON_OFF{
        @Override
        public String title() {
            return "Aç-Kapa";
        }
    },
    VALUE{
        @Override
        public String title() {
            return "Değer Yollama";
        }
    },
    READ{
        @Override
        public String title() {
            return "Değer Okuma";
        }
    };

    public abstract String title();

}
