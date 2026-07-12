-- oe_movies Database Schema

CREATE DATABASE oe_movies;
GO

USE oe_movies;
GO

-- Roles lookup table
CREATE TABLE Roles (
    RoleID   TINYINT      NOT NULL PRIMARY KEY,
    RoleName NVARCHAR(50) NOT NULL
);

INSERT INTO Roles (RoleID, RoleName) VALUES
    (1, 'Director'),
    (2, 'Producer'),
    (3, 'Writer'),
    (4, 'Actor');

-- Movies
CREATE TABLE Movies (
    MovieID     INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    Title       NVARCHAR(255)     NOT NULL,
    ReleaseDate DATE              NOT NULL
);

-- Persons
CREATE TABLE Persons (
    PersonID  INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    FirstName NVARCHAR(100)     NOT NULL,
    Surname   NVARCHAR(100)     NOT NULL,
    Gender    NVARCHAR(10)      NOT NULL,
    BirthDate DATE              NULL
);

-- MoviePersons (junction table: links Movie, Person and their Role)
CREATE TABLE MoviePersons (
    MovieID  INT     NOT NULL,
    PersonID INT     NOT NULL,
    RoleID   TINYINT NOT NULL,

    PRIMARY KEY (MovieID, PersonID, RoleID),
    FOREIGN KEY (MovieID)  REFERENCES Movies  (MovieID),
    FOREIGN KEY (PersonID) REFERENCES Persons (PersonID),
    FOREIGN KEY (RoleID)   REFERENCES Roles   (RoleID)
);

-- Reviews
CREATE TABLE Reviews (
    ReviewID INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
    MovieID  INT           NOT NULL,
    Rating   DECIMAL(3,1)  NOT NULL,

    FOREIGN KEY (MovieID) REFERENCES Movies (MovieID)
);
