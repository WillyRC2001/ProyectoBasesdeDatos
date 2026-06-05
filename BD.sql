USE master;
GO

ALTER DATABASE QUINIELAS SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
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
GO

CREATE OR ALTER PROCEDURE PR_REGISTRO_USUARIO
    (
    @Nombre_Usuario VARCHAR(50),
    @Contrasena VARCHAR(50),
    @FechaNacimiento DATE,
    @Correo VARCHAR(50),
    @Nombre VARCHAR(50),
    @Segundo_Nombre VARCHAR(50),
    @Primer_Apellido VARCHAR(50),
    @Segundo_Apellido VARCHAR(50)
    )
    AS
BEGIN
    IF @Nombre_Usuario IN (SELECT Nombre_Usuario FROM Usuarios)
BEGIN
        RAISERROR('El nombre de usuario ya existe.', 16, 1);
        RETURN;
END

INSERT INTO Usuarios (Rol_Usuario, Nombre_Usuario, Contraseña, Fecha_Nacimiento)
VALUES ('Jugador', @Nombre_Usuario, @Contrasena, @FechaNacimiento);

DECLARE @ID_Usuario INT = SCOPE_IDENTITY();

INSERT INTO Correo_Electronico (ID_Usuario, Correo)
VALUES (@ID_Usuario, @Correo);

INSERT INTO Nombre_Completo (Usuario, Nombre, Segundo_Nombre, Primer_Apellido, Segundo_Apellido)
VALUES (@ID_Usuario, @Nombre, @Segundo_Nombre, @Primer_Apellido, @Segundo_Apellido);
END;
GO

CREATE OR ALTER PROCEDURE PR_AUTENTIFICACION_USUARIO
    (
    @Nombre_Usuario VARCHAR(50),
    @Contrasena VARCHAR(50)
    )
    AS
BEGIN
	DECLARE @ID_Usuario INT;
SELECT @ID_Usuario = U.ID_Usuario
FROM Usuarios U
WHERE U.Nombre_Usuario = @Nombre_Usuario AND U.Contraseña = @Contrasena;
IF @ID_Usuario IS NULL
BEGIN
		RAISERROR('Nombre de usuario o contraseña incorrectos.', 16, 1);
		RETURN;
END
SELECT 'Autenticación exitosa. Bienvenido, ' + @Nombre_Usuario AS Mensaje;
END;
GO

CREATE OR ALTER PROCEDURE PR_CONSEGUIR_ROL_USUARIO
    (
    @Nombre_Usuario VARCHAR(50)
    )
    AS
BEGIN
	DECLARE @Rol_Usuario VARCHAR(20);
SELECT @Rol_Usuario = Rol_Usuario
FROM Usuarios
WHERE Nombre_Usuario = @Nombre_Usuario;
IF @Rol_Usuario IS NULL
BEGIN
		RAISERROR('Nombre de usuario no existe.', 16, 1);
		RETURN;
END
SELECT @Rol_Usuario AS Rol_Usuario;
END;
GO

CREATE OR ALTER PROCEDURE PR_CREAR_QUINIELA
    (
    @Nombre_QUINIELA VARCHAR(50),
    @Descripcion VARCHAR(255),
    @Fecha_Inicio DATE,
    @Fecha_Fin DATE,
    @Modalidad VARCHAR(20),
    @Tipo_Puntuacion VARCHAR(20),
    @Reglas_Generales VARCHAR(255),
    @Rol_Usuario VARCHAR(20)
    )
    AS
BEGIN
    IF @Rol_Usuario != 'Administrador'
BEGIN
        RAISERROR('Solo los administradores pueden crear quinielas.', 16, 1);
        RETURN;
END

INSERT INTO Quiniela (Nombre_Quiniela, Descripcion, Fecha_Inicio, Fecha_Fin, Modalidad, Tipo_Puntuacion, Estado, Reglas_Generales)
VALUES (@Nombre_QUINIELA, @Descripcion, @Fecha_Inicio, @Fecha_Fin, @Modalidad, @Tipo_Puntuacion, 'Abierta', @Reglas_Generales);
END;
GO

