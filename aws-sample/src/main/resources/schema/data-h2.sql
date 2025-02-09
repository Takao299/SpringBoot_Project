INSERT INTO admin_user(username, password, auth_admin_user, auth_facility, auth_member, auth_reservation, auth_business, version)
VALUES('admin','$2a$10$LPLmeMElSBnkpkh49Yp90OnFuRki6BnMg3Gbdbf6qtYvh9F7rvvPq',0,0,0,0,0,0);

INSERT INTO member(email, password, name, version)
VALUES('member1@a.a','$2a$10$LPLmeMElSBnkpkh49Yp90OnFuRki6BnMg3Gbdbf6qtYvh9F7rvvPq','member1', 0);
INSERT INTO member(email, password, name, version)
VALUES('member2@a.a','$2a$10$LPLmeMElSBnkpkh49Yp90OnFuRki6BnMg3Gbdbf6qtYvh9F7rvvPq','member2', 0);
INSERT INTO member(email, password, name, version)
VALUES('member3@a.a','$2a$10$LPLmeMElSBnkpkh49Yp90OnFuRki6BnMg3Gbdbf6qtYvh9F7rvvPq','member3', 0);
INSERT INTO member(email, password, name, version)
VALUES('member4@a.a','$2a$10$LPLmeMElSBnkpkh49Yp90OnFuRki6BnMg3Gbdbf6qtYvh9F7rvvPq','member4', 0);
INSERT INTO member(email, password, name, version)
VALUES('member5@a.a','$2a$10$LPLmeMElSBnkpkh49Yp90OnFuRki6BnMg3Gbdbf6qtYvh9F7rvvPq','member5', 0);
INSERT INTO member(email, password, name, version)
VALUES('member6@a.a','$2a$10$LPLmeMElSBnkpkh49Yp90OnFuRki6BnMg3Gbdbf6qtYvh9F7rvvPq','member6', 0);
INSERT INTO member(email, password, name, version)
VALUES('member7@a.a','$2a$10$LPLmeMElSBnkpkh49Yp90OnFuRki6BnMg3Gbdbf6qtYvh9F7rvvPq','member7', 0);
INSERT INTO member(email, password, name, version)
VALUES('member8@a.a','$2a$10$LPLmeMElSBnkpkh49Yp90OnFuRki6BnMg3Gbdbf6qtYvh9F7rvvPq','member8', 0);
INSERT INTO member(email, password, name, version)
VALUES('member9@a.a','$2a$10$LPLmeMElSBnkpkh49Yp90OnFuRki6BnMg3Gbdbf6qtYvh9F7rvvPq','member9', 0);
INSERT INTO member(email, password, name, version)
VALUES('member10@a.a','$2a$10$LPLmeMElSBnkpkh49Yp90OnFuRki6BnMg3Gbdbf6qtYvh9F7rvvPq','member10', 0);
INSERT INTO member(email, password, name, version)
VALUES('member11@a.a','$2a$10$LPLmeMElSBnkpkh49Yp90OnFuRki6BnMg3Gbdbf6qtYvh9F7rvvPq','member11', 0);
INSERT INTO member(email, password, name, version)
VALUES('member12@a.a','$2a$10$LPLmeMElSBnkpkh49Yp90OnFuRki6BnMg3Gbdbf6qtYvh9F7rvvPq','member12', 0);
INSERT INTO member(email, password, name, version)
VALUES('a@a.a','$2a$10$ZFoLIywvwYA9cr3mCwyFtOqj60W4sFYDJ1HpgJaMDy5YODfqYYYBG','memberA', 0);

INSERT INTO facility(name, amount, version)
VALUES('facility1',1,0);
INSERT INTO facility(name, amount, version)
VALUES('facility2',1,0);
INSERT INTO facility(name, amount, version)
VALUES('facility3',1,0);
INSERT INTO facility(name, amount, memo, version)
VALUES('エアロバイク',2,'テスト文章　テスト',0);
INSERT INTO facility(name, amount, memo, version)
VALUES('ランニングマシン',3,'改行テスト
改行A
改行B1　B2',0);


INSERT INTO reservation(member_id, facility_id, rday, rstart, rend)
VALUES('1','1','2024-08-04','09:00','09:59');
INSERT INTO reservation(member_id, facility_id, rday, rstart, rend)
VALUES('1','1','2024-08-05','23:00','23:59');
INSERT INTO reservation(member_id, facility_id, rday, rstart, rend)
VALUES('1','1','2024-08-06','00:00','00:59');
INSERT INTO reservation(member_id, facility_id, rday, rstart, rend)
VALUES('1','1','2024-08-06','01:00','01:59');

INSERT INTO business_hour(day_name, is_open, open_time, close_time)
VALUES('MONDAY', 'on', 9, 18);
INSERT INTO business_hour(day_name, is_open, open_time, close_time)
VALUES('TUESDAY', 'on', 9, 18);
INSERT INTO business_hour(day_name, is_open, open_time, close_time)
VALUES('WEDNESDAY', 'on', 9, 18);
INSERT INTO business_hour(day_name, is_open, open_time, close_time)
VALUES('THURSDAY', 'on', 9, 18);
INSERT INTO business_hour(day_name, is_open, open_time, close_time)
VALUES('FRIDAY', 'on', 9, 18);
INSERT INTO business_hour(day_name, is_open, open_time, close_time)
VALUES('SATURDAY', null, 9, 18);
INSERT INTO business_hour(day_name, is_open, open_time, close_time)
VALUES('SUNDAY', null, 9, 18);

INSERT INTO temporary_business(rday, is_open, open_time, close_time)
VALUES('2024-08-24', 'on', 6, 12);
INSERT INTO temporary_business(rday, is_open, open_time, close_time)
VALUES('2024-08-25', 'on', 6, 12);
INSERT INTO temporary_business(rday, is_open, open_time, close_time)
VALUES('2024-08-26', null, 6, 12);
INSERT INTO temporary_business(rday, is_open, open_time, close_time)
VALUES('2024-08-27', 'on', 6, 12);

INSERT INTO permission_hour(non_reservable_time, non_cancellable_time)
VALUES(0, 0);
