package com.homeparty.invitation.domain.commands;

import abstraction.command.CommandHandler;
import com.homeparty.invitation.domain.aggregates.invitation.InvitationLocation;
import com.homeparty.invitation.domain.aggregates.invitation.InvitationRepository;
import com.homeparty.invitation.domain.aggregates.invitation.Invitation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateInvitationCommandHandler implements CommandHandler<CreateInvitationCommand, UUID> {

    private final InvitationRepository invitationRepository;

    public UUID handle(CreateInvitationCommand command) {
        Invitation invitation = new Invitation(
                command.getInvitationId(),
                command.getHostId(),
                command.getTitle(),
                command.getDescription(),
                command.getType(),
                command.getDressCodes(),
                command.getFoods(),
                command.getPartiedAt(),
                new InvitationLocation(
                        command.getLocationName(),
                        command.getLatitude(),
                        command.getLongitude()
                )
        );
        invitationRepository.save(invitation);

        return invitation.getId();
    }

}
