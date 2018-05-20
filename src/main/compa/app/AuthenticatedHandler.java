package main.compa.app;

@FunctionalInterface
public interface AuthenticatedHandler<User, E> {
    void handle(User u, E event);
}