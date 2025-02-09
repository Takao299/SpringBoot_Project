DROP TABLE IF EXISTS admin_user CASCADE;
CREATE TABLE admin_user
(
	id 					SERIAL PRIMARY KEY,
	username 			TEXT NOT NULL,
	password 			TEXT,
	auth_admin_user 	INTEGER NOT NULL,
	auth_facility 		INTEGER NOT NULL,
	auth_member 		INTEGER NOT NULL,
	auth_reservation 	INTEGER NOT NULL,
	auth_business	 	INTEGER NOT NULL,
	version 			INTEGER,
	delete_date_time 	TIMESTAMP
);

DROP TABLE IF EXISTS member CASCADE;
CREATE TABLE member
(
	id 					SERIAL PRIMARY KEY,
	email 				TEXT NOT NULL,
	password 			TEXT,
	name 				TEXT,
	version 			INTEGER,
	delete_date_time 	TIMESTAMP
);

DROP TABLE IF EXISTS facility CASCADE;
CREATE TABLE facility
(
	id 					SERIAL PRIMARY KEY,
	name 				TEXT NOT NULL,
	amount 				INTEGER NOT NULL,
	memo				TEXT,
	version 			INTEGER,
	delete_date_time 	TIMESTAMP,
	delete_user			TEXT,
	update_date_time	TIMESTAMP,
	update_user			TEXT,
	create_date_time 	TIMESTAMP,
	create_user			TEXT
);

DROP TABLE IF EXISTS reservation CASCADE;
CREATE TABLE reservation
(
	id 					SERIAL PRIMARY KEY,
	member_id 			INTEGER NOT NULL,
	facility_id 		INTEGER NOT NULL,
	rday 				DATE NOT NULL,
	rstart 				TIME NOT NULL,
	rend 				TIME NOT NULL,
	delete_date_time 	TIMESTAMP
);

DROP TABLE IF EXISTS attached_file CASCADE;
CREATE TABLE attached_file
(
	id 					SERIAL PRIMARY KEY,
	foreign_id 			INTEGER,
	file_name 			TEXT,
	create_time 		TEXT,
	delete_pic 			TEXT,
	version 			INTEGER,
	delete_date_time 	TIMESTAMP
);

DROP TABLE IF EXISTS business_hour CASCADE;
CREATE TABLE business_hour
(
	id 					SERIAL PRIMARY KEY,
	day_name			TEXT,
	is_open				TEXT,
	open_time			INTEGER,
	close_time			INTEGER,
	update_date_time 	TIMESTAMP
);

DROP TABLE IF EXISTS temporary_business CASCADE;
CREATE TABLE temporary_business
(
	id 					SERIAL PRIMARY KEY,
	rday				DATE,
	is_open				TEXT,
	open_time			INTEGER,
	close_time			INTEGER,
	delete_date_time 	TIMESTAMP
);

DROP TABLE IF EXISTS permission_hour CASCADE;
CREATE TABLE permission_hour
(
	id 						SERIAL PRIMARY KEY,
	non_reservable_time		INTEGER,
	non_cancellable_time	INTEGER
);

DROP TABLE IF EXISTS session_id CASCADE;
CREATE TABLE session_id
(
	id 						SERIAL PRIMARY KEY,
	session_id 				TEXT,
	member_id 				INTEGER,
	active					BOOLEAN,
	expiration_date_time 	TIMESTAMP,
	create_date_time 		TIMESTAMP,
	issuer					TEXT
);

DROP TABLE IF EXISTS mail_code CASCADE;
CREATE TABLE mail_code
(
	id 					SERIAL PRIMARY KEY,
	email 				TEXT,
	session_id 			TEXT,
	code				TEXT,
	enabled				BOOLEAN
);