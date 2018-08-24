package org.cboard.test;

import org.cboard.dto.ViewDashboardBoard;
import org.cboard.pojo.DashboardBoard;
import org.cboard.services.BoardService;
import org.cboard.services.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by ok_vince on 2018-05-12.
 */
@RestController
@RequestMapping("/restful")
public class DashboardRestful {
    @Autowired
    private BoardService boardService;

    @RequestMapping(value = "/board", method = RequestMethod.GET)
    public @ResponseBody
    ViewDashboardBoard findBoards(@RequestParam Long id) {
        ViewDashboardBoard board = boardService.getBoardData(id);
        return board;
    }


    public BoardService getBoardService() {
        return boardService;
    }

    public void setBoardService(BoardService boardService) {
        this.boardService = boardService;
    }
}