CREATE OR ALTER PROCEDURE PR_INSCRIBIRSE_QUINIELA
    (
    @ID_Usuario INT,
    @ID_QUINIELA INT,
    @Acepta_Reglas VARCHAR(20)
    )
    AS
BEGIN
    IF @Acepta_Reglas != 'Sí'
BEGIN
        RAISERROR('Debes aceptar las reglas para inscribirte.', 16, 1);
        RETURN;
END
    IF @ID_QUINIELA NOT IN (SELECT ID_Quiniela FROM Quiniela WHERE Estado = 'Abierta')
BEGIN
        RAISERROR('La quiniela no está abierta o no existe.', 16, 1);
        RETURN;
END
    IF @ID_Usuario NOT IN (SELECT ID_Usuario FROM Usuarios)
BEGIN
        RAISERROR('El usuario no existe.', 16, 1);
        RETURN;
END;
IF EXISTS (
        SELECT 1
        FROM Inscripcion
        WHERE Usuario = @ID_Usuario
          AND Quiniela = @ID_QUINIELA
    )
BEGIN
        RAISERROR('Ya estás inscrito en esta quiniela.', 16, 1);
        RETURN;
END;

INSERT INTO Inscripcion (Usuario, Quiniela, Acepta_Reglas)
VALUES (@ID_Usuario, @ID_QUINIELA, @Acepta_Reglas);
END;
GO

CREATE OR ALTER PROCEDURE PR_UPDATE_ESTADO_QUINIELA
    (
    @ID_QUINIELA INT,
    @Rol_Usuario VARCHAR(20)
    )
    AS
BEGIN
    IF @Rol_Usuario != 'Administrador'
BEGIN
        RAISERROR('Solo los administradores pueden actualizar el estado.', 16, 1);
        RETURN;
END

    DECLARE @FechaActual DATE = GETDATE();
    DECLARE @FechaFin DATE;

SELECT @FechaFin = Fecha_Fin FROM Quiniela WHERE ID_Quiniela = @ID_QUINIELA;

IF @FechaActual > @FechaFin
BEGIN
UPDATE Quiniela SET Estado = 'Cerrada' WHERE ID_Quiniela = @ID_QUINIELA;
END
END;
GO

CREATE OR ALTER PROCEDURE PR_REGISTRAR_PARTIDO
    (
    @Fecha_Hora DATETIME,
    @Rol_Usuario VARCHAR(20)
    )
    AS
BEGIN
    IF @Rol_Usuario != 'Administrador'
BEGIN
        RAISERROR('Solo los administradores pueden registrar partidos.', 16, 1);
        RETURN;
END

INSERT INTO Partido (Fecha_Hora)
VALUES (@Fecha_Hora);
END;
GO

CREATE OR ALTER PROCEDURE PR_UPDATE_RESULTADOS_PARTIDO
    (
    @ID_Partido VARCHAR(20),
    @Goles_Local INT,
    @Goles_Visitante INT,
    @Rol_Usuario VARCHAR(20)
    )
    AS
BEGIN
	IF @Rol_Usuario != 'Administrador'
BEGIN
		RAISERROR('Solo los administradores pueden actualizar los resultados del partido.', 16, 1);
		RETURN;
END

	IF @ID_Partido NOT IN (SELECT Id_Partido FROM Partido)
BEGIN
		RAISERROR('El partido no existe.', 16, 1);
		RETURN;
END

	IF @Goles_Local < 0 OR @Goles_Visitante < 0
BEGIN
		RAISERROR('Los goles no pueden ser negativos.', 16, 1);
		RETURN;
END

UPDATE Partido
SET Goles_Local = @Goles_Local,
    Goles_Visitante = @Goles_Visitante,
    Estado = 'Finalizado'
WHERE Id_Partido = @ID_Partido
  AND Estado != 'Finalizado';

END;
GO

