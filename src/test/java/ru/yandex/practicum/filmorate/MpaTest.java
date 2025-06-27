package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaDbStorage;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({MpaDbStorage.class})

public class MpaTest {

    private final MpaDbStorage mpaDbStorage;
    private static final int SIZE_MPA_LIST = 5;

    @Test
    void shouldGetAllMpa() {
        Collection<Mpa> mpa = mpaDbStorage.getAllMpa();
        assertNotNull(mpa);
        assertEquals(mpa.size(), SIZE_MPA_LIST);
    }

    @Test
    void shouldGetMpaById() {
        int id = 1;
        Mpa mpa = mpaDbStorage.getMpaById((long)id)
                .orElseThrow(() -> new NotFoundException("Возрастное ограничение не найдено!"));
        assertNotNull(mpa);
        assertThat(mpa).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(mpa).hasFieldOrPropertyWithValue("name", "G");
    }
}