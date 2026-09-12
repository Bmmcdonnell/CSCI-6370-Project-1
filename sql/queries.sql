USE uni_in_class;

-- Query 1: List instructors, courses, and classrooms
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

-- Query 2: List students and their advisors
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
