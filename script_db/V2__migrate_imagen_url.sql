-- Migración: reemplazar URLs firmadas de Google Storage por URLs públicas de Firebase Storage
-- URL antigua: https://storage.googleapis.com/{bucket}/{filename}?GoogleAccessId=...
-- URL nueva:   https://firebasestorage.googleapis.com/v0/b/{bucket}/o/{filename}?alt=media

UPDATE receta
SET imagen_url = CONCAT(
    'https://firebasestorage.googleapis.com/v0/b/uan-especializacion.firebasestorage.app/o/',
    SUBSTRING_INDEX(
        SUBSTRING(imagen_url, LENGTH('https://storage.googleapis.com/uan-especializacion.firebasestorage.app/') + 1),
        '?',
        1
    ),
    '?alt=media'
)
WHERE imagen_url LIKE 'https://storage.googleapis.com/uan-especializacion.firebasestorage.app/%';
