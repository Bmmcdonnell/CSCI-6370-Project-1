USE uni_in_class;

-- Query 1: List instructors, courses, and classrooms (Nash)
SELECT
    i.name AS instructor,
    c.title AS course,
    s.semester,
    s.year,
    s.building,
    s.room_number,
    cr.capacity
FROM instructor AS i
JOIN teaches AS t ON i.ID = t.ID
JOIN course AS c ON t.course_id = c.course_id
JOIN section AS s
    ON t.course_id = s.course_id
    AND t.sec_id = s.sec_id
    AND t.semester = s.semester
    AND t.year = s.year
JOIN classroom AS cr
    ON s.building = cr.building
    AND s.room_number = cr.room_number
LIMIT 50;

-- Query 2: List students and their advisors (Nash)
SELECT
    s.ID AS student_id,
    s.name AS student,
    s.dept_name AS student_department,
    s.tot_cred,
    COALESCE(i.name, 'No advisor assigned') AS advisor,
    i.dept_name AS advisor_department
FROM student AS s
LEFT JOIN advisor AS a ON s.ID = a.s_ID
LEFT JOIN instructor AS i ON a.i_ID = i.ID
ORDER BY s.dept_name, s.name
LIMIT 50;

-- Query 3: List students with 20 or more total credits (Brendan)
SELECT
    s.ID AS student_id,
    s.name AS student,
    s.dept_name AS department,
    s.tot_cred
FROM student AS s
WHERE s.tot_cred >= 20
ORDER BY s.tot_cred DESC, s.name
LIMIT 50;

-- Query 4: List advisors who advise students in the History department (Brendan)
SELECT
    i.ID AS advisor_id,
    i.name AS advisor,
    i.dept_name AS advisor_department,
    s.ID AS student_id,
    s.name AS student,
    s.dept_name AS student_department
FROM instructor AS i
JOIN advisor AS a ON i.ID = a.i_ID
JOIN student AS s ON a.s_ID = s.ID
WHERE s.dept_name = 'History'
ORDER BY i.name, s.name
LIMIT 50;

-- Query 5: List students, the courses they are taking, and their grades (Ido)
SELECT
    s.ID AS student_id,
    s.name AS student,
    c.title AS course,
    t.semester,
    t.year,
    t.grade
FROM student AS s
JOIN takes AS t ON s.ID = t.ID
JOIN course AS c ON t.course_id = c.course_id
ORDER BY s.name, t.year, t.semester
LIMIT 50;

-- Query 6: List instructors and the courses they teach, along with the offering department (Ido)
SELECT
    i.ID AS instructor_id,
    i.name AS instructor,
    c.title AS course,
    c.dept_name AS offering_department,
    c.credits
FROM instructor AS i
JOIN teaches AS t ON i.ID = t.ID
JOIN course AS c ON t.course_id = c.course_id
ORDER BY i.name, c.title
LIMIT 50;
