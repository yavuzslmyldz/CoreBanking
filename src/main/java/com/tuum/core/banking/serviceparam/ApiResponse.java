package com.tuum.core.banking.serviceparam;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
    public class ApiResponse<T> {
    @JsonProperty("payload")
    private T data;
    @JsonProperty("messages")
    private List<String> messages;

        public ApiResponse() {
            super();
        }

        public ApiResponse(T _data, List<String> _messages) {
            data = _data;
            messages = _messages;
        }


    /*    public ApiResponse(List<String> messages) {
            this.messages = messages;
        }

        public ApiResponse(T data) {
            this.data = data;
        } */

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }

        public List<String> getMessages() {
            return messages;
        }

        public void setMessages(List<String> messages) {
            this.messages = messages;
        }

}
