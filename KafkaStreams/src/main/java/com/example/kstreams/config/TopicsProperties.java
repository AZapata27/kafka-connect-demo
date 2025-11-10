package com.example.kstreams.config;

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
        private String perUserCount;
        private String perUserTotal;

        public String getInput() {
            return input;
        }

        public void setInput(String input) {
            this.input = input;
        }

        public String getHighValue() {
            return highValue;
        }

        public void setHighValue(String highValue) {
            this.highValue = highValue;
        }

        public String getLowValue() {
            return lowValue;
        }

        public void setLowValue(String lowValue) {
            this.lowValue = lowValue;
        }

        public String getPerUserCount() {
            return perUserCount;
        }

        public void setPerUserCount(String perUserCount) {
            this.perUserCount = perUserCount;
        }

        public String getPerUserTotal() {
            return perUserTotal;
        }

        public void setPerUserTotal(String perUserTotal) {
            this.perUserTotal = perUserTotal;
        }
    }

    public static class Demo {
        private int highValueThreshold = 100_000;

        public int getHighValueThreshold() {
            return highValueThreshold;
        }

        public void setHighValueThreshold(int highValueThreshold) {
            this.highValueThreshold = highValueThreshold;
        }
    }
}
