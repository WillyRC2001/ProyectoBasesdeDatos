USE master;
GO

ALTER DATABASE QUINIELAS SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
GO

DROP DATABASE QUINIELAS;
GO

CREATE DATABASE QUINIELAS;
GO

USE QUINIELAS;
GO

CREATE TABLE Usuarios(
    ID_Usuario INT PRIMARY KEY IDENTITY(1,1),
    Rol_Usuario VARCHAR(20) NOT NULL,
    Nombre_Usuario VARCHAR(50) NOT NULL UNIQUE,
    Contraseña VARCHAR(100) NOT NULL,
    Fecha_Nacimiento DATE NOT NULL
);

CREATE TABLE Correo_Electronico(
    ID_Correo INT PRIMARY KEY IDENTITY(1,1),
    ID_Usuario INT NOT NULL,
    Correo VARCHAR(50) NOT NULL UNIQUE,
    FOREIGN KEY (ID_Usuario) REFERENCES Usuarios(ID_Usuario)
);

CREATE TABLE Nombre_Completo(
    Usuario INT PRIMARY KEY,
    Nombre VARCHAR(50) NOT NULL,
    Segundo_Nombre VARCHAR(50),
    Primer_Apellido VARCHAR(50) NOT NULL,
    Segundo_Apellido VARCHAR(50) NOT NULL,
    FOREIGN KEY (Usuario) REFERENCES Usuarios(ID_Usuario)
);

CREATE TABLE Quiniela(
    ID_Quiniela INT PRIMARY KEY IDENTITY(1,1),
    Nombre_Quiniela VARCHAR(50) NOT NULL UNIQUE,
    Descripcion VARCHAR(255) NOT NULL,
    Fecha_Inicio DATE NOT NULL,
    Fecha_Fin DATE NOT NULL,
    Modalidad VARCHAR(20) NOT NULL,
    Tipo_Puntuacion VARCHAR(20) NOT NULL,
    Estado VARCHAR(20) NOT NULL DEFAULT 'Abierta',
    Reglas_Generales VARCHAR(255) NOT NULL
);

CREATE TABLE Equipo(
    Id_Equipo INT PRIMARY KEY IDENTITY(1,1),
    Pais VARCHAR(50) NOT NULL,
    Nombre_Equipo VARCHAR(50) NOT NULL
);

CREATE TABLE Partido(
    Id_Partido INT PRIMARY KEY IDENTITY(1,1),
    Estado VARCHAR(50) NOT NULL DEFAULT 'Pendiente',
    Fecha_Hora DATETIME NOT NULL,
    Goles_Local INT NOT NULL DEFAULT 0,
    Goles_Visitante INT NOT NULL DEFAULT 0
);

CREATE TABLE Participacion(
    Id_Participacion INT PRIMARY KEY IDENTITY(1,1),
    Equipo INT NOT NULL,
    Partido INT NOT NULL,
    Local_O_Visitante VARCHAR(20) NOT NULL,
    FOREIGN KEY (Equipo) REFERENCES Equipo(Id_Equipo),
    FOREIGN KEY (Partido) REFERENCES Partido(Id_Partido)
);

CREATE TABLE Partido_Quiniela(
    Partido INT NOT NULL,
    Quiniela INT NOT NULL,
    FOREIGN KEY (Partido) REFERENCES Partido(Id_Partido),
    FOREIGN KEY (Quiniela) REFERENCES Quiniela(ID_Quiniela),
    PRIMARY KEY (Partido, Quiniela)
);

CREATE TABLE Inscripcion(
    Id_Inscripcion INT PRIMARY KEY IDENTITY(1,1),
    Usuario INT NOT NULL,
    Quiniela INT NOT NULL,
    Acepta_Reglas VARCHAR(20) NOT NULL,
    FOREIGN KEY (Usuario) REFERENCES Usuarios(ID_Usuario),
    FOREIGN KEY (Quiniela) REFERENCES Quiniela(ID_Quiniela)
);

CREATE TABLE Puntuacion(
    Id_Puntuacion INT PRIMARY KEY IDENTITY(1,1),
    Puntos_Obtenidos INT NOT NULL,
    Fecha_Calculo DATE NOT NULL
);

