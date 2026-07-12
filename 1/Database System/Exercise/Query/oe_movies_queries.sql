-- =============================================================================
--  oe_movies — SQL Queries
--  Questions are listed in oe_movies_queries.md
-- =============================================================================

USE oe_movies;
GO


-- ----------------------------------------------------------------------------
-- Q1. List all films with at least two actors
-- ----------------------------------------------------------------------------

SELECT
    m.MovieID,
    m.Title,
    COUNT(mp.PersonID) AS ActorCount
FROM Movies AS m
JOIN MoviePersons AS mp ON m.MovieID = mp.MovieID
JOIN Roles AS r         ON mp.RoleID = r.RoleID
WHERE r.RoleName = 'Actor'
GROUP BY m.MovieID, m.Title
HAVING COUNT(mp.PersonID) >= 2;


-- ----------------------------------------------------------------------------
-- Q2. List all directors who have directed exactly two films
-- ----------------------------------------------------------------------------

SELECT
    p.PersonID,
    p.FirstName,
    p.Surname,
    COUNT(mp.MovieID) AS FilmCount
FROM MoviePersons AS mp
JOIN Persons AS p ON mp.PersonID = p.PersonID
JOIN Roles   AS r ON mp.RoleID   = r.RoleID
WHERE r.RoleName = 'Director'
GROUP BY p.PersonID, p.FirstName, p.Surname
HAVING COUNT(mp.MovieID) = 2;


-- ----------------------------------------------------------------------------
-- Q3. List actors and how many films they appeared in (most first)
-- ----------------------------------------------------------------------------

SELECT
    p.PersonID,
    p.FirstName,
    p.Surname,
    COUNT(mp.MovieID) AS FilmCount
FROM MoviePersons AS mp
JOIN Persons AS p ON mp.PersonID = p.PersonID
JOIN Roles   AS r ON mp.RoleID   = r.RoleID
WHERE r.RoleName = 'Actor'
GROUP BY p.PersonID, p.FirstName, p.Surname
ORDER BY FilmCount DESC;


-- ----------------------------------------------------------------------------
-- Q4. List films ordered by number of reviews (highest first)
-- ----------------------------------------------------------------------------

SELECT
    m.MovieID,
    m.Title,
    COUNT(r.ReviewID) AS ReviewCount
FROM Movies AS m
LEFT JOIN Reviews AS r ON m.MovieID = r.MovieID
GROUP BY m.MovieID, m.Title
ORDER BY ReviewCount DESC;


-- ----------------------------------------------------------------------------
-- Q5. List films ordered by average rating (lowest first)
-- ----------------------------------------------------------------------------

SELECT
    m.MovieID,
    m.Title,
    AVG(r.Rating) AS AvgRating
FROM Movies AS m
JOIN Reviews AS r ON m.MovieID = r.MovieID
GROUP BY m.MovieID, m.Title
ORDER BY AvgRating ASC;


-- ----------------------------------------------------------------------------
-- Q6. List films where a person was both the director and an actor
-- ----------------------------------------------------------------------------

SELECT
    p.FirstName,
    p.Surname,
    m.Title,
    m.ReleaseDate
FROM MoviePersons AS dir
JOIN MoviePersons AS act
    ON  dir.MovieID  = act.MovieID
    AND dir.PersonID = act.PersonID
JOIN Persons AS p ON p.PersonID = dir.PersonID
JOIN Movies  AS m ON m.MovieID  = dir.MovieID
WHERE dir.RoleID = 1   -- Director
  AND act.RoleID = 4;  -- Actor


-- ----------------------------------------------------------------------------
-- Q7. List female actors ordered by date of birth
-- ----------------------------------------------------------------------------

SELECT
    p.PersonID,
    p.FirstName,
    p.Surname,
    p.BirthDate
FROM Persons      AS p
JOIN MoviePersons AS mp ON p.PersonID = mp.PersonID
JOIN Roles        AS r  ON mp.RoleID  = r.RoleID
WHERE p.Gender    = 'Female'
  AND r.RoleName  = 'Actor'
ORDER BY p.BirthDate;


-- ----------------------------------------------------------------------------
-- Q8. Calculate the age in years of every director
-- ----------------------------------------------------------------------------

SELECT DISTINCT
    p.PersonID,
    p.FirstName,
    p.Surname,
    p.BirthDate,
    DATEDIFF(YEAR, p.BirthDate, GETDATE())
        - CASE
            WHEN DATEADD(YEAR, DATEDIFF(YEAR, p.BirthDate, GETDATE()), p.BirthDate) > GETDATE()
            THEN 1 ELSE 0
          END AS AgeYears
FROM MoviePersons AS mp
JOIN Persons      AS p ON mp.PersonID = p.PersonID
JOIN Roles        AS r ON mp.RoleID   = r.RoleID
WHERE r.RoleName = 'Director';


-- ----------------------------------------------------------------------------
-- Q9. Top 3 actors who worked with the most different directors
-- ----------------------------------------------------------------------------

SELECT TOP 3
    act.PersonID AS ActorID,
    COUNT(DISTINCT dir.PersonID) AS DifferentDirectors
FROM MoviePersons AS dir
JOIN MoviePersons AS act
    ON  dir.MovieID  = act.MovieID
    AND dir.PersonID != act.PersonID
WHERE dir.RoleID = 1   -- Director
  AND act.RoleID = 4   -- Actor
GROUP BY act.PersonID
ORDER BY DifferentDirectors DESC;


-- ----------------------------------------------------------------------------
-- Q10. Top 6 films with the shortest title (ties broken by earliest release date)
-- ----------------------------------------------------------------------------

SELECT TOP 6
    m.Title,
    m.ReleaseDate,
    LEN(m.Title) AS TitleLength
FROM Movies AS m
ORDER BY TitleLength ASC, m.ReleaseDate ASC;


-- ----------------------------------------------------------------------------
-- Q11. Number of films per director released between 1990 and 2010
-- ----------------------------------------------------------------------------

SELECT
    p.PersonID,
    p.FirstName,
    p.Surname,
    COUNT(mp.MovieID) AS FilmCount
FROM Movies       AS m
JOIN MoviePersons AS mp ON mp.MovieID  = m.MovieID
JOIN Persons      AS p  ON p.PersonID  = mp.PersonID
JOIN Roles        AS r  ON mp.RoleID   = r.RoleID
WHERE r.RoleName     = 'Director'
  AND m.ReleaseDate  > '1990-01-01'
  AND m.ReleaseDate  < '2010-01-01'
GROUP BY p.PersonID, p.FirstName, p.Surname;


-- =============================================================================
-- END OF QUERIES
-- =============================================================================
