package com.example.kbasic.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class TopicsProperties {

    private Topics topics = new Topics();
    private Demo demo = new Demo();

    public Topics getTopics() {
        return topics;
    }

    public void setTopics(Topics topics) {
        this.topics = topics;
    }

    public Demo getDemo() {
        return demo;
    }

    public void setDemo(Demo demo) {
        this.demo = demo;
    }

    public static class Topics {
        private String input;
        private String highValue;
        private String lowValue;
        private String points;

        public String getInput() { return input; }
        public void setInput(String input) { this.input = input; }

        public String getHighValue() { return highValue; }
        public void setHighValue(String highValue) { this.highValue = highValue; }

        public String getLowValue() { return lowValue; }
        public void setLowValue(String lowValue) { this.lowValue = lowValue; }

        public String getPoints() { return points; }
        public void setPoints(String points) { this.points = points; }
    }

    public static class Demo {
        private int highValueThreshold = 100_000;
        private int pointsFactor = 1000;
        private int bigGiftThreshold = 200;
        private int smallGiftThreshold = 50;

        public int getHighValueThreshold() { return highValueThreshold; }
        public void setHighValueThreshold(int highValueThreshold) { this.highValueThreshold = highValueThreshold; }

        public int getPointsFactor() { return pointsFactor; }
        public void setPointsFactor(int pointsFactor) { this.pointsFactor = pointsFactor; }

        public int getBigGiftThreshold() { return bigGiftThreshold; }
        public void setBigGiftThreshold(int bigGiftThreshold) { this.bigGiftThreshold = bigGiftThreshold; }

        public int getSmallGiftThreshold() { return smallGiftThreshold; }
        public void setSmallGiftThreshold(int smallGiftThreshold) { this.smallGiftThreshold = smallGiftThreshold; }
    }
}
