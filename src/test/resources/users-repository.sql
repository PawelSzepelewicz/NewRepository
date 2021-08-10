insert into ROLES(id,role) values (1, 'USER');
insert into ROLES(id,role) values (2, 'ADMIN');
insert into USERS(id, user_name, description, rating, email, password, enabled)
values (1, 'Admin', 'Developer', 2500, 'admin@gmail.com', '$2a$12$9jjZvojR3Bnf04b/9lrq0eRCsRp6pQFmJPaEDe3Ytq0MjYLschwJe', true);
insert into USER_ROLES(user_id, role_id) values (1,1);
insert into USER_ROLES(user_id, role_id) values (1,2);
insert into USERS(id, user_name, description, rating, email, password, enabled)
values (2, 'User', 'Client', 2515, 'client@gmail.com', '$2a$12$1rMB6IhSafSiSwIS2zMfJeG.FlUvG5AfbI0AuQ0/3pFlKT0FXuXlG', true);
insert into USER_ROLES(user_id, role_id) values (2,1);
insert into USERS(id, user_name, description, rating, email, password, enabled)
values (3, 'Next', 'Client', 2000, 'next@gmail.com', '$2a$12$1rMB6IhSafSiSwIS2zMfJeG.FlUvG5AfbI0AuQ0/3pFlKT0FXuXlG', true);
insert into USER_ROLES(user_id, role_id) values (3,1);
insert into USERS(id, user_name, description, rating, email, password, enabled)
values (4, 'NotVerified', 'Client', 2500, 'notVerirfied@gmail.com', '$2a$12$1rMB6IhSafSiSwIS2zMfJeG.FlUvG5AfbI0AuQ0/3pFlKT0FXuXlG', false);
insert into USER_ROLES(user_id, role_id) values (4,1);