CREATE TABLE Pronostico(
    Id_Pronostico INT PRIMARY KEY IDENTITY(1,1),
    Partido INT NOT NULL,
    Usuario INT NOT NULL,
    Quiniela INT NOT NULL,
    Puntuacion INT,
    Goles_Prediccion_Local INT NOT NULL,
    Goles_Prediccion_Visitante INT NOT NULL,
    Fecha_Hora_Ingreso DATETIME NOT NULL,
    FOREIGN KEY (Partido) REFERENCES Partido(Id_Partido),
    FOREIGN KEY (Usuario) REFERENCES Usuarios(ID_Usuario),
    FOREIGN KEY (Quiniela) REFERENCES Quiniela(ID_Quiniela),
    FOREIGN KEY (Puntuacion) REFERENCES Puntuacion(Id_Puntuacion)
);

-- DATOS DE PRUEBA

-- Equipos
INSERT INTO Equipo (Pais, Nombre_Equipo) VALUES ('España', 'Real Madrid');
INSERT INTO Equipo (Pais, Nombre_Equipo) VALUES ('España', 'Barcelona');
INSERT INTO Equipo (Pais, Nombre_Equipo) VALUES ('Inglaterra', 'Manchester City');
INSERT INTO Equipo (Pais, Nombre_Equipo) VALUES ('Inglaterra', 'Liverpool');

-- Quiniela
INSERT INTO Quiniela (Nombre_Quiniela, Descripcion, Fecha_Inicio, Fecha_Fin, Modalidad, Tipo_Puntuacion, Estado, Reglas_Generales)
VALUES ('Quiniela Mayo', 'Quiniela de prueba', '2026-05-01', '2026-05-31', 'Publica', 'Puntos', 'Abierta', 'Reglas generales de prueba');

-- Partidos
INSERT INTO Partido (Estado, Fecha_Hora) VALUES ('Pendiente', '2026-05-25T18:00:00');
INSERT INTO Partido (Estado, Fecha_Hora) VALUES ('Pendiente', '2026-05-26T20:00:00');

-- Participacion
INSERT INTO Participacion (Equipo, Partido, Local_O_Visitante) VALUES (1, 1, 'Local');
INSERT INTO Participacion (Equipo, Partido, Local_O_Visitante) VALUES (2, 1, 'Visitante');
INSERT INTO Participacion (Equipo, Partido, Local_O_Visitante) VALUES (3, 2, 'Local');
INSERT INTO Participacion (Equipo, Partido, Local_O_Visitante) VALUES (4, 2, 'Visitante');

-- Partido_Quiniela
INSERT INTO Partido_Quiniela (Partido, Quiniela) VALUES (1, 1);
INSERT INTO Partido_Quiniela (Partido, Quiniela) VALUES (2, 1);

--quinela 2
-- DATOS DE PRUEBA

-- Equipos
INSERT INTO Equipo (Pais, Nombre_Equipo) VALUES ('Alemania', 'Bayern Munich');
INSERT INTO Equipo (Pais, Nombre_Equipo) VALUES ('Francia', 'Paris Saint-Germain');
INSERT INTO Equipo (Pais, Nombre_Equipo) VALUES ('Italia', 'Juventus');
INSERT INTO Equipo (Pais, Nombre_Equipo) VALUES ('Portugal', 'Benfica');

-- Quiniela
INSERT INTO Quiniela (Nombre_Quiniela, Descripcion, Fecha_Inicio, Fecha_Fin, Modalidad, Tipo_Puntuacion, Estado, Reglas_Generales)
VALUES ('Quiniela Junio', 'Quiniela internacional de prueba', '2026-06-01', '2026-06-30', 'Privada', 'Puntos', 'Abierta', 'Reglas generales de junio');

-- Partidos
INSERT INTO Partido (Estado, Fecha_Hora) VALUES ('Pendiente', '2026-06-10T19:30:00');
INSERT INTO Partido (Estado, Fecha_Hora) VALUES ('Pendiente', '2026-06-12T21:00:00');

-- Participacion
INSERT INTO Participacion (Equipo, Partido, Local_O_Visitante) VALUES (5, 3, 'Local');
INSERT INTO Participacion (Equipo, Partido, Local_O_Visitante) VALUES (6, 3, 'Visitante');
INSERT INTO Participacion (Equipo, Partido, Local_O_Visitante) VALUES (7, 4, 'Local');
INSERT INTO Participacion (Equipo, Partido, Local_O_Visitante) VALUES (8, 4, 'Visitante');

-- Partido_Quiniela
INSERT INTO Partido_Quiniela (Partido, Quiniela) VALUES (3, 2);
INSERT INTO Partido_Quiniela (Partido, Quiniela) VALUES (4, 2);
