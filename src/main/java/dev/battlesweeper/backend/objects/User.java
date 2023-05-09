package dev.battlesweeper.backend.objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Builder
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) @AllArgsConstructor
public class User {

        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        Long id;

        String email;

        String pwHash;

        String name;

        Long joinMillis = System.currentTimeMillis();
}
