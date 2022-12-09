package invitation.domain.commands;

import invitation.domain.aggregates.invitation.Invitation;
import invitation.domain.aggregates.invitation.InvitationRepository;
import invitation.domain.aggregates.invitationcomment.InvitationComment;
import invitation.domain.aggregates.invitationcomment.InvitationCommentRepository;
import invitation.domain.exception.InvitationException;
import invitation.domain.exception.InvitationExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentInvitationCommandHandler {
    private final InvitationRepository invitationRepository;
    private final InvitationCommentRepository invitationCommentRepository;

    public void handle(CommentInvitationCommand command) {
        Invitation invitation = invitationRepository.findById(command.getInvitationId())
                .orElseThrow(() -> new InvitationException(InvitationExceptionCode.NOT_FOUND_INVITATION));

        InvitationComment comment = new InvitationComment(
                command.getCommentId(),
                command.getInvitationId(),
                command.getUserId(),
                command.getName(),
                command.getContent(),
                command.isSecret(),
                LocalDateTime.now()
        );

        invitationCommentRepository.save(comment);
    }
}
