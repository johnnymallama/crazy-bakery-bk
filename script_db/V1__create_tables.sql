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
