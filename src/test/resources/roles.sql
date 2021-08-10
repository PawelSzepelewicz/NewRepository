insert into ROLES(id,role) values (1, 'USER');
insert into ROLES(id,role) values (2, 'ADMIN');
insert into USERS(id, user_name, description, rating, email, password, enabled)
values (1, 'Admin', 'Developer', 2500, 'admin@gmail.com', '@Admin0000', false);
insert into USER_ROLES(user_id, role_id) values (1,1);
insert into USER_ROLES(user_id, role_id) values (1,2);
insert into VERIFICATION_TOKEN(id, token, expiry_date)
values (1, '902a8cf0-63a7-444f-a424-8960907e7eeb', CAST(CURRENT_TIME AS TIMESTAMP) +  INTERVAL '2' HOUR);
insert into USER_TOKEN(user_id, token_id) values (1, 1);