CREATE OR ALTER PROCEDURE PR_CREAR_PRONOSTICO
    (
    @ID_Partido INT,
    @ID_Usuario INT,
    @ID_QUINIELA INT,
    @Goles_Prediccion_Local INT,
    @Goles_Prediccion_Visitante INT
    )
    AS
BEGIN
    IF @ID_Partido NOT IN (SELECT Id_Partido FROM Partido)
BEGIN
        RAISERROR('El partido no existe.', 16, 1);
        RETURN;
END
    IF @ID_Usuario NOT IN (SELECT ID_Usuario FROM Usuarios)
BEGIN
        RAISERROR('El usuario no existe.', 16, 1);
        RETURN;
END
    IF @ID_QUINIELA NOT IN (SELECT ID_Quiniela FROM Quiniela)
BEGIN
        RAISERROR('La quiniela no existe.', 16, 1);
        RETURN;
END
    IF @ID_QUINIELA NOT IN (SELECT Quiniela FROM Inscripcion WHERE Usuario = @ID_Usuario)
BEGIN
        RAISERROR('El usuario no está inscrito en la quiniela.', 16, 1);
        RETURN;
END

INSERT INTO Pronostico (Partido, Usuario, Quiniela, Puntuacion, Goles_Prediccion_Local, Goles_Prediccion_Visitante, Fecha_Hora_Ingreso)
VALUES (@ID_Partido, @ID_Usuario, @ID_QUINIELA, NULL, @Goles_Prediccion_Local, @Goles_Prediccion_Visitante, GETDATE());
END;
GO

CREATE OR ALTER PROCEDURE PR_CALCULAR_PUNTUACION_PRONOSTICO
    (
    @Pronostico INT
    )
    AS
BEGIN
    DECLARE @Puntos_Obtenidos INT = 0;
    DECLARE @Gano_Local_Pronostico INT = 0; -- 1 si ganó el local, 0 si fue empate, -1 si ganó el visitante
    DECLARE @FechaActual DATE = GETDATE();
    DECLARE @ID_PUNTUACION INT;
    DECLARE @ID_Usuario INT;
    DECLARE @ID_QUINIELA INT;

    IF @Pronostico NOT IN (SELECT Id_Pronostico FROM Pronostico)
BEGIN
        RAISERROR('El pronóstico no existe.', 16, 1);
        RETURN;
END

SELECT @ID_Usuario = Usuario, @ID_QUINIELA = Quiniela
FROM Pronostico
WHERE Id_Pronostico = @Pronostico;

IF @ID_QUINIELA NOT IN (SELECT ID_Quiniela FROM Quiniela)
BEGIN
        RAISERROR('La quiniela no existe.', 16, 1);
        RETURN;
END
    IF @ID_Usuario NOT IN (SELECT ID_Usuario FROM Usuarios)
BEGIN
        RAISERROR('El usuario no existe.', 16, 1);
        RETURN;
END
    IF @ID_QUINIELA NOT IN (SELECT Quiniela FROM Inscripcion WHERE Usuario = @ID_Usuario)
BEGIN
        RAISERROR('El usuario no está inscrito en la quiniela.', 16, 1);
        RETURN;
END

    IF EXISTS (SELECT 1 FROM Puntuacion PU
               JOIN Pronostico PR ON PU.Id_Puntuacion = PR.Puntuacion
               WHERE PR.Id_Pronostico = @Pronostico)
BEGIN
        RAISERROR('La puntuación ya fue calculada.', 16, 1);
        RETURN;
END

SELECT @Gano_Local_Pronostico = CASE
                                    WHEN Goles_Prediccion_Local > Goles_Prediccion_Visitante THEN 1
                                    WHEN Goles_Prediccion_Local < Goles_Prediccion_Visitante THEN -1
                                    ELSE 0 END
FROM Pronostico
WHERE Id_Pronostico = @Pronostico;

