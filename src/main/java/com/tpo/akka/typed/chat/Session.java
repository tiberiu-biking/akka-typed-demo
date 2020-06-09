package com.tpo.akka.typed.chat;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;

public class Session {

  public static Behavior<SessionCommand> create(ActorRef<ChatRoom.ChatRoomCommand> room, String screenName, ActorRef<SessionEvent> client) {
    return Behaviors.receive(SessionCommand.class)
        .onMessage(PostMessage.class, post -> onPostMessage(room, screenName, post))
        .onMessage(NotifyClient.class, notification -> onNotifyClient(client, notification))
        .build();
  }

  private static Behavior<SessionCommand> onPostMessage(ActorRef<ChatRoom.ChatRoomCommand> room, String screenName, PostMessage post) {
    room.tell(ChatRoom.PublishSessionMessage.of(screenName, post.getMessage()));
    return Behaviors.same();
  }

  private static Behavior<SessionCommand> onNotifyClient(ActorRef<SessionEvent> client, NotifyClient notification) {
    client.tell(notification.getMessage());
    return Behaviors.same();
  }

}
