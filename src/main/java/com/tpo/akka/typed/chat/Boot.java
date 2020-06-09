package com.tpo.akka.typed.chat;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.Terminated;
import akka.actor.typed.javadsl.Behaviors;
import lombok.val;

public class Boot {

  public static Behavior<Void> create() {
    return Behaviors.setup(
        context -> {
          val chatRoom = context.spawn(ChatRoom.create(), "chatRoom");
          val gabbler = context.spawn(Gabbler.create(), "gabbler");
          context.watch(gabbler);

          chatRoom.tell(ChatRoom.GetSession.of("Gabbler", gabbler));

          return Behaviors.receive(Void.class)
              .onSignal(Terminated.class, sig -> Behaviors.stopped())
              .build();
        });
  }

  public static void main(String[] args) {
    ActorSystem.create(Boot.create(), "ChatRoom");
  }

}