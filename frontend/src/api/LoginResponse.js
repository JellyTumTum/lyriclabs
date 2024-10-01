class LoginResponse {
    constructor(userId, username, email, jwt, responseMessage) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.jwt = jwt;
        this.responseMessage = responseMessage;
    }
}

export default LoginResponse;
