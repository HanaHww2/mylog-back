package me.study.mylog.board.service;

import lombok.RequiredArgsConstructor;
import me.study.mylog.board.entity.Board;
import me.study.mylog.board.entity.BoardMemberType;
import me.study.mylog.board.mapper.BoardMemberMapper;
import me.study.mylog.board.repository.BoardMemberRepository;
import me.study.mylog.users.domain.User;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BoardMemberWriteService {
    private final BoardMemberRepository boardMemberRepository;

    public void addFirstBoardMember(Board board, User user, String nickname) {

        var boardMember = BoardMemberMapper.toEntity(board, user, BoardMemberType.MANAGER, nickname);
        boardMemberRepository.save(boardMember);
    }
}
