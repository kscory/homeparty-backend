package com.homeparty.invitation.domain.commands;

import abstraction.command.Command;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@AllArgsConstructor
@Getter
@ToString
public class CreateInvitationCardCommand implements Command {
    private UUID cardId;
    private UUID uploaderId;
}
