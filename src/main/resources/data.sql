MERGE INTO genre AS target
USING (VALUES
    (1, 'Комедия'),
    (2, 'Драма'),
    (3, 'Мультфильм'),
    (4, 'Триллер'),
    (5, 'Документальный'),
    (6, 'Боевик')
) AS source (genre_id, name)
ON target.genre_id = source.genre_id
WHEN NOT MATCHED THEN
INSERT (genre_id, name) VALUES (source.genre_id, source.name);

MERGE INTO mpa AS target
USING (VALUES
    (1, 'G'),
    (2, 'PG'),
    (3, 'PG-13'),
    (4, 'R'),
    (5, 'NC-17')
) AS source (mpa_id, name)
ON target.mpa_id = source.mpa_id
WHEN NOT MATCHED THEN
INSERT (mpa_id, name) VALUES (source.mpa_id, source.name);