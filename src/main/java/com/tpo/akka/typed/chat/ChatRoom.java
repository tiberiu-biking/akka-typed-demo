package com.tpo.akka.typed.chat;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import lombok.Value;
import lombok.val;

import java.util.ArrayList;
import java.util.List;

public class ChatRoom {

  public static Behavior<ChatRoomCommand> create() {
    return Behaviors.setup(ctx -> new ChatRoom(ctx).chatRoom(new ArrayList<>()));
  }

  private final ActorContext<ChatRoomCommand> context;

  private ChatRoom(ActorContext<ChatRoomCommand> context) {
    this.context = context;
  }

  private Behavior<ChatRoomCommand> chatRoom(List<ActorRef<SessionCommand>> sessions) {
    return Behaviors.receive(ChatRoomCommand.class)
        .onMessage(GetSession.class, getSession -> onGetSession(sessions, getSession))
        .onMessage(PublishSessionMessage.class, pub -> onPublishSessionMessage(sessions, pub))
        .build();
  }

  private Behavior<ChatRoomCommand> onGetSession(List<ActorRef<SessionCommand>> sessions, GetSession getSession) {
    val client = getSession.getReplyTo();

    val session = Session.create(context.getSelf(), getSession.getScreenName(), client);
    val ses = context.spawn(session, getSession.getScreenName());

    client.tell(SessionGranted.of(ses.narrow()));
    val newSessions = new ArrayList<>(sessions);
    newSessions.add(ses);

    return chatRoom(newSessions);
  }

  private Behavior<ChatRoomCommand> onPublishSessionMessage(List<ActorRef<SessionCommand>> sessions, PublishSessionMessage pub) {
    val message = MessagePosted.of(pub.getScreenName(), pub.getMessage());
    val notification = NotifyClient.of(message);

    sessions.forEach(s -> s.tell(notification));

    return Behaviors.same();
  }

  public interface ChatRoomCommand {
  }


  @Value(staticConstructor = "of")
  static class GetSession implements ChatRoomCommand {
    String screenName;
    ActorRef<SessionEvent> replyTo;
  }

  @Value(staticConstructor = "of")
  public static class PublishSessionMessage implements ChatRoomCommand {
    String screenName;
    String message;
  }

}