SET @Puntos_Obtenidos = (
        SELECT CASE
            WHEN (Goles_Prediccion_Local = Goles_Local AND Goles_Prediccion_Visitante = Goles_Visitante) THEN 3
            WHEN (
                (@Gano_Local_Pronostico = 1  AND Goles_Local > Goles_Visitante) OR
                (@Gano_Local_Pronostico = -1 AND Goles_Local < Goles_Visitante) OR
                (@Gano_Local_Pronostico = 0  AND Goles_Local = Goles_Visitante)
            ) THEN 1
            ELSE 0 END
        FROM Pronostico PR
        JOIN Partido PA ON PR.Partido = PA.Id_Partido
        WHERE PR.Id_Pronostico = @Pronostico
    );

INSERT INTO Puntuacion (Puntos_Obtenidos, Fecha_Calculo)
VALUES (@Puntos_Obtenidos, @FechaActual);

SET @ID_PUNTUACION = SCOPE_IDENTITY();

UPDATE Pronostico
SET Puntuacion = @ID_PUNTUACION
WHERE Id_Pronostico = @Pronostico;
END;
GO

CREATE OR ALTER PROCEDURE CALCULO_PUNTUACION
    (
    @ID_Partido INT
    )
    AS
BEGIN
    DECLARE @ID_Pronostico INT;

    IF NOT EXISTS (SELECT 1 FROM Partido WHERE Id_Partido = @ID_Partido AND Estado = 'Finalizado')
BEGIN
        RAISERROR('El partido no ha finalizado o no existe.', 16, 1);
        RETURN;
END

    DECLARE Pronostico_Cursor CURSOR FOR
SELECT Id_Pronostico FROM Pronostico
WHERE Partido = @ID_Partido AND Puntuacion IS NULL;

OPEN Pronostico_Cursor;
FETCH NEXT FROM Pronostico_Cursor INTO @ID_Pronostico;

WHILE @@FETCH_STATUS = 0
BEGIN
BEGIN TRY
EXEC PR_CALCULAR_PUNTUACION_PRONOSTICO @ID_Pronostico;
END TRY
BEGIN CATCH
PRINT 'Error en pronóstico: ' + CAST(@ID_Pronostico AS VARCHAR);
END CATCH
FETCH NEXT FROM Pronostico_Cursor INTO @ID_Pronostico;
END

CLOSE Pronostico_Cursor;
DEALLOCATE Pronostico_Cursor;
END;
GO

CREATE OR ALTER TRIGGER TR_UPDATE_RESULTADOS_PARTIDO ON Partido
AFTER UPDATE
                          AS
BEGIN
    DECLARE @ID_Partido INT;
SELECT @ID_Partido = Id_Partido FROM inserted;
IF UPDATE(Estado) AND (SELECT Estado FROM inserted) = 'Finalizado'
BEGIN
EXEC CALCULO_PUNTUACION @ID_Partido;
END
END;
GO

CREATE OR ALTER VIEW VW_RANKING_QUINIELA
AS
SELECT
    Q.ID_Quiniela,
    Q.Nombre_Quiniela,
    U.ID_Usuario,
    U.Nombre_Usuario,
    SUM(PU.Puntos_Obtenidos) AS Puntaje_Total
FROM Quiniela Q
         JOIN Inscripcion I ON I.Quiniela = Q.ID_Quiniela
         JOIN Usuarios U ON U.ID_Usuario = I.Usuario
         JOIN Pronostico PR ON PR.Usuario = U.ID_Usuario AND PR.Quiniela = Q.ID_Quiniela
         JOIN Puntuacion PU ON PU.Id_Puntuacion = PR.Puntuacion
GROUP BY Q.ID_Quiniela, Q.Nombre_Quiniela, U.ID_Usuario, U.Nombre_Usuario;
GO

CREATE OR ALTER PROCEDURE PR_REGISTRAR_EQUIPO
    (
    @Pais VARCHAR(50),
    @Nombre_Equipo VARCHAR(50)
    )
    AS
BEGIN
    IF @Pais IS NULL OR @Nombre_Equipo IS NULL
BEGIN
        RAISERROR('El país y el nombre del equipo son obligatorios.', 16, 1);
        RETURN;
END
    IF EXISTS (SELECT 1 FROM Equipo WHERE Nombre_Equipo = @Nombre_Equipo AND Pais = @Pais)
