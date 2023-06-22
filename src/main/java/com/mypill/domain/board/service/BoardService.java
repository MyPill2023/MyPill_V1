package com.mypill.domain.board.service;

import com.mypill.domain.board.entity.Board;
import com.mypill.domain.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BoardService {
    private final BoardRepository boarRepository;

    public List<Board> getList() {
        return boarRepository.findAll();
    }
}
