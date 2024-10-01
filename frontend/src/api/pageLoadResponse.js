class pageLoadResponse {
    constructor(username, token, responseMessage) {
        this.username = username;
        this.token = token;
        this.responseMessage = responseMessage
        this.loaded = true;
    }
}

export default pageLoadResponse;
