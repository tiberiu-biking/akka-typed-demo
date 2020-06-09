package com.tpo.akka.typed.chat;

import akka.actor.typed.ActorRef;
import lombok.Value;


interface SessionEvent {
}

@Value(staticConstructor = "of")
class SessionGranted implements SessionEvent {
  ActorRef<PostMessage> handle;
}

@Value(staticConstructor = "of")
class SessionDenied implements SessionEvent {
  String reason;
}

@Value(staticConstructor = "of")
class MessagePosted implements SessionEvent {
  String screenName;
  String message;
}

interface SessionCommand {
}

@Value(staticConstructor = "of")
class PostMessage implements SessionCommand {
  String message;
}

@Value(staticConstructor = "of")
class NotifyClient implements SessionCommand {
  MessagePosted message;
}
