/**
 * Class representing the input state update message that is sent to the server by the client.
 */
class ClientInputStateUpdate {
    public username: string;
    public authString: string;
    public movingUp: boolean;
    public movingLeft: boolean;
    public movingRight: boolean;
    public movingDown: boolean;
    public shooting: boolean;
    public angle: number;
}