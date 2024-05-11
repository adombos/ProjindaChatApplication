# Chat Application in Java

## Project Description 
A chat application in Java that allows you to send and receive messages. It consists of two main componenets: a client and a server. 
The server sends the messages it receives from any client to all the clients except the sender. The application uses sockets and a Swing GUI. 

## Usage
Clone the repository and run the server by typing  `java Server <port number>`. Then, start the client by typing `java Client <Server name> <port number>`.
For example, `java Server 48620` and `java Client localhost 48620`. 

Type messages in the GUI and not in the terminal for all functinalities. To exit the chat type "exit" in some form. 
