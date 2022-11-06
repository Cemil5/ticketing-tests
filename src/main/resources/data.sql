-- INSERT INTO roles(description)
-- VALUES ('Admin'),
--        ('Manager'),
--        ('Employee');

INSERT INTO roles(insert_date_time, insert_user_id, last_update_date_time, last_update_user_id, description)
VALUES ('2022-03-13 02:15:00.000000', 1,'2022-03-13 02:15:00.000000',1,'Admin'),
       ('2022-03-13 02:15:00.000000', 1, '2022-03-13 02:15:00.000000',1, 'Manager'),
       ('2022-03-13 02:15:00.000000', 1,'2022-03-13 02:15:00.000000',1,'Employee');

-- INSERT INTO roles(insert_date_time, insert_user_id, last_update_date_time, last_update_user_id, description)
-- VALUES ('2022-03-13 02:15:00.000000-07:00', 1, false,'2022-03-13 02:15:00.000000-07:00','Admin'),
--        ('2022-03-13 02:15:00.000000-07:00', 1, false,'2022-03-13 02:15:00.000000-07:00','Manager'),
--        ('2022-03-13 02:15:00.000000-07:00', 1, false,'2022-03-13 02:15:00.000000-07:00','Employee');

-- from ozzy :
-- insert into roles(insert_date_time, insert_user_id, is_deleted, last_update_date_time, last_update_user_id, description)
-- VALUES ('2022-01-05 00:00:00', 1, false, '2022-01-05 00:00:00', 1, 'Admin'),
--        ('2022-01-05 00:00:00', 1, false, '2022-01-05 00:00:00', 1, 'Manager'),
--        ('2022-01-05 00:00:00', 1, false, '2022-01-05 00:00:00', 1, 'Employee');
--
--
-- insert into users(insert_date_time, insert_user_id, is_deleted, last_update_date_time, last_update_user_id, enabled,
--                   first_name, gender, last_name, user_name, role_id)
-- values ('2022-01-05 00:00:00', 1, false, '2022-01-05 00:00:00', 1, true, 'admin', 'MALE', 'admin', 'admin@admin.com',
--         1);