BEGIN
        RAISERROR('El nombre del equipo ya existe para el país especificado.', 16, 1);
        RETURN;
END

INSERT INTO Equipo (Pais, Nombre_Equipo)
VALUES (@Pais, @Nombre_Equipo);
END;
GO

CREATE OR ALTER PROCEDURE PR_REGISTRAR_PARTICIPACION
    (
    @ID_Equipo INT,
    @ID_Partido INT,
    @Local_O_Visitante VARCHAR(20)
    )
    AS
BEGIN
    IF @ID_Equipo NOT IN (SELECT Id_Equipo FROM Equipo)
BEGIN
        RAISERROR('El equipo no existe.', 16, 1);
        RETURN;
END
    IF @ID_Partido NOT IN (SELECT Id_Partido FROM Partido)
BEGIN
        RAISERROR('El partido no existe.', 16, 1);
        RETURN;
END
    IF EXISTS (SELECT 1 FROM Participacion WHERE Partido = @ID_Partido AND Equipo = @ID_Equipo)
BEGIN
        RAISERROR('El equipo ya está registrado para este partido.', 16, 1);
        RETURN;
END
    IF @Local_O_Visitante NOT IN ('Local', 'Visitante')
BEGIN
        RAISERROR('El valor de Local_O_Visitante debe ser "Local" o "Visitante".', 16, 1);
        RETURN;
END
    IF EXISTS (SELECT 1 FROM Participacion WHERE Partido = @ID_Partido AND Local_O_Visitante = @Local_O_Visitante)
BEGIN
        RAISERROR('Ya hay un equipo registrado como ' + @Local_O_Visitante + ' para este partido.', 16, 1);
        RETURN;
END

INSERT INTO Participacion (Equipo, Partido, Local_O_Visitante)
VALUES (@ID_Equipo, @ID_Partido, @Local_O_Visitante);
END;

CREATE OR ALTER PROCEDURE PR_VINCULAR_PARTIDO_QUINIELA
    (
    @ID_Partido INT,
    @ID_Quiniela INT
    )
    AS
BEGIN
    IF @ID_Partido NOT IN (SELECT Id_Partido FROM Partido)
BEGIN
        RAISERROR('El partido no existe.', 16, 1);
        RETURN;
END
    IF @ID_Quiniela NOT IN (SELECT ID_Quiniela FROM Quiniela)
BEGIN
        RAISERROR('La quiniela no existe.', 16, 1);
        RETURN;
END
    IF EXISTS (SELECT 1 FROM Partido_Quiniela WHERE Partido = @ID_Partido AND Quiniela = @ID_Quiniela)
BEGIN
        RAISERROR('El partido ya está vinculado a la quiniela.', 16, 1);
        RETURN;
END

INSERT INTO Partido_Quiniela (Partido, Quiniela)
VALUES (@ID_Partido, @ID_Quiniela);
END;
GO

-- =============================================
-- 1. USUARIOS (Administrador y Jugadores)
-- =============================================
EXEC PR_REGISTRO_USUARIO
    'admin01', 'admin123', '1985-03-15',
    'admin@quiniela.com', 'Carlos', NULL, 'Mora', 'Jiménez';

EXEC PR_REGISTRO_USUARIO
    'jugador01', 'pass123', '1995-06-20',
    'jugador01@gmail.com', 'Daniel', NULL, 'Pérez', 'López';

EXEC PR_REGISTRO_USUARIO
    'jugador02', 'pass456', '1998-11-10',
    'jugador02@gmail.com', 'María', 'José', 'Rodríguez', 'Soto';

EXEC PR_REGISTRO_USUARIO
    'jugador03', 'pass789', '2000-01-25',
    'jugador03@gmail.com', 'Luis', NULL, 'Vargas', 'Castro';

-- El registro solo crea jugadores, el admin debe actualizarse manualmente
UPDATE Usuarios SET Rol_Usuario = 'Administrador' WHERE Nombre_Usuario = 'admin01';

