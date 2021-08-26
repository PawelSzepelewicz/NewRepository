SET REFERENTIAL_INTEGRITY FALSE;
truncate table USER_TOKEN;
truncate table USER_ROLES;
truncate table VERIFICATION_TOKEN;
truncate table ROLES;
truncate table USERS;
SET REFERENTIAL_INTEGRITY TRUE;
insert into ROLES(id,role_name) values (1, 'USER');
insert into ROLES(id,role_name) values (2, 'ADMIN');
insert into USERS(id, user_name, description, rating, email, password, enabled)
values (1, 'Admin', 'Developer', 2500, 'admin@gmail.com', '$2a$12$9jjZvojR3Bnf04b/9lrq0eRCsRp6pQFmJPaEDe3Ytq0MjYLschwJe', true);
insert into USER_ROLES(user_id, role_id) values (1,1);
insert into USER_ROLES(user_id, role_id) values (1,2);
insert into VERIFICATION_TOKEN(id, token, expiry_date)
values (1, '902a8cf0-63a7-444f-a424-8960907e7eeb', CAST(CURRENT_TIME AS TIMESTAMP) + INTERVAL '2' HOUR);
insert into USER_TOKEN(user_id, token_id)
values (1, 1);
insert into USERS(id, user_name, description, rating, email, password, enabled)
values (2, 'User', 'Client', 2515, 'client@gmail.com', '$2a$12$1rMB6IhSafSiSwIS2zMfJeG.FlUvG5AfbI0AuQ0/3pFlKT0FXuXlG', true);
insert into USER_ROLES(user_id, role_id) values (2,1);
insert into USERS(id, user_name, description, rating, email, password, enabled)
values (3, 'Next', 'Client', 2000, 'next@gmail.com', '$2a$12$1rMB6IhSafSiSwIS2zMfJeG.FlUvG5AfbI0AuQ0/3pFlKT0FXuXlG', true);
insert into USER_ROLES(user_id, role_id) values (3,1);
insert into USERS(id, user_name, description, rating, email, password, enabled)
values (4, 'NotVerified', 'Client', 2500, 'notVerirfied@gmail.com', '$2a$12$1rMB6IhSafSiSwIS2zMfJeG.FlUvG5AfbI0AuQ0/3pFlKT0FXuXlG', false);
insert into USER_ROLES(user_id, role_id) values (4,1);
