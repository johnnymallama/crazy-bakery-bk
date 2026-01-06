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
    codigo BIGINT AUTO_INCREMENT NOT NULL,
    nombre VARCHAR(255) NULL,
    composicion VARCHAR(255) NULL,
    tipo_ingrediente VARCHAR(255) NULL,
    valor FLOAT NULL,
    CONSTRAINT pk_ingrediente PRIMARY KEY (codigo)
);