-- =============================================
-- 2. PROBAR AUTENTICACION
-- =============================================
EXEC PR_AUTENTIFICACION_USUARIO 'admin01', 'admin123';        -- Exitosa
EXEC PR_AUTENTIFICACION_USUARIO 'jugador01', 'pass123';       -- Exitosa
EXEC PR_AUTENTIFICACION_USUARIO 'jugador01', 'wrongpass';     -- Error: contraseña incorrecta

-- =============================================
-- 3. PROBAR CONSEGUIR ROL
-- =============================================
EXEC PR_CONSEGUIR_ROL_USUARIO 'admin01';      -- Administrador
EXEC PR_CONSEGUIR_ROL_USUARIO 'jugador01';    -- Jugador
EXEC PR_CONSEGUIR_ROL_USUARIO 'noexiste';     -- Error: no existe

-- =============================================
-- 4. EQUIPOS (inserts directos, no hay procedimiento)
-- =============================================
EXEC PR_REGISTRAR_EQUIPO 'España', 'Real Madrid';
EXEC PR_REGISTRAR_EQUIPO 'Inglaterra', 'Manchester City';
EXEC PR_REGISTRAR_EQUIPO 'Alemania', 'Bayern Munich';
EXEC PR_REGISTRAR_EQUIPO 'Francia', 'Paris Saint-Germain';

-- =============================================
-- 5. CREAR QUINIELA
-- =============================================
EXEC PR_CREAR_QUINIELA
    'Champions 2026',
    'Quiniela de la Champions League 2026',
    '2026-06-01', '2026-06-30',
    'Pública', 'Estándar',
    'Se otorgan 3 puntos por marcador exacto y 1 por acertar el ganador.',
    'Administrador';

EXEC PR_CREAR_QUINIELA
    'Liga España',
    'Quiniela de La Liga española',
    '2026-06-01', '2026-06-15',
    'Privada', 'Estándar',
    'Se otorgan 3 puntos por marcador exacto y 1 por acertar el ganador.',
    'Jugador';    -- Error: solo administradores

-- =============================================
-- 6. INSCRIBIRSE EN QUINIELA
-- =============================================

-- Obtener el ID de la quiniela creada
DECLARE @ID_Q INT;
SELECT @ID_Q = ID_Quiniela FROM Quiniela WHERE Nombre_Quiniela = 'Champions 2026';

EXEC PR_INSCRIBIRSE_QUINIELA 1, @ID_Q, 'No';

DECLARE @ID_Usuario1 INT, @ID_Usuario2 INT, @ID_Usuario3 INT;
SELECT @ID_Usuario1 = ID_Usuario FROM Usuarios WHERE Nombre_Usuario = 'jugador01';
SELECT @ID_Usuario2 = ID_Usuario FROM Usuarios WHERE Nombre_Usuario = 'jugador02';
SELECT @ID_Usuario3 = ID_Usuario FROM Usuarios WHERE Nombre_Usuario = 'jugador03';

EXEC PR_INSCRIBIRSE_QUINIELA @ID_Usuario1, @ID_Q, 'Sí';
EXEC PR_INSCRIBIRSE_QUINIELA @ID_Usuario2, @ID_Q, 'Sí';
EXEC PR_INSCRIBIRSE_QUINIELA @ID_Usuario3, @ID_Q, 'Sí';
EXEC PR_INSCRIBIRSE_QUINIELA @ID_Usuario1, @ID_Q, 'Sí';  -- Error: ya inscrito

-- =============================================
-- 7. REGISTRAR PARTIDOS
-- =============================================
EXEC PR_REGISTRAR_PARTIDO '2026-06-10 20:00:00', 'Administrador';
EXEC PR_REGISTRAR_PARTIDO '2026-06-11 18:00:00', 'Administrador';
EXEC PR_REGISTRAR_PARTIDO '2026-06-12 21:00:00', 'Administrador';
EXEC PR_REGISTRAR_PARTIDO '2026-06-13 19:00:00', 'Jugador';  -- Error: solo administradores

