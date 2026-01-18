CREATE TABLE usuario (
    id VARCHAR(255) NOT NULL,
    email VARCHAR(255) NULL,
    nombre VARCHAR(255) NULL,
    apellido VARCHAR(255) NULL,
    tipo VARCHAR(255) NULL,
    telefono VARCHAR(255) NULL,
    direccion VARCHAR(255) NULL,
    departamento VARCHAR(255) NULL,
    ciudad VARCHAR(255) NULL,
    CONSTRAINT pk_usuario PRIMARY KEY (id)
);

ALTER TABLE usuario ADD COLUMN estado BOOLEAN;

CREATE TABLE ingrediente (
    id BIGINT AUTO_INCREMENT NOT NULL,
    nombre VARCHAR(255) NULL,
    composicion VARCHAR(255) NULL,
    tipo_ingrediente VARCHAR(255) NULL,
    valor FLOAT NULL,
    CONSTRAINT pk_ingrediente PRIMARY KEY (id)
);

ALTER TABLE ingrediente ADD COLUMN estado BOOLEAN;

CREATE TABLE tamano (
    id BIGINT AUTO_INCREMENT NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    alto INT NOT NULL,
    diametro INT NOT NULL,
    porciones INT NOT NULL,
    tipo_receta VARCHAR(255) NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_tamano PRIMARY KEY (id)
);

CREATE TABLE tamano_tipo_ingrediente (
    id BIGINT AUTO_INCREMENT NOT NULL,
    tamano_id BIGINT NOT NULL,
    tipo_ingrediente VARCHAR(255) NOT NULL,
    gramos FLOAT NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_tamano_tipo_ingrediente PRIMARY KEY (id),
    CONSTRAINT fk_tamano FOREIGN KEY (tamano_id) REFERENCES tamano (id)
);

-- Script para crear la tabla 'torta'
CREATE TABLE torta (
    id BIGINT AUTO_INCREMENT NOT NULL,
    bizcocho_id BIGINT NOT NULL,
    relleno_id BIGINT NOT NULL,
    cubertura_id BIGINT NOT NULL,
    tamano_id BIGINT NOT NULL,
    valor REAL NOT NULL,
    estado BOOLEAN NOT NULL,
    CONSTRAINT pk_torta PRIMARY KEY (id),
    CONSTRAINT fk_torta_bizcocho FOREIGN KEY (bizcocho_id) REFERENCES ingrediente(id),
    CONSTRAINT fk_torta_relleno FOREIGN KEY (relleno_id) REFERENCES ingrediente(id),
    CONSTRAINT fk_torta_cubertura FOREIGN KEY (cubertura_id) REFERENCES ingrediente(id),
    CONSTRAINT fk_torta_tamano FOREIGN KEY (tamano_id) REFERENCES tamano(id)
);

-- Tabla para Receta
CREATE TABLE receta (
    id BIGINT AUTO_INCREMENT NOT NULL,
    tipo_receta VARCHAR(255) NOT NULL,
    torta_id BIGINT NOT NULL,
    cantidad INT NOT NULL,
    valor FLOAT NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_receta PRIMARY KEY (id),
    CONSTRAINT fk_receta_torta FOREIGN KEY (torta_id) REFERENCES torta (id)
);
