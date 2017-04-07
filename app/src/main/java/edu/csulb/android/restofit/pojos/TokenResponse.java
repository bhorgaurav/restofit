package edu.csulb.android.restofit.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TokenResponse {

        @SerializedName("access_token")
        @Expose
        private String accessToken;
        @SerializedName("token_type")
        @Expose
        private String tokenType;
        @SerializedName("expires_in")
        @Expose
        private int expiresIn;

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getTokenType() {
            return tokenType;
        }

        public void setTokenType(String tokenType) {
            this.tokenType = tokenType;
        }

        public int getExpiresIn() {
            return expiresIn;
        }

        public void setExpiresIn(int expiresIn) {
            this.expiresIn = expiresIn;
        }

    }

