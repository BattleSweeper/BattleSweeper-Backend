package dev.battlesweeper.backend.objects.rank;

import lombok.*;

@Builder @Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) @AllArgsConstructor
public class RankData {

    private Long id;
    private String name;
    private Long clearTime;
}
