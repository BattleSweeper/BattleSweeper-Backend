package dev.battlesweeper.backend.rest;

import dev.battlesweeper.backend.db.UserService;
import dev.battlesweeper.backend.objects.rank.RankData;
import dev.battlesweeper.backend.socket.WebSocketConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController @Slf4j
public class RankHandler {

    private final UserService userService;

    public RankHandler(UserService service) {
        userService = service;
    }

    @GetMapping(WebSocketConfig.ROUTE_PREFIX + "/rank")
    public List<RankData> getRank(@RequestParam(required = false, defaultValue = "30") int limit) {
        if (limit == Integer.MAX_VALUE || limit <= 0)
            return null;
        return userService.findAllByOrderByClearTimeAsc(limit);
    }
}