-- =============================================
-- 8. PARTICIPACIONES (que equipo es local/visitante en cada partido)
-- =============================================
DECLARE @P1 INT, @P2 INT, @P3 INT;
SELECT @P1 = Id_Partido FROM Partido WHERE Fecha_Hora = '2026-06-10 20:00:00';
SELECT @P2 = Id_Partido FROM Partido WHERE Fecha_Hora = '2026-06-11 18:00:00';
SELECT @P3 = Id_Partido FROM Partido WHERE Fecha_Hora = '2026-06-12 21:00:00';

DECLARE @E1 INT, @E2 INT, @E3 INT, @E4 INT;
SELECT @E1 = Id_Equipo FROM Equipo WHERE Nombre_Equipo = 'Real Madrid';
SELECT @E2 = Id_Equipo FROM Equipo WHERE Nombre_Equipo = 'Manchester City';
SELECT @E3 = Id_Equipo FROM Equipo WHERE Nombre_Equipo = 'Bayern Munich';
SELECT @E4 = Id_Equipo FROM Equipo WHERE Nombre_Equipo = 'Paris Saint-Germain';

EXEC PR_REGISTRAR_PARTICIPACION @E1, @P1, 'Local';
EXEC PR_REGISTRAR_PARTICIPACION @E2, @P1, 'Visitante';
EXEC PR_REGISTRAR_PARTICIPACION @E2, @P2, 'Local';
EXEC PR_REGISTRAR_PARTICIPACION @E4, @P2, 'Visitante';
EXEC PR_REGISTRAR_PARTICIPACION @E4, @P3, 'Visitante';
EXEC PR_REGISTRAR_PARTICIPACION @E2, @P3, 'Local';

EXEC PR_REGISTRAR_PARTICIPACION @E3, @P2, 'Local';  -- Error: ya hay local

-- =============================================
-- 9. VINCULAR PARTIDOS A QUINIELA
-- =============================================
DECLARE @P1 INT, @P2 INT, @P3 INT;
DECLARE @ID_Q INT;

SELECT @ID_Q = ID_Quiniela FROM Quiniela WHERE Nombre_Quiniela = 'Champions 2026';
SELECT @P1 = Id_Partido FROM Partido WHERE Fecha_Hora = '2026-06-10 20:00:00';
SELECT @P2 = Id_Partido FROM Partido WHERE Fecha_Hora = '2026-06-11 18:00:00';
SELECT @P3 = Id_Partido FROM Partido WHERE Fecha_Hora = '2026-06-12 21:00:00';

EXEC PR_VINCULAR_PARTIDO_QUINIELA @P1, @ID_Q;
EXEC PR_VINCULAR_PARTIDO_QUINIELA @P2, @ID_Q;
EXEC PR_VINCULAR_PARTIDO_QUINIELA @P3, @ID_Q;

-- =============================================
-- 10. REGISTRAR PRONOSTICOS
-- =============================================
DECLARE @P1 INT, @P2 INT, @P3 INT;
DECLARE @ID_Q INT;
DECLARE @ID_Usuario1 INT, @ID_Usuario2 INT, @ID_Usuario3 INT;

SELECT @ID_Q       = ID_Quiniela FROM Quiniela WHERE Nombre_Quiniela = 'Champions 2026';
SELECT @P1         = Id_Partido  FROM Partido  WHERE Fecha_Hora      = '2026-06-10 20:00:00';
SELECT @P2         = Id_Partido  FROM Partido  WHERE Fecha_Hora      = '2026-06-11 18:00:00';
SELECT @P3         = Id_Partido  FROM Partido  WHERE Fecha_Hora      = '2026-06-12 21:00:00';
SELECT @ID_Usuario1 = ID_Usuario FROM Usuarios WHERE Nombre_Usuario  = 'jugador01';
SELECT @ID_Usuario2 = ID_Usuario FROM Usuarios WHERE Nombre_Usuario  = 'jugador02';
SELECT @ID_Usuario3 = ID_Usuario FROM Usuarios WHERE Nombre_Usuario  = 'jugador03';

-- jugador01: acertará marcador exacto en P1, ganador en P2, nada en P3
EXEC PR_CREAR_PRONOSTICO @P1, @ID_Usuario1, @ID_Q, 2, 1;
EXEC PR_CREAR_PRONOSTICO @P2, @ID_Usuario1, @ID_Q, 3, 1;
EXEC PR_CREAR_PRONOSTICO @P3, @ID_Usuario1, @ID_Q, 0, 3;

-- jugador02: acertará ganador en P1, marcador exacto en P2, nada en P3
EXEC PR_CREAR_PRONOSTICO @P1, @ID_Usuario2, @ID_Q, 3, 0;
EXEC PR_CREAR_PRONOSTICO @P2, @ID_Usuario2, @ID_Q, 2, 0;
EXEC PR_CREAR_PRONOSTICO @P3, @ID_Usuario2, @ID_Q, 1, 3;

-- jugador03: no acertará nada
EXEC PR_CREAR_PRONOSTICO @P1, @ID_Usuario3, @ID_Q, 0, 2;
EXEC PR_CREAR_PRONOSTICO @P2, @ID_Usuario3, @ID_Q, 1, 0;
EXEC PR_CREAR_PRONOSTICO @P3, @ID_Usuario3, @ID_Q, 2, 2;

-- =============================================
-- 11. ACTUALIZAR RESULTADOS (dispara el trigger)
-- =============================================
DECLARE @P1 INT, @P2 INT, @P3 INT;

SELECT @P1 = Id_Partido FROM Partido WHERE Fecha_Hora = '2026-06-10 20:00:00';
SELECT @P2 = Id_Partido FROM Partido WHERE Fecha_Hora = '2026-06-11 18:00:00';
SELECT @P3 = Id_Partido FROM Partido WHERE Fecha_Hora = '2026-06-12 21:00:00';

EXEC PR_UPDATE_RESULTADOS_PARTIDO @P1, 2, 1, 'Administrador';
EXEC PR_UPDATE_RESULTADOS_PARTIDO @P2, 2, 0, 'Administrador';
EXEC PR_UPDATE_RESULTADOS_PARTIDO @P3, 1, 0, 'Administrador';

EXEC PR_UPDATE_RESULTADOS_PARTIDO @P1, 3, 3, 'Administrador';  -- Error: ya finalizado
EXEC PR_UPDATE_RESULTADOS_PARTIDO @P1, -1, 0, 'Administrador'; -- Error: goles negativos

-- =============================================
-- 12. VERIFICAR PUNTUACIONES ESPERADAS
-- =============================================
SELECT * FROM Puntuacion;
SELECT * FROM Pronostico;

-- =============================================
-- 13. RANKING DE LA QUINIELA
-- =============================================
DECLARE @ID_Q INT;

SELECT @ID_Q = ID_Quiniela FROM Quiniela WHERE Nombre_Quiniela = 'Champions 2026';

SELECT * FROM VW_RANKING_QUINIELA
WHERE ID_Quiniela = @ID_Q
ORDER BY Puntaje_Total DESC;

-- =============================================
-- 14. PROBAR FN_UPDATE_ESTADO_QUINIELA
-- =============================================

-- Quiniela con fecha ya vencida para que cambie a Cerrada
EXEC PR_CREAR_QUINIELA
    'Quiniela Vencida', 'Para prueba de estado',
    '2026-01-01', '2026-01-31',
    'Pública', 'Estándar', 'Reglas de prueba', 'Administrador';

DECLARE @ID_Q2 INT;
SELECT @ID_Q2 = ID_Quiniela FROM Quiniela WHERE Nombre_Quiniela = 'Quiniela Vencida';

EXEC PR_UPDATE_ESTADO_QUINIELA @ID_Q2, 'Administrador';
EXEC PR_UPDATE_ESTADO_QUINIELA @ID_Q2, 'Jugador';
SELECT Estado FROM Quiniela WHERE ID_Quiniela = @ID_Q2;
DELETE FROM Quiniela WHERE Nombre_Quiniela = 'Quiniela Vencida';